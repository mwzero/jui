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
public class Table extends WebElement  {
	
	String caption;
	DataFrame df;
	
	List<String> styles;
	
    public Table() {
    	
    	super("Table");
    	styles = new ArrayList<>();
    }
    
    public void setStyles(String...args) {
    	
    	for (String arg : args) {
			this.styles.add(arg);
		}
    	
    }
}
