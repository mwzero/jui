package com.st;

import java.util.LinkedHashMap;

public class LinkedMap {
	
	public static LinkedHashMap<Object, Object> of(Object...args) {
		
		LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
		
		
		for ( int i=0; i<args.length; i++) {
			map.put(args[i], args[++i]);
		}
		
		return map;
		
	}

}
