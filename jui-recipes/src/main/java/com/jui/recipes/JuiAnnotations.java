package com.jui.recipes;

import static com.jui.JuiApp.jui;

import com.jui.annotations.Jui;
import com.jui.annotations.JuiSlider;
import com.jui.annotations.JuiText;

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
		
		@Jui(key="slider1")
		@JuiSlider(min=1, max=10)
		int slider;
		
		@Jui(key="slider2")
		@JuiSlider(min=1, max=10)
		int slider2;
		
		
		@Jui(key="text1")
		@JuiText(text="esempio",input=true, readonly=false)
		int int1;
		
	}

	public static void main(String... args) {
		
		JuiAnnotations testAnnotations = new JuiAnnotations();
		
		jui.input.header("POJO Example", "blue");
		
    	jui.write(testAnnotations.buildTestOne(10, 5));
    	jui.input.submitbutton("Submit Data", null);

    	jui.start();
    	
    }
	
}