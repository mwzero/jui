package com.st.dataset;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class DataSet {
	
	protected Reader reader;
    protected List<String> headers;
    protected List<List<Object>> data;
    
    public DataSet() {

        this.data = new ArrayList<>();
		this.headers = new ArrayList<>();
    }
    
    public DataSet(String[] headers, List<List<Object>> data) {
        this.headers = Arrays.asList(headers);
        this.data = data;
    }


	public void addColumn(String string, List<Object> of) {
		this.headers.add(string);
		this.data.add(of);
    }


	public void load() throws Exception {};

    public String show(int limit) {
		
		StringBuilder result = new StringBuilder();
	
		for (String header : headers) {
			result.append(header).append("\t");
		}
		result.append("\n");
	
		int i = 1;
		for (List<Object> row : data) {
			for (Object value : row) {
				result.append(value).append("\t");
			}
			result.append("\n");
			if ((limit != 0) && (i++ == limit)) {
				break;
			}
		}
	
		return result.toString();
	}
	

    public List<List<Object>> getData() {
        return data;
    }

    public List<String> getHeaders() {
        return headers;
    }

	public int rowCount() {
		return data.size(); 
	}

	public int columnCount() {
		return headers.size();
	}

	public Object getObject(int irow, int i) {
		
		return data.get(irow).get(i);
	}

	public String getColumnAt(int icol) {
		
		return headers.get(icol);
	}

	public DataSet select(List<String> of) {
		
		DataSet ds = new DataSet();
		
		data.stream().forEach( row -> {
			
			List<Object> newRow = new ArrayList<Object>();
			
			IntStream.range(0, headers.size()).forEach(idx -> {
			    if (of.contains(headers.get(idx))) {
			        newRow.add(row.get(idx));
			    }
			});
			
			//ds.data.add(newRow.toArray(new String[of.size()]));
		});
		
		ds.headers = of;
		
		return ds;
		
	}

	public DataSet limit(int limit) {
		
		int newLimit = Math.min(this.data.size() , limit);
		DataSet ds = new DataSet();
		
		IntStream.range(0, newLimit).forEach(idx -> {
			ds.data.add(
					this.data.get(idx));
					
		});
		
		ds.headers = this.headers;
		
		return ds;
	}
}
