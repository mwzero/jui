package com.jui;

import java.util.LinkedHashMap;
import java.util.Map;

import com.jui.html.JuiHtmlRenderer;
import com.jui.html.WebContainer;
import com.jui.html.WebElement;
import com.jui.model.JuiContent;
import com.jui.net.JuiServer;
import com.jui.net.handlers.HandlerJuiRequest;
import com.jui.net.handlers.HandlerJuiWebSocket;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
public class JuiApp extends WebContainer {
	
	public static final JuiApp jui = new JuiApp();
	
	@Setter
	@Getter
	String juiResponse;
	
	public WebContainer sidebar;

	public JuiAppSettings page;
	
	//
	JuiHtmlRenderer renderer;
	
	protected JuiApp() {
		
		super("core");
		
		log.info("JUI App: Start Initialization");
		renderer = new JuiHtmlRenderer();
		sidebar = new WebContainer("sidebar");
		page =  new JuiAppSettings();
	}
	
	public JuiContent render() {
			
		JuiContent content = new JuiContent();
		content.setMain( renderer.render(this));
		
		if ( sidebar.webContext().getLinkedMapContext() != null) {
			
			content.setSidebar(renderer.render(sidebar));
			
		} else
			content.setSidebar("");
		
		return content;
	}

	public void start() {
		
		String rootDoc = "html/";
		Boolean classLoading = true;
		String host = "0.0.0.0";
		int port = 8080;
		
		if ( page.layout != null)
			rootDoc = "html-" + page.layout + "/";
		
		this.start(rootDoc, classLoading, host, port);
	}
	
	protected void start(String docRoot, boolean classLoading, String host, int port) {
	
		JuiServer.builder()
			.juiHttpHandler(new HandlerJuiRequest())
			.juiWebSocketHandler(new HandlerJuiWebSocket())
			.classLoading(true)
			.docRoot(docRoot)
			.host(host)
			.port(port)
			.useWSS(true)
			.wssPort(8025)
		.build()
		.start();
	}

	public WebElement executeServerAction(String id, String action, Map<String, Object> payload) throws Exception{
		
		WebElement  component = this.webContext().getLinkedMapContext().get(id);
		if ( component != null ) {

			component.executeServerAction(action, payload);
			return component;
		}
		
		return null;
		
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
