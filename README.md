# JUI

JUI is a lightweight Java framework for building interactive web applications with a very small application surface.

The canonical API lives under `it.jui.framework`. Applications stay Java-only: application code describes intent while JUI handles browser rendering, HTTP and session state.

> Minimum tokens from user intent to running application.

This makes JUI useful both for humans and for small local code models that should not need to generate HTML, CSS, JavaScript or framework boilerplate.

## Minimal application

```java
import it.jui.framework.app.JuiApp;
import it.jui.framework.app.JuiProvider;
import it.jui.framework.core.UIContext;
import it.jui.framework.server.JuiServer;

public class HelloApp implements JuiApp {
    @Override
    public void run(UIContext ui) {
        ui.title("Hello JUI");
        ui.text("A Java-only interactive application.");
    }

    public static void main(String[] args) throws Exception {
        new JuiServer(new JuiProvider(new HelloApp())).start();
    }
}
```

JUI listens on port `8080` locally. When `PORT` is present, JUI uses it automatically for container deployments.

## Interactive application

Interactive widgets keep state across rerenders, while button clicks use one-shot event semantics.

```java
public class InteractiveApp implements JuiApp {
    @Override
    public void run(UIContext ui) {
        ui.title("Customer Profile");

        String name = ui.textInput("Name", "Guest");
        int age = ui.slider("Age", 0, 100, 25);
        boolean active = ui.checkbox("Active", true);

        if (ui.button("Save")) {
            ui.success("Saved: " + name + ", age " + age + ", active=" + active);
        }
    }
}
```

Each interaction updates session state and reruns the application, so application code stays sequential and entirely in Java.

## High-semantic-density APIs

```java
record Customer(String name, String email, int age, boolean active) {}

ui.metric("Customers", customers.size());
ui.form(Customer.class).ifPresent(customers::add);
ui.table("Customers", customers);
ui.crud(Customer.class, customers);
```

For persistent data, provide a repository:

```java
CrudRepository<Customer, Long> repository = new H2CustomerRepository(dataSource);
ui.crud(Customer.class, repository);
```

`CrudRepository<T, ID>` owns identity and persistence while JUI owns interaction and rendering.

## Layout and navigation

Composite layout APIs keep ordinary JUI code inside Java lambdas:

```java
ui.columns(
    () -> ui.metric("Users", 42),
    () -> ui.metric("Revenue", 125000, "+12%")
);

ui.expander("Advanced", () -> ui.text("More options"));
ui.dialog("Details", () -> ui.table(customers));
ui.popover("Help", () -> ui.markdown("**Tip:** use records."));
```

A sidebar can render the selected page directly:

```java
ui.sidebar("Application", List.of("Home", "Customers", "Settings"), "Home", page -> {
    if (page.equals("Customers")) ui.crud(Customer.class, repository);
    else ui.header(page);
});
```

Tabs and compact dropdown-button navigation are also available.

## Text, lists and media

```java
ui.markdown("# Report\n- Java only\n- Interactive");
ui.code("record Customer(String name) {}", "java");
ui.caption("Generated locally");
ui.divider();
ui.bullets(List.of("one", "two"));

ui.image("/image.png", "Preview");
ui.audio("/sound.mp3");
ui.video("/movie.mp4");
```

Normal content is escaped. Raw HTML must be explicit with `ui.html(...)`.

## Inputs and uploads

Along with text, textarea, slider, boolean checkbox, select and date inputs, JUI provides:

```java
String size = ui.radio("Size", List.of("S", "M", "L"), "M");
List<String> tags = ui.multiCheckbox("Tags", List.of("Java", "AI", "Web"), List.of("Java"));
String color = ui.colorPicker("Color", "#4f46e5");

ui.fileUploader("Document").ifPresent(file -> {
    byte[] content = file.bytes();
});
```

Browser-backed uploads are intentionally limited to 5 MB by the default runtime.

## Charts and maps

