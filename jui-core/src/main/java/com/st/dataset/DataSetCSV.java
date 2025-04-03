package com.st.dataset;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class DataSetCSV extends DataSet {
	
    private String filePath;

    public DataSetCSV(String filePath) {
        this.filePath = filePath;
    }
    
    public DataSetCSV(Reader reader) {
        this.reader = reader;
    }

    @Override
    public void load() throws Exception  {
    	
    	if ( reader == null ) {
    		reader = new FileReader(filePath);
    	}
    	
    	Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
        this.headers = new ArrayList<>(records.iterator().next().toMap().keySet());

        for (CSVRecord record : records) {
            List<Object> row = new ArrayList<Object>();
            for (int i = 0; i < headers.size(); i++) {
                row.add(record.get(headers.get(i)));
            }
            data.add(row);
        }
    }
}

