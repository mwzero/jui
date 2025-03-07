# Architecture

<img src="https://raw.githubusercontent.com/mwzero/jui/main/assets/images/logical-architecture.png" width="500px">

## Communication Flow

1.  **Frontend -> Static Resources (/js, /css):**
    * The frontend requests JavaScript and CSS files from the `/js` and `/css` endpoints.

2.  **Frontend -> Backend (POST /jui):**
    * Once the `index.html` page is loaded, the frontend sends a POST request to the `/jui` endpoint with a data payload to initialize the JUI Web Application.

3.  **JUI Rendering and Response:**
    * The JUI renders all JUI HTML Elements added to the Web Application Context during development.
    * The server returns all generated HTML and scripts to the frontend as response to a /jui post request. 

4.  **Backend -> Frontend (WebSocket /ws/jui):**
    * The frontend establishes a WebSocket connection to the `/ws/jui` endpoint.
    * Every time a JUI Component changes its state, the backend sends messages to the frontend via the WebSocket connection.

## Components

- HTTP/HTTPS/WSS Server: manages communication between the Java backend and the HTML frontend. Supports HTTP/HTTPS and WebSocket for real-time updates.
- User Session: manages user information ( used only if authentication module is configured)
- Web Application Context: stores all JUI Compoments added during development phase.
- JUI HTML Libraries: Collections of Java-based HTML tags and html-like templates
- HTML Renderer: Converts Java-based HTML tags to pure bootstrap 5-based HTML/JS/CSS resources

### JUI Simple Web Server

**JUI Simple Web server** designed to provide a web user interface (JUI) with bidirectional communication between the frontend (HTML page) and the backend (Java application). Totally based on `com.sun.net.httpserver.HttpServer` and utilizes custom context handlers to manage different types of requests:

1.  **`/jui` Context Handler (JUIHttpHandler):**
    * A custom implementation of `HttpHandler` that handles POST requests to the `/jui` endpoint.
    * **Functionality:**
        * Receives payload data from the HTTP request body.
        * Processes the data (e.g., JSON deserialization, data model updates).
        * Sends an HTTP response to the frontend (e.g., 200 OK status code, response data).


2.  **`/ws/jui` Context Handler (WebSocketHandler):**
    * A custom handler that manages WebSocket connections to the `/ws/jui` endpoint.
    * **Functionality:**
        * Performs the WebSocket handshake.
        * Manages bidirectional communication via WebSocket.
        * Allows the backend to send real-time messages to the frontend.
        * Handles WebSocket connection closures.
    * **Implementation Considerations:**
        * Use a custome WebSocket Library

4.  **`/js` and `/css` Context Handler (FileHandler):**
    * A custom handler that serves static files (JavaScript and CSS) from the file system.


>[!NOTE]
   >
   >Consider using a more performant web server (e.g., Jetty, Tomcat) for production environments.

### index.html

TBD

### Web Application Context

The "Web Application Context" serves as the core of the JUI application's state and component management. It acts as a centralized container for all JUI components created during the development phase. In technical terms, it's an instance of a Java class (often a `Map` or similar data structure) that maintains a reference to each active JUI component.

* **Component Management:**
    * During development, when a new JUI component is created (e.g., a button, table, form), it is added to the context.
    * The context provides a mechanism to retrieve components based on a unique identifier, allowing the backend to interact with specific UI elements.
* **Application State:**
    * The context can also store application state, such as session data, configurations, or global variables.
    * This allows JUI components to access and modify application state in a centralized manner.
* **Lifecycle:**
    * The context has a lifecycle that corresponds to the web application's lifecycle.
    * When the application starts, the context is initialized.
    * When the application stops, the context is destroyed.

### JUI HTML Libraries
The "JUI HTML Libraries" are collections of Java classes and templates that represent HTML elements and UI components. These libraries provide a high-level abstraction for creating web user interfaces, allowing developers to define the UI using Java code rather than directly writing HTML, JavaScript, and CSS.

* **HTML Abstraction:**
    * The libraries provide Java classes that correspond to standard HTML elements (e.g., `Button`, `Table`, `Div`).
    * Developers can create instances of these classes and configure them using Java methods.
* **Reusable Components:**
    * The libraries include reusable components, such as forms, dialog boxes, and data grids.
    * These components are designed to be easily customized and integrated into different parts of the application.
* **HTML Templates:**
    * The libraries may include HTML templates that define the structure and style of components.
    * These templates can be customized to fit specific application needs.


### HTML Renderer

The "HTML Renderer" is the component responsible for converting JUI components into HTML, JavaScript, and CSS code that can be displayed in the browser. This component acts as a bridge between the backend and frontend, ensuring that the UI is displayed correctly. 

* **Java-to-HTML Conversion:**
    * The renderer takes instances of JUI HTML Libraries classes as input and converts them into corresponding HTML elements.
    * This process includes generating HTML attributes, CSS styles, and JavaScript scripts.
* **Bootstrap 5 Integration:**
    * The renderer is designed to generate code compatible with Bootstrap 5, a popular CSS framework for creating responsive UIs.
    * This ensures that the UI is consistent and well-structured.
* **Event Handling:**
    * The renderer also generates the JavaScript code needed to handle UI events, such as mouse clicks and user input.
    * This allows JUI components to interact with the user and respond to their actions.

# Events

**Example**:

JUI component send a WSS message containing the kind of changes and commands to execute. In this case a button send the command to disable the button.

Jui Button: uses the following command to disable its state:

```JavaScript
    const button = document.getElementById("jui_btn_ctx_1_1");
    button.disabled = true;
	button.classList.add("disabled");
```

JUI fires the wss message to frontend:

```Java
    this.backEndEvents().onServerUpdate(this, "change", javascriptCommand);	
```

The frontend receives the message and executes the JavaScript command. Each JUI HTML element is aware of its own HTML rendering and manages its changes accordingly.

```JavaScript
    ws.onmessage = function(event) {
    const notification = JSON.parse(event.data);
    eval(notification.command);
};
```