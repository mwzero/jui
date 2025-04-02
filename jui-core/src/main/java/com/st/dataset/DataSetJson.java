package com.st.dataset;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DataSetJson extends DataSet {
	
    private String filePath;

    public DataSetJson(String filePath) {
        this.filePath = filePath;
        this.data = new ArrayList<>();
    }
    
    public DataSetJson(Reader reader) {
        this.reader = reader;
        this.data = new ArrayList<>();
    }

    @Override
    public void load() throws Exception {
    	
    	if ( reader == null ) {
    		reader = new FileReader(filePath);
    	}
    	
        JsonElement jsonElement = JsonParser.parseReader(reader);
        
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            for (JsonElement element : jsonArray) {
                if (element.isJsonObject()) {
                	
                	String[] row = parseElement(element);
                	data.add(row);
                    
                }
            }
        } else {
        	String[] row = parseElement(jsonElement);
        	data.add(row);
        }
    }
    
    protected String[] parseElement(JsonElement element ) {
    	
    	JsonObject json = element.getAsJsonObject();
    	String[] row = new String[json.keySet().size()];
    	headers = new String[json.keySet().size()];
    	
    	int i=0;
        for (String key : json.keySet()) {
        	
            JsonElement value = json.get(key);
            row[i] = value.toString();
            headers[i++] = key;
        }
        
        return row;
        
    }
}

