package com.jui.builders;

import com.st.DataFrame;

import lombok.Builder;

@Builder
public class TableAttributes {
	
	String caption;
	DataFrame df;
	int limit;
	
	
	public static TableAttributesBuilder builder() {
        return new CustomTableAttributesBuilder();
    }

    private static class CustomTableAttributesBuilder extends TableAttributesBuilder {
    	
        @Override
        public TableAttributes build() {
        	
        	
        	return super.build();
        	
        }
    }

}
