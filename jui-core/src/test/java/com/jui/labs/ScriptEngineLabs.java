package com.jui.labs;

import java.io.FileReader;
import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptEngineLabs {

	public static void main(String[] args) {
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("javascript");

		try {
			
			engine.eval(new FileReader("path/to/your/file.js"));
			
		} catch (ScriptException | IOException ex) {
			ex.printStackTrace();
		}
	}
}
