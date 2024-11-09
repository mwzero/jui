package com.jui;

import java.util.LinkedHashMap;

import com.jui.JuiAppAttributes.JuiAppAttributesBuilder;
import com.jui.html.JuiContainer;
import com.jui.html.JuiHtmlRenderer;
import com.jui.html.WebComponent;
import com.jui.model.JuiContent;
import com.jui.net.JuiServer;
import com.jui.net.handlers.HandlerJuiRequest;
import com.jui.net.handlers.HandlerJuiWebSocket;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
@Getter
public class JuiApp extends JuiContainer {
	
	public static final JuiApp jui = new JuiApp();
	
	//used onServerAction response
	@Setter
	String juiResponse;
	
	public JuiContainer sidebar;
		
	//Attributes Builder
	JuiAppAttributesBuilder attrsBuilder;
	
	//
	JuiHtmlRenderer renderer;
	
	protected JuiApp() {
		
		super("core");

		log.info("JUI App: Start Initialization");
		renderer = new JuiHtmlRenderer();
		sidebar = new JuiContainer("sidebar");
	}
	
	public JuiContent render() {
		return renderer.process(this, sidebar);
	}

	public void start() {
		
		String rootDoc = "html/";
		Boolean classLoading = true;
		String host = "0.0.0.0";
		int port = 8080;
		
		if ( attrsBuilder != null ) {
			JuiAppAttributes attrs = attrsBuilder.build();
			if ( attrs.layout != null)
				rootDoc = "html-" + attrs.layout + "/";
		}
		
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

	public WebComponent executeServerAction(String id) throws Exception{
		
		WebComponent  component = this.context().getLinkedMapContext().get(id);
		if ( component != null )
			component.executeServerAction();
		
		return component;
		
	}

	public JuiAppAttributesBuilder set_page_config() {
		
		attrsBuilder =  JuiAppAttributes.builder();
		return attrsBuilder;
		
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
