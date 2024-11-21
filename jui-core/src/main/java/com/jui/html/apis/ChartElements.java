package com.jui.html.apis;

import com.jui.helpers.MarkdownProcessor;
import com.jui.html.WebContext;
import com.jui.html.tags.Divider;
import com.jui.html.tags.Text;
import com.jui.html.tags.chart.ChartBar;
import com.jui.html.tags.chart.ChartLines;
import com.jui.html.tags.chart.ChartMap;

public class ChartElements extends BaseElements {
	
	public ChartElements(WebContext context) {
		
		super(context);
		
	}
	
	public ChartLines lines() {

		ChartLines lines = new ChartLines();
		context.add(lines);

		return lines;
		
	}
	
	public ChartBar bars() {
		
		ChartBar lines = new ChartBar();
		context.add(lines);
		
		return lines;
	}
	
	public ChartMap map() {
		
		ChartMap map = new ChartMap();
		context.add(map);
		
		return map;
	}
}
