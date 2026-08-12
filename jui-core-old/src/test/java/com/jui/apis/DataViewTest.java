package com.jui.apis;

import static com.jui.JuiApp.jui;
import static com.st.ST.st;

public class DataViewTest {

	public static void main(String... args) throws Exception {
		
		jui.markdown("""
    			# DataView Examples
    			""");
    	jui.divider();
    	
    	jui.dataview()
            .caption("DataView Label")
            .df(st.read_csv("./csv/table_1.csv"));
    	
    	jui.server().start();
    	
    }
}
