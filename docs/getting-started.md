# Getting Started with JUI

Adding JitPack.io Maven Repository to your pom.xml file:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Adding Jui dependency to your pom file:

```xml
<dependencies>

    <dependency>
        <groupId>com.github.mwzero.jui</groupId>
        <artifactId>jui-core</artifactId>
        <version>v0.0.9</version>
    </dependency>
</dependencies>
```

Write your first Jui application:

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

```

try to navigate to http://localhost:8000/index.html

<img src="https://raw.githubusercontent.com/mwzero/jui/main/assets/images/little-example.gif" width="300">

A complete list of examples [https://github.com/mwzero/jui/tree/main/jui-core/src/test/java/com/jui/api](JUI Github repository). 

