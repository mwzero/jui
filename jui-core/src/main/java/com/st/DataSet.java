package com.st;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;

public abstract class DataSet {
	
    protected String[] headers;
    protected List<String[]> data;

    public abstract void load() throws Exception;

    public void show() {
        for (String header : headers) {
            System.out.print(header + "\t");
        }
        System.out.println();

        for (String[] row : data) {
            for (String value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
    }

    public List<String[]> getData() {
        return data;
    }

    public String[] getHeaders() {
        return headers;
    }

	public int rowCount() {
		return data.size(); 
	}

	public int columnCount() {
		return headers.length;
	}

	public Object getObject(int irow, int i) {
		
		return data.get(irow)[i];
	}

	public String getColumnAt(int icol) {
		
		return headers[icol];
	}
}
