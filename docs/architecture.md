# Architecture

<img src="https://raw.githubusercontent.com/mwzero/jui/main/assets/images/logical-architecture.png" width="500px">

## Components

- Web Application Context: stores all JUI Compoments added during development phase.
- JUI HTML Libraries: Java-based representations of HTML elements using HTML templates files
- HTTP/HTTPS/WSS Server
    - Manages communication between the Java backend and the HTML frontend.
	- Serves the rendered HTML interface to the user.
	- Supports WebSocket for real-time updates.


### JUI Simple Web Server
Basically, JUI includes a com.sun.net.httpserver.HttpServer adding different contexts:

- /jui: managed by JUI Http Handler to POST payload from HTML page to JUI 
- /ws/jui: used by JUI to push WSS messages to HTML page 
- /js, /css: managed by FIle Handler to load HTML resources

### index.html

The JUI page supports WebSocket. Basically, the page receives JUI html elements backend changes via WSS and executes the command specified by payload sent via socket.
Each JUI Html Elements knows excalty for each changes occured on its html elements which kind of commands to execute in browser environment.

Example:

JUI component send a WSS message containing the kind of changes and commands to execute. In this case a button send the command to disable the button.
```java
    command.append("""
        const button = document.getElementById("jui_btn_ctx_1_1");
		button.disabled = true;
		button.classList.add("disabled");
    """;
    this.backEndEvents().onServerUpdate(this, "change", command.toString());	
```

The frontend receives the socket message and executes the specified command internally. Each JUI HTML element is aware of its own HTML rendering and manages its changes accordingly.

```html
    ws.onmessage = function(event) {
    const notification = JSON.parse(event.data);
    eval(notification.command);
};
```


		

## Data Flow
	1.	Development: The developer adds JUI components in Java.
    2.	Server Processing: The web server delivers the HTML to the end user (index.html).
    3. Rendering: index.html POST init payload message to server to initialize JUI compoments added to web application context.
	4.	Real-time Updates: Changes to JUI components trigger updates via WebSocket (onChange), dynamically refreshing the interface.


# Events

## BackEnd Events

## FrontEnd Events