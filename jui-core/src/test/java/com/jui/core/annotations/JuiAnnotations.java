package com.jui.core.annotations;

import static com.jui.JuiCore.jui;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import com.jui.annotations.JUI;

public class JuiAnnotations {

	public class TestOne {
		
		@JUI(component="slider", min=1, max=10)
		public int slider;
		
		@JUI(component="slider", min=1, max=10)
		public int slider2;
		
	}
	
	@Test
	void AnnotationTest() throws IOException {
		
		TestOne one = new TestOne();
		one.slider = 10;
		one.slider2 = 5;
		
		jui.setTemplate("simple-bootstrap-1");
		jui.text.header("POJO Example", "blue");
    	
    	jui.write(one);
    	jui.input.submitbutton("Submit Data", null);

    	jui.startJuiServer();
    	
    }
	
	@AfterAll
	public static void tearDown() {
	    while (true) { try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    }
	}
}