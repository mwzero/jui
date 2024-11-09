package com.jui.html.tags;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnorderedListItem {
	
	String icon;
	String label;
	String link;
	String content;

	public UnorderedListItem(String label, String icon, String link, String content) {
		this.label=label;
		this.icon = icon;
		this.link = link;
		this.content = content;
	}
}
