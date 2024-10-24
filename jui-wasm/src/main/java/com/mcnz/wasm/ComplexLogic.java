package com.mcnz.wasm;

//import static com.jui.JuiApp.jui;

import org.teavm.interop.Export;
import org.teavm.interop.Import;

public class ComplexLogic {
	
	public static void main(String[] argv) {
		
	}
	@Export(name = "getMagicNumber")
    public static void getMagicNumber(int range) {
		int magicNumber = (range/2) + range%3;
		setMagicNumber(magicNumber);
    }
	
	
	@Import(module = "env", name = "setMagicNumber")
    private static native void setMagicNumber(int message);

	/*
	@Export(name = "getJUIHtml")
    public static void getJUIHtml() {
		
		//jui.input.header("Input Example", "blue");
		String content = "eccolo!"; //jui.render().toJsonString(); 
		setHtml(content);
    }

	@Import(module = "env", name = "setHtml")
    private static native void setHtml(String content);
	 */
}
