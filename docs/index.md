# JUI: A Smart & Furious Toolkit

*JUI* is a toolkit designed to build and share web applications in pure Java, without requiring frontend development experience.  

Inspired by streamlit and folium.

It includes a simple HTTP/HTTPS/WSS server and a playground to try out its features.

# Key Features:
- Backend Development in Java: Build web applications using only Java.
- Integrated Server: Includes a simple HTTP/HTTPS/WSS server to handle requests.
- Playground: An interactive environment to test and experiment with the framework's features.

# Quickstart

```java
package com.jui.recipes;

import static com.jui.JuiApp.jui;

public class MapZoomer {
	
	public static void main(String... args) {
		
		jui.markdown("## Map Chart Example");
		jui.divider().color("blue");
    	
    	var slider = jui.slider("Zoom Level", 0, 19, 13);
    	var lat = jui.input("lat", "40.85631", "latitude");
    	var lng = jui.input("lng", "14.24641" ,"longitude");
    	
    	jui.map()
			.c_lat(lat)
			.c_lng(lng)
			.c_zoom(slider);
    	
    	jui.server().start();
	}

}
```

try to navigate to http://localhost:8000/index.html

<img src="https://raw.githubusercontent.com/mwzero/jui/main/assets/images/little-example.gif" width="300">

# Using the Playground
The JUI playground allows you to try out the framework's features in a controlled environment. To access the playground:

<img src="https://raw.githubusercontent.com/mwzero/jui/main/assets/images/playground.png" width="500px">
