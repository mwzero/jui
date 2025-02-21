package com.jui.toolkits;

public class ConsoleOutputListener implements OutputListener {

	@Override
	public void println(String line) {
		
		System.out.println(line);
	}

}
