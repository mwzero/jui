package com.st.dataset;

import java.io.FileReader;
import java.io.Reader;

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
        this.headers = records.iterator().next().toMap().keySet().toArray(new String[0]);

        for (CSVRecord record : records) {
            String[] row = new String[headers.length];
            for (int i = 0; i < headers.length; i++) {
                row[i] = record.get(headers[i]);
            }
            data.add(row);
        }
    }
}

