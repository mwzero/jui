package com.jui.html.elements;

import com.st.DataFrame;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DataViewParameters {
	
	String caption;
	DataFrame df;
	int limit;
	
	public static DataViewParametersBuilder builder() {
        return new CustomDataViewParametersBuilder();
    }

    private static class CustomDataViewParametersBuilder extends DataViewParametersBuilder {
    	
        @Override
        public DataViewParameters build() {
        	
        	return super.build();
        	
        }
    }

}
