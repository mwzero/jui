package com.jui.html.charts;

import com.jui.html.WebComponent;
import com.st.JuiDataFrame;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class BarChart extends WebComponent {
	
	JuiDataFrame data;
	
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
		
		//data.getDf().getColumns().get(0).getName()
		for ( int irow=0; irow < data.getDf().rowCount(); irow++ ) {
			
			if ( xasis != "" ) {
				xasis+=",";
				series+=",";
			} 

			xasis += "\"" + data.getDf().getObject(irow,0)  + "\"";
			series += data.getDf().getObject(irow,1);

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
