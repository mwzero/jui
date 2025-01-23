package com.jui;

import java.util.LinkedHashMap;

import com.jui.html.WebContainer;
import com.jui.net.JuiServer;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
@Getter
@Setter
@Accessors(fluent = true)
public class JuiApp extends WebContainer {
	
	public static final JuiApp jui = new JuiApp();
	
	String juiResponse;
	
	WebContainer sidebar;

	JuiAppSettings page;
	
	JuiServer server;
	
	protected JuiApp() {
		
		super("core");
		
		log.info("JUI App: Start Initialization");
		sidebar = new WebContainer("sidebar");
		page =  new JuiAppSettings();
		server = new JuiServer(page);

	}
	
	public static <K, V> LinkedHashMap<K, V> linkedMapOf(K key1, V value1, Object... moreKeysAndValues) {
	    LinkedHashMap<K, V> map = new LinkedHashMap<>();
	    map.put(key1, value1);
	    for (int i = 0; i < moreKeysAndValues.length; i += 2) {
	        @SuppressWarnings("unchecked")
	        K key = (K) moreKeysAndValues[i];
	        @SuppressWarnings("unchecked")
	        V value = (V) moreKeysAndValues[i + 1];
	        map.put(key, value);
	    }
	    return map;
	}

}
