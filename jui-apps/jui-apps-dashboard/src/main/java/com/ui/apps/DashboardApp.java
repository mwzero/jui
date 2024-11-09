package com.ui.apps;

import static com.jui.JuiApp.jui;
import static com.st.ST.st;

import java.util.List;
import java.util.Map;

import com.st.DataFrame;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Log
public class DashboardApp {

    public static void main(String[] args) throws Exception {
    	
    	log.info("Starting Dashboard APP");
    	
    	jui.set_page_config().layout("sidebar");
    	
    	jui.markdown("""
    			# Dashboard: Covid 19
    			""");
    	jui.divider();
    	
    	st.setOptions(Map.of("classLoading", "true"));
    	
    	try {
    		DataFrame df = st
    					 .read_json("../../../../datasets/dpc-covid19-ita-province.zip")
    					.select(
    						List.of("data",
    						"denominazione_regione",
    						"denominazione_provincia",
    						"sigla_provincia",
    						"lat", "long",
    						"totale_casi"));

    		jui.table("Covid", df, 4);
    	
    	} catch ( Exception err) {
    		
    		log.severe(err.getLocalizedMessage());
    	}
    	
    	jui.start();
    	
    }
}
