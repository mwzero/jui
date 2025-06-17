# Welcome to JUI
JUI is a lightweight framework for creating interactive web applications directly in Java with few simple commands.

Inspired by [streamlit](https://github.com/streamlit/streamlit) and [folium](https://github.com/python-visualization/folium).

JUI allows developers to create rich and dynamic user interfaces in just a few simple steps. With JUI, you can quickly turn your Java "scripts" into full-featured web applications without having to write HTML, CSS, or JavaScript.

Jui comes with a built-in HTTP server to run and serve applications.

## Installation

Download the latest JUI Framework release from [JUI GitHub Releases](https://github.com/mwzero/jui/releases) or if you prefer add the following dependency to your POM file:

Add jitpack as maven repository:

```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	<releases>
		<enabled>true</enabled>
	</releases>
	</repository>
</repositories>
```

Add Jui dependency:

```xml
<dependency>
  <groupId>com.jui</groupId>
  <artifactId>jui-core</artifactId>
  <version>v0.0.13</version>
</dependency>
```

Note: Publishing JUI to Maven central repository is on the roadmap; 

## Quickstart

### a little example

Write a new Java file "MapZoomerRecipe.java"

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
    	
    		jui.server()
			.start();
	}

}
```

Run the following command:

```sh
java -cp jui-0.0.1.jar MapZoomerRecipe.java
```

try to navigate to http://localhost:8000/index.html

<img src="https://raw.githubusercontent.com/mwzero/jui/main/assets/images/little-example.gif" width="300">

## Roadmap

* **GeoJSON & D3 Integration:**: Develop components for efficient geographic data processing and advanced visualizations.
* **Enhanced HTML Elements:**: Implement feature-rich and intuitive APIs to extend HTML element functionality.
* **Unified UI Consistency:**: Ensure consistent design and alignment across HTML, CSS, and JavaScript layouts.
* **Expanded Open Data Use Cases:**: Increase the number of practical applications that effectively utilize open data.

## License
JUI is fully free, open-source, and distributed under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).

## 📬 Contact

If you have any questions, feedback, or would like to get in touch, please feel free to reach out to us via email at [maurizio.farina@gmail.com](mailto:mauirizio.farina@gmail.com)
