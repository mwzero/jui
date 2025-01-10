package com.jui.apis.layouts;

import static com.jui.JuiApp.jui;

import java.util.List;

import lombok.extern.java.Log;

@Log
public class SidebarLayout {

	public static void main(String[] args)  {

		jui.page.layout("sidebar");
		
		jui.sidebar.markdown("""
 				# App
 				
 				**Overview:** A collection of AI agents around e-mail
 				**About the Developer:** I'm Maurizio Farina. Let's connect on LinkedIn: https://www.linkedin.com/in/farinamaurizio/
 				**Source Code:** You can access the code on GitHub
 				""");
 		
 		jui.sidebar.dropDownButton("Settings", List.of("Profile", "Account Settings", "Logout"));
 		
		
		jui.markdown("""
        		# Sidebar Example
        		""");

        jui.divider();
        
        jui.button("Cliccami", "primary", "alert('Button clicked!')");
        
        jui.start();

	}
}
