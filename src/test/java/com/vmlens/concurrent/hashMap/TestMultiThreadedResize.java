package com.vmlens.concurrent.hashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.function.Function;

import org.junit.Test;

import com.vmlens.api.AllInterleavings;

public class TestMultiThreadedResize {

	/*
	 * final Object INSERT_AFTER_RESIZE = new Object();
		
		
		ConcurrentEqualsHashMap concurrentEqualsHashMap = new ConcurrentEqualsHashMap(2)
				{				
					@Override
					protected Object insertAfterResize(Object key, Function compute, int length) {
					
						return INSERT_AFTER_RESIZE;
					}

					@Override
					protected boolean resize(int length, int intervall) {
					   return true;
					}
				};
				
				assertNotSame(  INSERT_AFTER_RESIZE , 
				concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(1), (key) -> {
					return new Object();
				}));
				assertNotSame(  INSERT_AFTER_RESIZE ,  concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(2), (key) -> {
					return new Object();
				}));
				
				assertSame(  INSERT_AFTER_RESIZE , 
				concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(3), (key) -> {
					return new Object();
				}));		
	 */
	
	@Test
	public void testValueDuringResize() throws InterruptedException {

		try (AllInterleavings testUpdate = new AllInterleavings("TestMultiThreadedResize.testValueDuringResize");) {
			while (testUpdate.hasNext()) {

				ComputeIfAbsentHashMap concurrentEqualsHashMap = ComputeIfAbsentHashMap.object(2);
				
				concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(1), (key) -> {
					return new Object();
				});
				

				MutableValue firstValue = new MutableValue();
				MutableValue secondValue = new MutableValue();

				

				Thread first = new Thread(new Runnable() {
					@Override
					public void run() {

						firstValue.value = concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(2), (key) -> {
								return new Object();
							});
					}
				});

				Thread second = new Thread(new Runnable() {
					@Override
					public void run() {
						secondValue.value = concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(2), (key) -> {
							return new Object();
						});
					}
				});

				first.start();
				second.start();

				second.join();
				first.join();

				assertEquals(firstValue.value, secondValue.value);

			}
		}

	}
	
	
	@Test
	public void testValueBeforeResize() throws InterruptedException {

		try (AllInterleavings testUpdate = new AllInterleavings("TestMultiThreadedResize.testValueBeforeResize");) {
			while (testUpdate.hasNext()) {

				ComputeIfAbsentHashMap concurrentEqualsHashMap = ComputeIfAbsentHashMap.object(2);
				
				Object firstValue = concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(1), (key) -> {
					return new Object();
				});
				

				
				MutableValue secondValue = new MutableValue();

				

				Thread first = new Thread(new Runnable() {
					@Override
					public void run() {

						concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(2), (key) -> {
								return new Object();
							});
					}
				});

				Thread second = new Thread(new Runnable() {
					@Override
					public void run() {
						secondValue.value = concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(1), (key) -> {
							return new Object();
						});
					}
				});

				first.start();
				second.start();

				second.join();
				first.join();

				assertEquals(firstValue , secondValue.value);

			}
		}

	}

	
	



}
