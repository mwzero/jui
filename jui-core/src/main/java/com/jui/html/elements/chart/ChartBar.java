package com.jui.html.elements.chart;

import com.jui.html.WebElement;
import com.st.DataFrame;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
@Accessors(fluent = true)
@Getter
@Setter
public class ChartBar extends WebElement {
	
	

	DataFrame df;
	
	int max_height;
	int max_width;

	public ChartBar() {
		super("ChartBar");
	}
	
	@Override
	public String getHtml() {
		
		log.fine("Rendering chart lines");
		
		String html = """
				<div id="%s" style="max-width: %s; max_height=%s; display: inline-block">
				</div>		
				""".formatted(this.clientId(), max_width == 0 ? "100%" : max_width + "px", max_height + "px");
		
		String series = "";
		String xasis = "";
		
		//data.getDf().getColumns().get(0).getName()
		for ( int irow=0; irow < df.getDs().rowCount(); irow++ ) {
			
			if ( xasis != "" ) {
				xasis+=",";
				series+=",";
			} 

			xasis += "\"" + df.getDs().getObject(irow,0)  + "\"";
			series += df.getDs().getObject(irow,1);

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
				""".formatted(series, xasis, this.clientId());
		
		return html + js;

	}
}
