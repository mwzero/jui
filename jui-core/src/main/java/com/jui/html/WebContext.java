package com.jui.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.jui.helpers.TemplateHelper;

import lombok.Builder;

public class WebContext {
	
	//contains webcomponent 
	LinkedHashMap<String, WebComponent> context;
	
	
	public LinkedHashMap<String, ArrayList<String>> relations;
	public LinkedHashMap<String, String> elementPostData;
	int i; 
	
	@Builder
	public WebContext() {
	
		this.i=0;
		this.context = new LinkedHashMap<>();
		this.relations = new LinkedHashMap<>();
		this.elementPostData = new LinkedHashMap<>();
		
	}
	
	public WebComponent add(WebComponent component) {
		
		String uuid = "c"+(++i);
		component.setKey(uuid);
		this.context.put(uuid, component);
		
		String postDataElement = component.getPostData();
		if ( postDataElement != null)
			this.elementPostData.put(uuid, component.getPostData());
		
		return component;
	}
	
	public LinkedHashMap<String, WebComponent> getLinkedMapContext() { 
		if ( i>0 ) return context;
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


}
