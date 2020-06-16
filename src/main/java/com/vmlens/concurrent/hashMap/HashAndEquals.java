package com.vmlens.concurrent.hashMap;

/**
 * 
 * Let you define how to keys are compared and how the hashCode of a key gets calculated.
 * 
 *
 */


public interface HashAndEquals {

	 /**
	  * calculates the hash code for a given key.
	  * 
	  *
	  */
	 int hashForKey(Object key);
	 
	 
	 /**
	  * 
	  *  Determines if the two given keys are equal.
	  *
	  *  @return Return true if the keys are equal false otherwise
	  *
	  */
	 boolean keyEquals(Object one , Object second);
	
}



