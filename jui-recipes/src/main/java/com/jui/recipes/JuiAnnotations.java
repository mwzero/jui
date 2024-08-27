package com.jui.recipes;

import static com.jui.JuiCore.jui;
import com.jui.annotations.JUI;

public class JuiAnnotations {

	TestOne testOne;
	
	public TestOne buildTestOne(int slider, int slider2) {
        TestOne one = new TestOne(slider, slider2);
        return one;
    }
	
	class TestOne {
		
		public TestOne(int slider, int slider2) {
			this.slider = slider;
			this.slider2 = slider2;
		}
		
		@JUI(component="slider", min=1, max=10)
		int slider;
		
		@JUI(component="slider", min=1, max=10)
		int slider2;
		
	}

	public static void main(String... args) {
		
		JuiAnnotations testAnnotations = new JuiAnnotations();
		
		jui.setTemplate("simple-bootstrap-1");
		jui.text.header("POJO Example", "blue");
    	
    	jui.write(testAnnotations.buildTestOne(10, 5));
    	jui.input.submitbutton("Submit Data", null);

    	jui.startJuiServer();
    	
    }
	
}