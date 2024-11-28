package com.jui.html.elements;

import com.jui.html.WebElement;

public class Slider extends WebElement {

    public Slider ( String label, int min, int max, int value) {
    	super("Slider");
    	this.label = label;
    	this.min = Integer.valueOf(min);
    	this.max = Integer.valueOf(max);
    	this.value = Integer.valueOf(value);
    }
    
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	//snippet
	/*
	public Integer getIntValue() {
		String callingClassName = new Exception().getStackTrace()[1].getClassName();
        System.out.println("La classe chiamante è: " + callingClassName);
		return value;
	}
	*/
	
	public Integer getIntValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	private String label;
	private Integer min;
    private Integer max;
    private Integer value;
    

    
    @Override
	public String getHtml() {
        
        return """
        		<div class="mb-3">
		    		<label for="%s" class="form-label">%s</label>
		    		<br>
		    		<input type="range" class="form-range form-control" id="%s" name="%s" min="%d" max="%d" value="%d">
        		</div>
				""".formatted(this.clientId(), label, this.clientId(), this.clientId(), min, max, value);
    }
}
