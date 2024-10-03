package com.st;


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

    public void show(int limit) {
    	ds.show(limit);
    }

	public DataFrame select(List<String> of) {
		
		return new DataFrame(ds.select(of));
		
	}

	public DataFrame limit(int limit) {
		
		return new DataFrame(ds.limit(limit));
	}
}
