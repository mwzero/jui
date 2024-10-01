package com.st;


import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataFrame {
	
	private DataSet ds;
	
	public DataFrame(DataSet dataset) {
        this.ds = dataset;
    }

    public void load() throws Exception {
    	ds.load();
    }

    public void show() {
    	ds.show();
    }

    public DataFrame filter(int columnIndex, String value) {
    	
        DataFrame filtered = new DataFrame(ds);
        List<String[]> filteredData = new ArrayList<>();

        for (String[] row : ds.getData()) {
            if (row[columnIndex].equals(value)) {
                filteredData.add(row);
            }
        }

        filtered.ds.data = filteredData;  // Aggiorna i dati filtrati
        return filtered;
    }
}
