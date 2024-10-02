package com.ui.apps.mail;

import static com.jui.JuiApp.jui;
import static com.st.ST.st;

import java.util.Map;

import com.st.DataFrame;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DashboardApp {

    public static void main(String[] args) throws Exception {
    	
    	log.info("Starting Dashboard APP");
    	
    	jui.set_page_config().rootDoc("sidebar");
    	
    	jui.markdown("""
    			# Dashboard: Covid 19
    			""");
    	jui.divider();
    	
    	st.setOptions(Map.of("classLoading", "true"));
    	
    	try {
    		/*
    		DataFrame df = st.read_json("../../../../datasets/dpc-covid19-ita-province.zip");
    		DataFrame df2 = df.select(
    				List.of("data",
    						"denominazione_regione",
    						"denominazione_provincia",
    						"sigla_provincia",
    						"lat", "long",
    						"totale_casi"));
    		*/

    		DataFrame df2 = st.read_csv_string("""
    				data, denominazione_regione, denominazione_provincia, sigla_provincia, lat, long, totale_casi
    				a,b,c,d,e,f,g
    				a,b,c,d,e,f,g
    				a,b,c,d,e,f,g
    				a,b,c,d,e,f,g
    				a,b,c,d,e,f,g
    				a,b,c,d,e,f,g
    				a,b,c,d,e,f,g
    				a,b,c,d,e,f,g
    				""");
    		
    		jui.table("Covid", df2, 4);
    	
    	} catch ( Exception err) {
    		
    		log.error(err.getLocalizedMessage());
    	}
    	
    	jui.start();
    	
    }
}
