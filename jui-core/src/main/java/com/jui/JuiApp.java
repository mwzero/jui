package com.jui;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jui.JuiAppAttributes.JuiAppAttributesBuilder;
import com.jui.helpers.TemplateHelper;
import com.jui.html.JuiContainer;
import com.jui.html.JuiContainerCol;
import com.jui.html.JuiContainerRow;
import com.jui.html.JuiHtmlRenderer;
import com.jui.html.WebComponent;
import com.jui.html.base.builders.InputBuilder;
import com.jui.html.base.tags.Button;
import com.jui.html.base.tags.Divider;

import com.jui.html.base.tags.Table;
import com.jui.html.base.tags.Text;
import com.jui.html.charts.builders.ChartBuilder;
import com.jui.model.JuiContent;
import com.jui.net.JuiServer;
import com.jui.net.http.JuiRequestHandler;
import com.jui.net.http.JuiWebSocketHandler;
import com.jui.utils.Utils;
import com.st.DataFrame;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
@Getter
public class JuiApp {
	
	public static final JuiApp jui = new JuiApp();
	
	//used onServerAction response
	@Setter
	String juiResponse;
	
	public JuiContainer main;
	public JuiContainer sidebar;
		
	// only to work over main container as default
	public ChartBuilder chart;
	public InputBuilder input;
	
	//Attributes Builder
	JuiAppAttributesBuilder attrsBuilder;
	
	//
	JuiHtmlRenderer renderer;
	
	protected JuiApp() {

		log.info("JUI App: Start Initialization");
		renderer = new JuiHtmlRenderer();
		main = new JuiContainer("main");
		sidebar = new JuiContainer("sidebar");
		
		chart = main.chart;
		input = main.input;
	}
	
	public JuiContainer addContainer(String key) {
		
		JuiContainerRow container = new JuiContainerRow(key);
		main.getContext().add(container);
		
		return container;
	}
	
	public Button button(String label, String type, String onClick, Runnable onServerSide) { 
		return (Button) this.input.button(label, type, onClick, onServerSide);}

	public void write(String... args) {
		this.main.write(args);
	}

	public void write(Object obj) {
		this.main.write(obj);
	}

	public Divider divider() {
		return this.main.divider();
	}
	
	public Text markdown(String... args) {
		return this.main.markdown(args);
	}

	public Table table(String caption, DataFrame df) {
		return this.main.table(caption, df, 0);
	}
	
	public Table table(String caption, DataFrame df, int limit) {
		return this.main.table(caption, df, limit);
	}

	public JuiContent render() {
		
		return renderer.process(main, sidebar);
		
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
			.juiHttpHandler(new JuiRequestHandler())
			.juiWebSocketHandler(new JuiWebSocketHandler())
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
		
		WebComponent  component = this.main.getContext().getLinkedMapContext().get(id);
		if ( component != null )
			component.executeServerAction();
		
		return component;
		
	}

	public JuiAppAttributesBuilder set_page_config() {
		
		attrsBuilder =  JuiAppAttributes.builder();
		return attrsBuilder;
		
	}
	
	public List<JuiContainerCol> columns(Map<String, Integer> of) {
		
		return this.main.columns(of);
	}

	public JuiContainer columns(String key) {
		
		return this.main.columns(key);
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
