package com.jui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jui.html.WebComponent;
import com.jui.templates.TemplateHelper;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebContext {
	
	TemplateHelper engine;
	
	LinkedHashMap<String, WebComponent> context;
	LinkedHashMap<String, ArrayList<String>> relations;
	public LinkedHashMap<String, String> elementPostData;
	int i; 
	
	@Builder
	public WebContext(TemplateHelper engine) {
	
		this.engine = engine;
		
		log.debug("Initializing new context");
		i=0;
		this.context = new LinkedHashMap<>();
		this.relations = new LinkedHashMap<>();
		this.elementPostData = new LinkedHashMap<>();
		
	}
	
	
	public WebComponent add(WebComponent component) {
		
		String uuid = "c"+(++i);
		component.setKey(uuid);
		component.setEngine(engine);
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
	
	public String elementMapping( ) throws JsonProcessingException {
		
		ObjectMapper objectMapper = new ObjectMapper();
	    
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		boolean notFirst = false;
		for ( String source : relations.keySet() ) {
			if ( notFirst) sb.append(",");
			sb.append("""
					{
						"source" :"%s", "commands": [%s]
					}
					""".formatted(source, objectMapper.writeValueAsString(relations.get(source))));
			notFirst = true;
		}
		sb.append("]");
		
		return sb.toString();
	}



}
