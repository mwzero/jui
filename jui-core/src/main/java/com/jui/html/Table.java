package com.jui.html;

import java.util.ArrayList;
import java.util.List;

import com.st.JuiDataFrame;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Table extends WebComponent {
	
	String caption;
	JuiDataFrame st;
	
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
