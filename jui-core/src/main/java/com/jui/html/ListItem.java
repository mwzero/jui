package com.jui.html;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListItem {
	
	String icon;
	String label;
	String link;

	public ListItem(String label, String icon, String link) {
		this.label=label;
		this.icon = icon;
		this.link = link;
	}
}
