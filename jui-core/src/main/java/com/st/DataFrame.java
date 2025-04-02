package com.st;


import java.util.List;

import com.st.dataset.DataSet;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import lombok.extern.java.Log;

@Log
@Getter
@Setter
@Accessors(fluent = true)
public class DataFrame {
	
	@Delegate
	DataSet ds;
	
	public DataFrame(DataSet dataset) {
		this.ds = dataset;
    }

	public DataFrame select(List<String> of) {
		return new DataFrame(ds.select(of));
	}

	public DataFrame limit(int limit) {
		return new DataFrame(ds.limit(limit));
	}
}
