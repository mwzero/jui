package com.jui.html.tags;

import java.util.ArrayList;
import java.util.List;

import com.jui.html.WebComponent;
import com.st.DataFrame;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Table extends WebComponent  {
	
	String caption;
	DataFrame df;
	
	List<String> styles;
	
    public Table() {
    	
    	this.setId("Table");
    	styles = new ArrayList<>();
    }
    
    public void setStyles(String...args) {
    	
    	for (String arg : args) {
			this.styles.add(arg);
		}
    	
    }
}
