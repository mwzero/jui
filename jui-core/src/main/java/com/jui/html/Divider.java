package com.jui.html;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Divider extends WebComponent {
	
	String color;
	
    public Divider() {
    	this.setId("Divider");
    }
    public Divider(String color) {this.color= color;}
}
