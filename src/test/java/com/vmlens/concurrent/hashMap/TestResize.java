package com.vmlens.concurrent.hashMap;

import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class TestResize {
	
	@Test
	public void testValueBeforeResize() throws InterruptedException {

	

		ComputeIfAbsentHashMap concurrentEqualsHashMap = ComputeIfAbsentHashMap.object(2);
				
				Object firstValue = concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(1), (key) -> {
					return new Object();
				});
				
				concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(2), (key) -> {
					return new Object();
				});
				
				
				Object secondValue = concurrentEqualsHashMap.computeIfAbsent(new KeyWithHashCode1(1), (key) -> {
							return new Object();
						});
				

				assertEquals(firstValue , secondValue);

		
		}

	
}
