package com.jui.apps;

import static com.jui.JuiApp.jui;
import static com.st.ST.st;

import java.util.List;
import java.util.Map;

import com.st.DataFrame;

import lombok.extern.java.Log;

@Log
public class DashboardApp {

    public static void main(String[] args) throws Exception {
    	
    	log.info("Starting Dashboard APP");
    	
    	jui.page().layout("sidebar");
    	
    	jui.markdown("""
    			# Dashboard: Covid 19
    			""");
    	jui.divider();
    	
    	st.setOptions(Map.of("classLoading", "true"));
    	
    	try {
    		DataFrame df = st
    					.read_json("https://raw.githubusercontent.com/pcm-dpc/COVID-19/refs/heads/master/dati-regioni/dpc-covid19-ita-regioni-20200224.csv")
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
    	
    	jui.server().start();
    	
    }
}
