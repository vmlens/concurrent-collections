package com.vmlens.concurrent.hashMap;

public class KeyWithHashCode1 {

	private final int value;
	
	
	public KeyWithHashCode1(int value) {
		super();
		this.value = value;
	}

	@Override
	public int hashCode() {
		
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyWithHashCode1 other = (KeyWithHashCode1) obj;
		if (value != other.value)
			return false;
		return true;
	}

	

	
	
	
	
}
