# JUI Architecture

JUI is a Java-first server-rendered UI framework. Application code implements `JuiApp` and is rerun to describe the current page whenever the browser initializes or sends a widget update.

## Runtime overview

```text
Browser
  │
  │ GET static shell/resources
  │ GET/POST /ui
  ▼
Jetty / JuiServer
  │
  ├── static resources
  └── UiServlet
        │
        ├── session state
        ├── JuiProvider -> JuiApp
        └── new UIContext per render
              │
              ├── text / status / layout
              ├── table / metric
              ├── form / crud
              └── map / navigation
                    │
                    ▼
              generated HTML
```

The browser is thin. JUI application semantics remain in Java.

## Server

`JuiServer` embeds Jetty.

- context path: `/`
- static browser resources: served from the packaged `/static` resources
- UI endpoint: `/ui`
- default local port: `8080`
- deployment port: value of the `PORT` environment variable when present

A `JuiServer` is constructed with a `JuiProvider`, which supplies the `JuiApp` to execute for each render.

## Render cycle

`UiServlet` implements the current request/rerender loop.

### Initial render

A `GET /ui` request resolves a session ID, creates a fresh `UIContext`, executes:

```java
app.run(ui);
```

and returns a JSON response containing generated HTML, HTML dependencies and a `fullPage` flag.

### Widget update

Interactive browser widgets call the backend with `POST /ui` and a payload containing:

```json
{
  "id": "widget-id",
  "value": "new-value"
}
```

The servlet stores the value in the current session, creates a new `UIContext`, reruns the application and returns the new rendered output.

This means JUI applications should be understood as deterministic render functions over application data plus session/widget state.

## Session state

`ISessionManager` abstracts widget/session storage. The current server uses `InMemorySessionManager`.

Interactive widgets should use deterministic IDs. `UIContext.getNextWidgetId(String key)` derives an ID from a stable application-level key so the same widget maps to the same session value across rerenders.

Normal values are retrieved through `getValue(...)`.

Action events such as buttons use one-shot semantics through `consumeBoolean(...)`: the value is removed from session state when consumed, so an action is true for exactly the render triggered by that click.

Composite components such as forms and CRUD use the same session state primitives rather than maintaining a separate state system.

## UI API composition

`UIContext` delegates to focused API classes including:

- `TextElements`
- `StatusElements`
- `LayoutElements`
- `NavigationElements`
- `DataElements`
- `FormElements`
- `CrudElements`
- `MapElements`

The public experience remains a single compact `UIContext`:

```java
ui.title("Customers");
ui.metric("Customers", customers.size());
ui.crud(Customer.class, customers);
```

Higher-level APIs are intentionally composed from lower-level primitives rather than introducing a second application model.

## Type-driven tables

`table(List<T>)` infers a presentation deterministically:

- Java records -> record component declaration order
- bean-style POJOs -> public getter properties in stable order
- maps -> stable sorted keys
- scalar values -> one `Value` column

Ordinary content is HTML-escaped.

## Type-driven forms

`form(Class<T>)` creates values and `form(T)` edits existing values.

Records are the preferred model. JUI uses record components and the canonical constructor. Bean-style POJOs are supported when suitable public getter/setter pairs and a no-argument constructor are available.

Current field inference includes strings, numeric values, booleans, `LocalDate` and enums.

Keyed internal form overloads allow composite components such as CRUD to isolate form state.

## CRUD

The compact in-memory API is:

```java
ui.crud(Customer.class, customers);
```

It composes create, list, edit, delete and cancel behavior from the existing form, table and state primitives.

For durable or externally managed data, JUI uses:

```java
CrudRepository<T, ID>
```

The repository owns identity and persistence:

```java
List<T> findAll();
ID id(T value);
T create(T value);
T update(ID id, T value);
void deleteById(ID id);
Optional<T> findById(ID id);
```

JUI therefore does not need to know whether persistence is implemented with H2/JDBC, PostgreSQL, a REST API or another store.

`InMemoryCrudRepository<T, ID>` provides the same repository shape for memory-backed applications with stable IDs.

## Application data vs UI state

JUI session state stores interactive widget values and transient UI mode such as CRUD create/edit selection.

Business/application data should remain in application-owned collections or repositories. For example:

```java
private final CrudRepository<Customer, Long> customers;

@Override
public void run(UIContext ui) {
    ui.crud(Customer.class, customers);
}
```

This separation becomes especially important in stateless/container deployments.

## Deployment

The canonical example builds an executable shaded JAR. The repository also includes `Dockerfile.vercel` and `vercel.json` for deploying that HTTP server as a Vercel container.

The runtime reads `$PORT`, so the same server code can run locally or behind a deployment platform.

Container instances should be treated as stateless. Durable application data belongs in an external backing store exposed to JUI through `CrudRepository` or another application-level service.

## LLM-first design

JUI remains Java-first; there is no required application IR or textual DSL.

The intended generation path is:

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

The central architecture rule is semantic compression: when JUI can infer structure deterministically from Java types and values, the caller should not have to describe that structure again.

## Legacy architecture

Older modules and documentation may refer to `com.jui.*`, a custom `HttpServer`, WebSocket-specific rendering or older fluent component APIs. Those belong to the legacy implementation and are not the architecture of the canonical `jui-core` described here.
