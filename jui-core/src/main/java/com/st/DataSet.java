package com.st;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class DataSet {
	
	protected Reader reader;
    protected String[] headers;
    protected List<String[]> data;
    
    public DataSet() {
        this.data = new ArrayList<>();
    }
    
    public DataSet(String[] headers, List<String[]> data) {
        this.headers = headers;
        this.data = data;
    }


	public void load() throws Exception {};

    public void show(int limit) {
    	
        for (String header : headers) {
            System.out.print(header + "\t");
        }
        System.out.println();

        int i=1;
        for (String[] row : data) {
            for (String value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
            if ( (limit != 0) && (i++ == limit ) )
            	break;
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

	public DataSet select(List<String> of) {
		
		DataSet ds = new DataSet();
		
		data.stream().forEach( row -> {
			
			ArrayList<String> newRow = new ArrayList<String>();
			
			IntStream.range(0, headers.length).forEach(idx -> {
			    if (of.contains(headers[idx])) {
			        newRow.add(row[idx]);
			    }
			});
			
			ds.data.add(newRow.toArray(new String[of.size()]));
		});
		
		ds.headers = of.toArray(String[]::new);
		
		return ds;
		
	}

	public DataSet limit(int limit) {
		
		DataSet ds = new DataSet();
		
		IntStream.range(0, limit).forEach(idx -> {
			ds.data.add(
					this.data.get(idx));
					
		});
		
		ds.headers = this.headers;
		
		return ds;
	}
}
