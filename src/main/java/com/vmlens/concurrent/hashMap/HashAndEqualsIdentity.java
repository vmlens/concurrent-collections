package com.vmlens.concurrent.hashMap;

class HashAndEqualsIdentity  implements HashAndEquals {

	public final int hashForKey(Object key)
	{
		return  System.identityHashCode(key);	
	}
	
	
	public final boolean keyEquals(Object one , Object second)
	{
		return one == second;
	}
	
	
}
