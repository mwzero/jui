package com.jui.html.apis;

import com.jui.html.WebElementContext;
import com.jui.html.elements.chart.ChartBar;
import com.jui.html.elements.chart.ChartLines;
import com.jui.html.elements.chart.ChartMap;

public class ChartElements extends BaseElements {
	
	public ChartElements(WebElementContext context) {
		
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
