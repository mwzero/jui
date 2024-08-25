# Welcome to JUI
JUI is a lightweight framework for creating interactive web applications directly in Java with few simple commands.

Inspired by [streamlit](https://github.com/streamlit/streamlit) and [folium](https://github.com/python-visualization/folium).

JUI allows developers to create rich and dynamic user interfaces in just a few simple steps. With JUI, you can quickly turn your Java "scripts" into full-featured web applications without having to write HTML, CSS, or JavaScript.

Jui comes with a built-in HTTP server to run and serve applications.

# Installation

# Quickstart

## a little example

```java
public static void main(String... args) {
		
		jui.setTemplate("simple-bootstrap-1");
		jui.text.header("Map Chart Example", "blue");
    	
    	var slider = jui.input.slider("Zoom Level", 0, 19, 13);
    	var lat = jui.input.input("lat", "40.85631", "latitude");
    	var lng = jui.input.input("lng", "14.24641" ,"longitude");
    	
    	jui.chart.map(
    			MapBuilder.builder()
    				.c_lat(lat)
    				.c_lng(lng)
    				.c_zoom(slider).build()
    	);
    	
    	startJuiServer();
	}
```

Now run it!

![Example](/assets/images/little-example.jpg "JUI example")


# Get Inspired

# License
JUI is completely free and open-source and licensed under the (Apache 2.0)[https://www.apache.org/licenses/LICENSE-2.0] license.