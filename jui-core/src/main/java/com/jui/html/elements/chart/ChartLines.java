package com.jui.html.elements.chart;

import com.jui.html.WebElement;
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
public class ChartLines extends WebElement {
	
	DataFrame df;
	
	int height;
	int width;
	
	public ChartLines() {
		super("ChartLines");
	}
	
	@Override
	public String getHtml() {
		
		log.fine("Rendering lines chart graph");
		
		String html = """
				<div id="%s" style="width: %s; height=%s; display: inline-block">
				</div>		
				""".formatted(this.clientId(), width == 0 ? "100%" : width + "px", height + "px");
		
		Object [] xasis = new Object [df.rowCount()];
		Object  series[][] = new Object [df.columnCount() - 1][df.rowCount()];
		
		for ( int irow=0; irow < df.rowCount(); irow++ ) {
			
			xasis[irow] = df.getObject(irow,0);

			for ( int icol =1; icol <df.columnCount(); icol ++) {
				series[icol-1][irow] = df.getObject(irow,icol);
			}
        }
		
		String jsSeries = "";
		for ( int icol =1; icol <df.columnCount(); icol ++) {

			if ( jsSeries.length() > 0 ) jsSeries +=",";

			jsSeries += """
					{
						name: '%s',
						data: [%s]
					}
				""".formatted(df.getColumnAt(icol), Utils.buildString (series[icol-1]));
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
				""".formatted(jsSeries, Utils.buildString (xasis), this.clientId());
		
		
		return html + js;

	}
}
