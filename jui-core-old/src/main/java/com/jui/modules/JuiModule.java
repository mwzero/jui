package com.jui.modules;

import com.sun.net.httpserver.HttpServer;

public abstract class JuiModule {
	
	public abstract void initialize();
	public abstract void createContexts(HttpServer server);
}

