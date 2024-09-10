package com.jui.html;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListItem {
	
	String icon;
	String label;
	String link;
	String content;

	public ListItem(String label, String icon, String link, String content) {
		this.label=label;
		this.icon = icon;
		this.link = link;
		this.content = content;
	}
}
