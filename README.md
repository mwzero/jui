# JUI

JUI is a lightweight Java framework for building interactive web applications with a very small application surface.

The current canonical API lives under `it.jui.framework` and is designed around a simple model:

- implement `JuiApp`;
- receive a `UIContext`;
- describe the application with compact Java calls such as `ui.title(...)`, `ui.textInput(...)`, `ui.button(...)`, `ui.table(...)` and `ui.map(...)`;
- let JUI handle HTTP, browser rendering and session state.

The long-term design goal is to make JUI especially suitable for code generation by small local LLMs: applications should require few source tokens, little framework context and deterministic APIs.

## Canonical API

Legacy `com.jui.*` APIs and `jui-core-old` are retained for historical/reference purposes only. New applications should use `jui-core` and the `it.jui.framework.*` packages.

## Minimal application

```java
import it.jui.framework.app.JuiApp;
import it.jui.framework.core.UIContext;

public class HelloApp implements JuiApp {

    @Override
    public void run(UIContext ui) {
        ui.title("Hello JUI");
        ui.text("A Java-only interactive application.");
    }
}
```

## Interactive application

```java
import it.jui.framework.app.JuiApp;
import it.jui.framework.core.UIContext;

public class CustomerApp implements JuiApp {

    @Override
    public void run(UIContext ui) {
        ui.title("Customer");

        String name = ui.textInput("Name", "Guest");
        int age = ui.slider("Age", 0, 100, 25);

        if (ui.button("Save")) {
            ui.success("Saved: " + name + ", " + age);
        }
    }
}
```

Widget state is keyed deterministically from the widget label in the current API. Buttons use one-shot event semantics: a click evaluates to `true` for the render triggered by that click and is then consumed.

Normal text-oriented APIs escape HTML. Raw HTML must be intentional and explicit:

```java
ui.text("<b>escaped text</b>");
ui.html("<b>trusted raw HTML</b>");
```

## CLI

Create a source file from the canonical template:

```bash
java -jar jui-core.jar init MyApp.java
```

Run/watch it:

```bash
java -jar jui-core.jar run MyApp.java
```

or:

```bash
java -jar jui-core.jar watch MyApp.java
```

JUI compiles the application source using the current JDK and serves it through the built-in HTTP server.

## Modules

- `jui-core` — current implementation and canonical API.
- `jui-core-old` — legacy implementation, not intended for new development.
- other modules and playground code may still contain legacy examples and should not be used as the API source of truth.

## Direction: LLM-first application framework

JUI is evolving toward a compact application framework where a small local model can generate useful applications without needing knowledge of HTML, CSS, JavaScript, Jetty or deployment internals.

The design principle is:

> Minimum tokens from user intent to running application.

Future work should favor high-semantic-density application primitives, a compact validated application model/IR, machine-generated API metadata for LLM context and deterministic packaging/deployment.

## Build

```bash
mvn test
```

The normal unit-test suite must terminate automatically; manual tests that start a blocking server are kept disabled from the Maven unit-test lifecycle.

## License

Apache License 2.0.
