package com.jui.html.apis;

import com.jui.html.WebElementContext;
import com.jui.html.elements.Table;
import com.jui.html.elements.DataView;

import com.st.DataFrame;

public class DataElements extends BaseElements {

	public DataElements(WebElementContext context) {
		super(context);
	}

	public Table table(String caption) {
		
		Table table = new Table();
		table.caption(caption);
		context.add(table);
		return table;
	}

    public Table table(String caption, DataFrame df) {
		
		Table table = new Table();
		table.df(df);
		table.caption(caption);
		context.add(table);
		return table;
	}

	public Table table(String caption, DataFrame df, int limit) {
		
		Table table = new Table();
		table.df(df.limit(limit));
		table.caption(caption);
		
		context.add(table);
		return table;
	}

	// API for DataView supports */

	public DataView dataview() {
		
		DataView dv = new DataView();
		context.add(dv);
		return dv;
	}
}
