package com.vmlens.concurrent.hashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import org.junit.Test;
import com.vmlens.api.AllInterleavings;


public class TestMultiThreadedComputeIfAbsent {

	
	@Test
	public void testCreateGetSingleKey() throws InterruptedException {

		try (AllInterleavings testUpdate = new AllInterleavings("TestMultiThreadedComputeIfAbsent.testCreateGetSingleKey");) {
			while (testUpdate.hasNext()) {

				ComputeIfAbsentHashMap concurrentEqualsHashMap =  ComputeIfAbsentHashMap.object(16);

				MutableValue firstValue = new MutableValue();
				MutableValue secondValue = new MutableValue();

				

				Thread first = new Thread(new Runnable() {
					@Override
					public void run() {

						firstValue.value = concurrentEqualsHashMap.computeIfAbsent("1", (key) -> {
								return new Object();
							});
					}
				});

				Thread second = new Thread(new Runnable() {
					@Override
					public void run() {
						secondValue.value = concurrentEqualsHashMap.computeIfAbsent("1", (key) -> {
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
	public void testTwoKeysSameHashCode() throws InterruptedException {

		try (AllInterleavings testUpdate = new AllInterleavings("TestMultiThreadedComputeIfAbsent.testTwoKeysSameHashCode");) {
			while (testUpdate.hasNext()) {

				ComputeIfAbsentHashMap concurrentEqualsHashMap =  ComputeIfAbsentHashMap.object(16);

				MutableValue firstValue = new MutableValue();
				MutableValue secondValue = new MutableValue();

				

				Thread first = new Thread(new Runnable() {
					@Override
					public void run() {

						firstValue.value = concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(5), (key) -> {
								return new Object();
							});
					}
				});

				Thread second = new Thread(new Runnable() {
					@Override
					public void run() {
						secondValue.value = concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(6), (key) -> {
							return new Object();
						});
					}
				});

				first.start();
				second.start();

				second.join();
				first.join();

				assertNotSame(firstValue.value, secondValue.value);

			}
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
