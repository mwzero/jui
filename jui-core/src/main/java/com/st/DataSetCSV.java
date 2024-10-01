package com.st;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataSetCSV extends DataSet {
	
    private String filePath;

    public DataSetCSV(String filePath) {
        this.filePath = filePath;
        this.data = new ArrayList<>();
    }

    @Override
    public void load() throws IOException {
        FileReader reader = new FileReader(filePath);
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

