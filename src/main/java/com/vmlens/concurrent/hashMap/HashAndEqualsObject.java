package com.vmlens.concurrent.hashMap;

class HashAndEqualsObject implements HashAndEquals {
	static final int HASH_BITS = 0x7fffffff;
	
	static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }
	
	public final int hashForKey(Object key)
	{
		return spread(key.hashCode());
	}
	
	
	public final boolean keyEquals(Object one , Object second)
	{
		return one == second || one.equals(second);
	}
	
	
}
