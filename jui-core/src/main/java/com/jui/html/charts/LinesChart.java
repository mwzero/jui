package com.jui.html.charts;

import com.jui.html.WebComponent;
import com.st.JuiDataFrame;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class LinesChart extends WebComponent {
	
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
		
		String seriesA = "";
		String seriesB = "";
		String xasis = "";
		
		for ( int irow=0; irow < data.getDf().rowCount(); irow++ ) {
			
			if ( xasis != "" ) {
				xasis+=",";
				seriesA+=",";
				seriesB+=",";
			} 
			
			xasis += "\"" +data.getDf().getObject(irow,0)  + "\"";
			seriesA += data.getDf().getObject(irow,1);
			seriesB += data.getDf().getObject(irow,2);

        }
		
		
		String js = """ 
				<script>
				var options = {
				  chart: {
					     type: "line",
				    	stacked: false
					  },
					  dataLabels: {
				    	enabled: false
				  	  },
					  series: [
						  {
						    name: 'serieA',
						    data: [%s]
						  },
						  {
						    name: 'serieB',
						    data: [%s]
						  }
					  ],
					  stroke: {
				    	width: [4, 4]
				  		},
				  	plotOptions: {
				    	bar: {
				      		columnWidth: "20%%"
				    	}
				  	},
					  xaxis: {
					    categories: [%s]
					  }
					}

					var chart = new ApexCharts(document.querySelector("#%s"), options);

					chart.render();
				</script>
				""".formatted(seriesA, seriesB, xasis, this.getKey());
		
		
		return html + js;

	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
}
