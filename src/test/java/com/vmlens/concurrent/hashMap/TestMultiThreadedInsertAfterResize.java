package com.vmlens.concurrent.hashMap;


import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import com.vmlens.api.AllInterleavings;

public class TestMultiThreadedInsertAfterResize {
	@Test
	public void testValueDuringResize() throws InterruptedException {

		try (AllInterleavings testUpdate = new AllInterleavings("TestMultiThreadedInsertAfterResize");) {
			while (testUpdate.hasNext()) {

				ComputeIfAbsentHashMap concurrentEqualsHashMap = ComputeIfAbsentHashMap.object(2);
				
				concurrentEqualsHashMap.computeIfAbsent(1, (key) -> {
					return new Object();
				});
				
				concurrentEqualsHashMap.computeIfAbsent(2, (key) -> {
					return new Object();
				});
				
				

				MutableValue firstValue = new MutableValue();
				MutableValue secondValue = new MutableValue();

				

				Thread first = new Thread(new Runnable() {
					@Override
					public void run() {

						firstValue.value = concurrentEqualsHashMap.computeIfAbsent(3, (key) -> {
								return new Object();
							});
					}
				});

				Thread second = new Thread(new Runnable() {
					@Override
					public void run() {
						secondValue.value = concurrentEqualsHashMap.computeIfAbsent(7, (key) -> {
							return new Object();
						});
					}
				});

				first.start();
				second.start();

				second.join();
				first.join();
				
				assertNotNull(firstValue.value );
				assertNotNull(secondValue.value);

				// hier noch prüfen das nichts verloren geht
				
				

			}
		}

	}
}
