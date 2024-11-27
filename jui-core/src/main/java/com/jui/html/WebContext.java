package com.jui.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Builder;

public class WebContext {
	
	LinkedHashMap<String, WebComponent> context;
	
	public LinkedHashMap<String, ArrayList<String>> relations;
	
	public LinkedHashMap<String, String> elementPostData;
	
	private static AtomicInteger instanceCount = new AtomicInteger(0);
	
	@Builder
	public WebContext() {
	
		this.context = new LinkedHashMap<>();
		this.relations = new LinkedHashMap<>();
		this.elementPostData = new LinkedHashMap<>();
		
	}
	
	public LinkedHashMap<String, WebComponent> getLinkedMapContext() { 
		if ( instanceCount.get() >0 ) return context;
		else return null;
	}
	
	public void addRelations (String source, List<String> commands) {
		
		relations.put(source, new ArrayList<>(commands));
	}
	
	public void addRelations(String key, String command) {
		
		if ( relations.containsKey(key) ) 
			relations.get(key).add(command);
		else 
			relations.put(key, new ArrayList<String>(Arrays.asList(command)));
	}

	public WebComponent add(WebComponent component) {
		
		String uuid = "c"+ instanceCount.incrementAndGet();
		component.key(uuid);
		
		this.context.put(uuid, component);
		
		String postDataElement = component.getPostData();
		if ( postDataElement != null)
			this.elementPostData.put(uuid, component.getPostData());
		
		return component;
		
	}

}
