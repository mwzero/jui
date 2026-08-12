package com.jui.apis;

import static com.jui.JuiApp.jui;

import java.util.List;

public class LayoutSideBarSimple {

	public static void main(String[] args)  {

		jui.sidebar().markdown("""
 				# App
 				
 				**Overview:** A collection of AI agents around e-mail
 				**About the Developer:** I'm Maurizio Farina. Let's connect on LinkedIn: https://www.linkedin.com/in/farinamaurizio/
 				**Source Code:** You can access the code on GitHub
 				""");
 		
 		jui.sidebar().dropDownButton("Settings", List.of("Profile", "Account Settings", "Logout"));
 		
		
		jui.markdown("""
        		# Sidebar Example
        		""");

        jui.divider();
        
        jui.button("Cliccami", "primary", "alert('Button clicked!')");
        
        jui.server()
        	.layout("sidebar").start();

	}
}
