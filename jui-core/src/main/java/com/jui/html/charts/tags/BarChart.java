package com.jui.html.charts.tags;

import com.jui.html.WebComponent;
import com.st.DataFrame;

import lombok.Builder;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Log
@Builder
public class BarChart extends WebComponent {
	
	DataFrame df;
	
	int max_height;
	int max_width;
	
	@Override
	public String getHtml() {
		
		log.fine("Rendering chart lines");
		
		String html = """
				<div id="%s" style="max-width: %s; max_height=%s; display: inline-block">
				</div>		
				""".formatted(this.getKey(), max_width == 0 ? "100%" : max_width + "px", max_height + "px");
		
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
				""".formatted(series, xasis, this.getKey());
		
		return html + js;

	}
	
	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
}
