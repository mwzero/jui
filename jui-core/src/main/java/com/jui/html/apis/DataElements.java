package com.jui.html.apis;

import com.jui.html.WebElementContext;
import com.jui.html.elements.Table;
import com.jui.html.elements.DataView;
import com.jui.html.elements.DataViewParameters;

import com.st.DataFrame;

public class DataElements extends BaseElements {

	public DataElements(WebElementContext context) {
		super(context);
	}

    public Table table(String caption, DataFrame df) {
		
		Table table = new Table();
		table.setDf(df);
		table.setCaption(caption);
		context.add(table);
		return table;
	}

	public Table table(String caption, DataFrame df, int limit) {
		
		Table table = new Table();
		table.setDf(df.limit(limit));
		table.setDf(df);
		table.setCaption(caption);
		
		context.add(table);
		return table;
	}

	// API for DataView supports */

	public DataView dataview(DataViewParameters params) {
		
		DataView dv = new DataView();
		dv.setCaption(params.getCaption());
		dv.setDf(params.getDf());
		context.add(dv);
		return dv;
	}
}
