package com.vmlens.concurrent.hashMap;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;

/**
 * 
 * A concurrent hash table which is optimized for computeIfAbsent.
 * 
 * 
 * 
 */

public class ComputeIfAbsentHashMap<K, V> {

	private static final int MAXIMUM_CAPACITY = 1 << 30;
	private static final Object MOVED_NULL_KEY = new Object();
	private static final KeyValue MOVED_KEY_VALUE = new KeyValue(MOVED_NULL_KEY, null);

	private static final VarHandle ARRAY = MethodHandles.arrayElementVarHandle(KeyValue[].class);

	/*
	 * package visibility for tests
	 */
	volatile KeyValue[] currentArray;
	private boolean resizeRunning;

	private final HashAndEquals hashAndEquals;
	private final int resizeAtPowerOfTwo = 8;
	private final int newMinLength = (int) Math.pow(2, resizeAtPowerOfTwo);
	private final Object resizeLock = new Object();

	/**
	 * 
	 * Creates a new ComputeIfAbsentHashMap, using == to compare two keys and
	 * System.identityHashCode to create the hash codes for the keys.
	 * 
	 * @param initialCapacity
	 *            The initial capacity of the map.
	 */

	public static ComputeIfAbsentHashMap identity(int initialCapacity) {
		return new ComputeIfAbsentHashMap(initialCapacity, new HashAndEqualsIdentity());
	}

	/**
	 * 
	 * Creates a new ComputeIfAbsentHashMap, using object.equals to compare two keys
	 * and object.hashCode to create the hash codes for the keys.
	 * 
	 * @param initialCapacity
	 *            The initial capacity of the map.
	 */

	public static ComputeIfAbsentHashMap object(int initialCapacity) {
		return new ComputeIfAbsentHashMap(initialCapacity, new HashAndEqualsObject());
	}

	/**
	 * 
	 * Constructs a new ComputeIfAbsentHashMap. The keys are compared using the
	 * provided hashAndEquals.
	 * 
	 * @param initialCapacity
	 *            The initial capacity of the map.
	 * @param hashAndEquals
	 *            Used to compare two keys and to create the hash code for the keys.
	 */

	public ComputeIfAbsentHashMap(int initialCapacity, HashAndEquals hashAndEquals) {
		super();
		int mod2Size = tableSizeFor(initialCapacity);
		currentArray = new KeyValue[mod2Size];
		this.hashAndEquals = hashAndEquals;

	}

	/**
	 * If the specified key is not already associated with a value, attempts to
	 * compute its value using the given mapping function and enters it into this
	 * map unless null. The function is NOT guaranteed to be applied once atomically
	 * only if the value is not present.
	 *
	 */

	public V computeIfAbsent(K key, Function<? super K, ? extends V> compute) {
		KeyValue[] local = currentArray;
		int hashCode = hashAndEquals.hashForKey(key);
		// the array position is given by hashCode modulo array size. Since
		// the array size is a power of two, we can use & instead of %.
		int index = (local.length - 1) & hashCode;
		int iterations = 0;
		KeyValue created = null;
		KeyValue current = tabAt(local, index);
		// fast path for reading
		if (current != null) {
			if (hashAndEquals.keyEquals(current.key, key)) {
				return (V) current.value;
			} else if (current.key == MOVED_NULL_KEY) {
				return (V) insertDuringResize(key, compute);
			}
		}
		while (true) {
			if (current == null) {
				if (created == null) {
					created = new KeyValue(key, compute.apply(key));
				}
				// use compareAndSet to set the array element if it is null
				if (casTabAt(local, index, created)) {
					if (((iterations) << resizeAtPowerOfTwo) > local.length) {
						resize(local.length, iterations);
					}
					// if successful we have inserted a new value
					return (V) created.value;
				}
				// if not we need to check if the other key is the same
				// as our key
				current = tabAt(local, index);
				if (hashAndEquals.keyEquals(current.key, key)) {
					return (V) current.value;
				} else if (current.key == MOVED_NULL_KEY) {
					return (V) insertDuringResize(key, compute);
				}
			}
			index++;
			iterations++;
			if (index == local.length) {
				index = 0;
			}
			if ((iterations << resizeAtPowerOfTwo) > local.length) {
				resize(local.length, iterations);
				return computeIfAbsent(key, compute);
			}
			current = tabAt(local, index);
			if (current != null) {
				if (hashAndEquals.keyEquals(current.key, key)) {
					return (V) current.value;
				} else if (current.key == MOVED_NULL_KEY) {
					return (V) insertDuringResize(key, compute);
				}
			}
		}
	}

	private static final KeyValue tabAt(KeyValue[] tab, int i) {
		return (KeyValue) ARRAY.getVolatile(tab, i);
	}

	private static final boolean casTabAt(KeyValue[] tab, int i, KeyValue newValue) {
		return ARRAY.compareAndSet(tab, i, null, newValue);
	}

	/**
	 * 
	 * Copied from java.util.concurrent.ConcurrentHashMap:
	 * 
	 * Returns a power of two table size for the given desired capacity. See Hackers
	 * Delight, sec 3.2
	 * 
	 */
	private static final int tableSizeFor(int c) {
		int n = c - 1;
		n |= n >>> 1;
		n |= n >>> 2;
		n |= n >>> 4;
		n |= n >>> 8;
		n |= n >>> 16;
		return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
	}

	private Object insertDuringResize(K key, Function<? super K, ? extends V> compute) {
		synchronized (resizeLock) {
			while (resizeRunning) {
				try {
					resizeLock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
					return null;
				}
			}
		}

		return computeIfAbsent(key, compute);
	}

	private void resize(int checkedLength, int intervall) {
		synchronized (resizeLock) {
			if (currentArray.length > checkedLength) {
				return;
			}
			resizeRunning = true;
			// Set all null values in the current array to the special value
			for (int i = 0; i < currentArray.length; i++) {
				if (tabAt(currentArray, i) == null) {
					casTabAt(currentArray, i, MOVED_KEY_VALUE);
				}
			}
			int arrayLength = Math.max(currentArray.length * 2, tableSizeFor(intervall * newMinLength + 2));
			// Create a new array
			KeyValue[] newArray = new KeyValue[arrayLength];
			// Copy all values from the current array to the new array
			for (int i = 0; i < currentArray.length; i++) {
				KeyValue current = tabAt(currentArray, i);
				if (current != MOVED_KEY_VALUE) {
					int hashCode = hashAndEquals.hashForKey(current.key);
					int index = (newArray.length - 1) & hashCode;
					while (newArray[index] != null) {
						index++;
						if (index == newArray.length) {
							index = 0;
						}
					}
					newArray[index] = current;
				}
			}
			// Set the current array to the new array
			currentArray = newArray;
			resizeRunning = false;
			resizeLock.notifyAll();
		}
	}
}
