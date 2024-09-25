package com.jui.html;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.st.JuiDataFrame;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Table extends WebComponent implements Iterable<String[]> {
	
	String caption;
	JuiDataFrame df;
	
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

	@Override
	public Iterator<String[]> iterator() {
		
		 return new Iterator<String[]>() {
	            private int index = 0;

	            @Override
	            public boolean hasNext() {
	                return index < df.getDf().rowCount();
	            }

	            @Override
	            public String[] next() {
	            	
	            	String[] row = new String[df.getDf().columnCount()];

	            	for (int columnIndex = 0; columnIndex < df.getDf().columnCount(); columnIndex++)
	            	{
	                        row[columnIndex] = df.getDf().getValueAsStringLiteral(index, columnIndex);
	            	}
	            	index++;
	            	
	            	return row;
	            }
	        };
	}
}
