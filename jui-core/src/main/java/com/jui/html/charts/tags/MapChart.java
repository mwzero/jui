package com.jui.html.charts.tags;

import com.jui.html.WebComponent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapChart extends WebComponent {
	
	private double lat;
	private double lng;
	private Integer zoom;
	
	//private int width;
	//private int height;
	
	
	@Override
	public String getPostData() {
		
		return "postData%s();".formatted(this.getKey());
	}
	
}
