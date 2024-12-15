package com.jui.apis.layouts;

import static com.jui.JuiApp.jui;
import static com.jui.JuiApp.linkedMapOf;

import java.util.List;

import lombok.extern.java.Log;

@Log
public class DefaultLayout {

	public static void main(String[] args)  {

		jui.markdown("""
        		# Main Content
        		Triggering server method from html *button*
        		""");

        jui.divider().color("blue");
        
        jui.button("Click Me!!", "primary", "alert('Button clicked!')");
        
        jui.container("One").markdown("""
        		# Container One
        		""");
        
        jui.container("Two").markdown("""
        		# Container Two
        		""");
        
        jui.columns(linkedMapOf("left", 8, "right", 4));
        
        jui.columns("left").markdown("""
        		# Container Left
        		""");
        
        jui.columns("right").markdown("""
        		# Container Right
        		""");
        
        jui.dialog("modal1", "title modal").markdown("""
        		# Container Modal
        		""");
        
        jui.expander("exp1", "Expander").markdown("""
        		# Container Exapander
        		""");
        
        jui.popover("pop1", "title popover").markdown("""
        		# Container Popover
        		""");
        
        jui.tabs(List.of("tab1", "tab2", "tab3"));
        jui.tabs("tab1").markdown("""
        		# Container Tab1
        		""");
        
        jui.tabs("tab2").markdown("""
        		# Container Tab2
        		""");
        
        jui.tabs("tab3").markdown("""
        		# Container Tab3
        		""");
        
        jui.start();

	}
}
