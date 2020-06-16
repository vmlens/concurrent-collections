package com.vmlens.concurrent.hashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.function.Function;

import org.junit.Test;

import com.vmlens.concurrent.hashMap.ComputeIfAbsentHashMap;

public class TestConcurrentHashMap {

	@Test
	public void testCreateGetSingleKey() {
		ComputeIfAbsentHashMap concurrentEqualsHashMap = ComputeIfAbsentHashMap.object(16);

		Object first = concurrentEqualsHashMap.computeIfAbsent("1", (key) -> {
			return new Object();
		});
		Object second = concurrentEqualsHashMap.computeIfAbsent("1", (key) -> {
			return new Object();
		});

		assertEquals(first, second);

	}

	@Test
	public void testSameHashCode() {
		ComputeIfAbsentHashMap concurrentEqualsHashMap =   ComputeIfAbsentHashMap.object(4096);

		Object first = concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(1), (key) -> {
			return new Object();
		});
		Object second = concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(2), (key) -> {
			return new Object();
		});
		Object third = concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(3), (key) -> {
			return new Object();
		});

		assertNotSame(first, second);
		assertNotSame(first, third);
		assertNotSame(second, third);

		assertEquals(first, concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(1), (key) -> {
			return new Object();
		}));

	}

//	@Test
//	public void testResize() {
//		ComputeIfAbsentHashMap concurrentEqualsHashMap =  ComputeIfAbsentHashMap.object(2);
//
//		assertEquals(2, concurrentEqualsHashMap.currentArray.length);
//
//		concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(1), (key) -> {
//			return new Object();
//		});
//		concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(2), (key) -> {
//			return new Object();
//		});
//		concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(3), (key) -> {
//			return new Object();
//		});
//
//		assertEquals(16, concurrentEqualsHashMap.currentArray.length);
//
//	}
	
	
	
	

}
