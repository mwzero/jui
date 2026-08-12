package com.jui.html.elements;

import com.st.DataFrame;

import lombok.Builder;

@Builder
public class TableParameters {
	
	String caption;
	DataFrame df;
	int limit;
	
	
	public static TableParametersBuilder builder() {
        return new CustomTableAttributesBuilder();
    }

    private static class CustomTableAttributesBuilder extends TableParametersBuilder {
    	
        @Override
        public TableParameters build() {
        	
        	
        	return super.build();
        	
        }
    }

}
