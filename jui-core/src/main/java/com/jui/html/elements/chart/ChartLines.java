package com.jui.html.elements.chart;

import com.jui.html.WebComponent;
import com.jui.utils.Utils;
import com.st.DataFrame;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
@Accessors(fluent = true)
@Getter
@Setter
public class ChartLines extends WebComponent {
	
	DataFrame df;
	
	int height;
	int width;
	
	@Override
	public String getHtml() {
		
		log.fine("Rendering lines chart graph");
		
		String html = """
				<div id="%s" style="width: %s; height=%s; display: inline-block">
				</div>		
				""".formatted(this.getKey(), width == 0 ? "100%" : width + "px", height + "px");
		
		Object [] xasis = new Object [df.getDs().rowCount()];
		Object  series[][] = new Object [df.getDs().columnCount() - 1][df.getDs().rowCount()];
		
		for ( int irow=0; irow < df.getDs().rowCount(); irow++ ) {
			
			xasis[irow] = df.getDs().getObject(irow,0);

			for ( int icol =1; icol <df.getDs().columnCount(); icol ++) {
				series[icol-1][irow] = df.getDs().getObject(irow,icol);
			}
        }
		
		String jsSeries = "";
		for ( int icol =1; icol <df.getDs().columnCount(); icol ++) {

			if ( jsSeries.length() > 0 ) jsSeries +=",";

			jsSeries += """
					{
						name: '%s',
						data: [%s]
					}
				""".formatted(df.getDs().getColumnAt(icol), Utils.buildString (series[icol-1]));
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
						  %s
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
				""".formatted(jsSeries, Utils.buildString (xasis), this.getKey());
		
		
		return html + js;

	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
}
