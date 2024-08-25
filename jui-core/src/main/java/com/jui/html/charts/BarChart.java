package com.jui.html.charts;

import java.util.ArrayList;
import java.util.List;

import com.jui.html.WebComponent;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class BarChart extends WebComponent {
	
	List<List<String>> data = new ArrayList<>();
	int max_height;
	int max_width;
	
	@Override
	public String render() {
		
		log.debug("Rendering chart lines");
		
		String html = """
				<div id="%s" style="max-width: %s; max_height=%s; display: inline-block">
				</div>		
				""".formatted(this.getKey(), max_width == 0 ? "100%" : max_width + "px", max_height + "px");
		
		String series = "";
		String xasis = "";
		
		for ( var item : data ) {
			
			if ( xasis != "" ) {
				xasis+=",";
				series+=",";
			} 
			
			xasis += "\"" + item.get(0)  + "\"";
			series += item.get(1);
		}
		
		String js = """ 
				<script>
				var options = {
				  chart: {
					    type: 'bar'
					  },
					  series: [{
					    name: 'sales',
					    data: [%s]
					  }],
					  xaxis: {
					    categories: [%s]
					  }
					}

					var chart = new ApexCharts(document.querySelector("#%s"), options);

					chart.render();
				</script>
				""".formatted(series, xasis, this.getKey());
		
		return html + js;

	}
	
	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
}
