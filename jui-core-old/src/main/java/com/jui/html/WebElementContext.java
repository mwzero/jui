package com.jui.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Builder;

public class WebElementContext {
	
	LinkedHashMap<String, WebElement> childrens;
	
	public LinkedHashMap<String, ArrayList<String>> relations;
	
	public LinkedHashMap<String, String> elementPostData;
	
	private static AtomicInteger instanceCount = new AtomicInteger(0);
	
	@Builder
	public WebElementContext() {
	
		this.childrens = new LinkedHashMap<>();
		this.relations = new LinkedHashMap<>();
		this.elementPostData = new LinkedHashMap<>();
		
	}
	
	public LinkedHashMap<String, WebElement> getLinkedMapContext() { 
		if ( instanceCount.get() >0 ) return childrens;
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

	public WebElement add(WebElement child) {
		
		String uuid = "c"+ instanceCount.incrementAndGet();
		child.clientId(uuid);
		
		this.childrens.put(uuid, child);
		
		String postDataElement = child.getPostData();
		if ( postDataElement != null)
			this.elementPostData.put(uuid, child.getPostData());
		
		return child;
		
	}

}