```java
record Month(String name, int revenue, int cost) {}

ui.lineChart("Revenue", months, "name", "revenue", "cost");
ui.barChart("Revenue", months, "name", "revenue");
```

Charts use ApexCharts in the browser. Maps use Leaflet and are interactive:

```java
MapState state = ui.map("Naples", 40.8518, 14.2681, 12);
```

Moving or zooming the map updates `MapState` through the normal JUI rerun/session mechanism.

## Google authentication

Google OAuth is optional and installed on the server explicitly:

```java
JuiServer server = new JuiServer(new JuiProvider(new MyApp()))
        .googleOAuth(GoogleOAuthConfig.fromEnv());
server.start();
```

Environment variables:

```text
GOOGLE_CLIENT_ID
GOOGLE_CLIENT_SECRET
GOOGLE_CALLBACK_URL
```

Application code can then use:

```java
if (ui.authenticated()) {
    ui.text("Hello " + ui.authUser().orElseThrow().name());
    ui.logoutButton("Logout");
} else {
    ui.googleLoginButton("Login with Google");
}
```

The OAuth `state` value is signed and time-limited; authentication no longer depends on the legacy global `JuiApp` singleton.

## jui-data

Data loading is separated from the UI runtime in the `jui-data` module:

```java
DataFrame csv = DataFrames.readCsv("customers.csv");
DataFrame json = DataFrames.readJson(jsonString);
DataFrame db = DataFrames.readSql(connection, "select * from customer");

ui.table(csv.select("name", "email").limit(100).toMaps());
```

`DataFrame` supports `select`, `limit`, row/column access and conversion to maps. CSV, JSON and JDBC readers replace the former `com.st` prototype without coupling them to `jui-core`.

## Forms

Forms are inferred from Java records or bean-style POJOs. Current mappings include strings, numeric primitives/wrappers, booleans, `LocalDate` and enums.

```java
ui.form(Customer.class).ifPresent(repository::create);
ui.form(existingCustomer).ifPresent(updated -> repository.update(id, updated));
```

Records are the preferred model because field order and construction are deterministic.

## Examples

`examples/` contains standalone applications that double as documentation and compile-time smoke tests:

| Project | Focus |
| --- | --- |
| `customer-app` | type-driven CRUD, tables and metrics |
| `dashboard-app` | sidebar, metrics, charts and tables |
| `survey-app` | interactive inputs, progress and buttons |
| `map-app` | interactive Leaflet map and `MapState` |
| `media-app` | Markdown, lists, layout and browser media |

For example:

```bash
mvn -B -pl examples/dashboard-app -am package
java -jar examples/dashboard-app/target/dashboard-app-0.0.1-SNAPSHOT.jar
```

Then open `http://localhost:8080`.

## CLI

```bash
java -jar jui-core.jar init MyApp.java
java -jar jui-core.jar run MyApp.java
java -jar jui-core.jar watch MyApp.java
```

## Build and CI

JUI targets Java 25.

```bash
mvn test
```

CI runs focused JUnit tests for the canonical APIs, tests `jui-data`, compiles every example project and verifies their executable packages.

## Modules

- `jui-core` — canonical interactive UI framework.
- `jui-data` — optional CSV/JSON/JDBC DataFrame utilities.
- `examples/*` — small standalone applications covering the canonical API surface.
- `jui-playground` — standalone playground, no longer dependent on `jui-toolkits`.

The obsolete `jui-core-old`, `jui-toolkits`, `jui-apps-dashboard` and root `sandbox` workspace have been removed rather than kept as parallel framework implementations.

## LLM-first direction

```text
Natural language
      ↓
     J0
      ↓
small local Code LLM
      ↓
compact JUI Java
      ↓
javac / Maven
      ↓
JUI runtime
      ↓
deploy
```

JUI remains Java-first. If information can be inferred deterministically from Java types and values, application code — and therefore an LLM — should not have to generate it again.

## License

Apache License 2.0.
