package com.jui.modules;

import lombok.extern.java.Log;

@Log
public abstract class Authenticator extends JuiModule {
    	
	@Override
	public void initialize() {
		
		log.fine("Initialize Authenticator Module");
	}
	
}
