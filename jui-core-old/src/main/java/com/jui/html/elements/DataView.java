package com.jui.html.elements;

import java.util.ArrayList;
import java.util.List;

import com.jui.html.WebElement;
import com.st.DataFrame;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class DataView extends WebElement  {
	
	String caption;
	DataFrame df;
	int limit;
	
	List<String> styles;
	
    public DataView() {
    	
    	super("DataView");
    	styles = new ArrayList<>();
    }
    
    public void setStyles(String...args) {
    	
    	for (String arg : args) {
			this.styles.add(arg);
		}
    	
    }
}
