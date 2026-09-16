# JUI Architecture

JUI is a Java-first server-rendered UI framework. Application code implements `JuiApp` and is rerun whenever the browser initializes or sends a widget update.

## Runtime overview

```text
Browser
  │
  │ GET static shell/resources
  │ GET/POST /ui
  ▼
Jetty / JuiServer
  │
  ├── static browser runtime
  ├── optional auth routes
  └── UiServlet
        │
        ├── session state
        ├── JuiProvider -> JuiApp
        └── new UIContext per render
              │
              ├── text / input / status
              ├── layout / navigation / lists
              ├── table / metric / form / CRUD
              ├── media / chart / map
              └── auth state
                    │
                    ▼
              generated HTML
```

The browser is deliberately thin. Application semantics stay in Java.

## Server

`JuiServer` embeds Jetty.

- context path: `/`
- static browser resources: packaged under `/static`
- UI endpoint: `/ui`
- default local port: `8080`
- deployment port: `PORT` environment variable when present
- optional Google OAuth routes can be installed before `start()`

```java
JuiServer server = new JuiServer(new JuiProvider(app));
server.start();
```

Optional Google authentication is installed explicitly:

```java
JuiServer server = new JuiServer(new JuiProvider(app))
        .googleOAuth(GoogleOAuthConfig.fromEnv());
server.start();
```

## Render cycle

A `GET /ui` creates a fresh `UIContext`, executes:

```java
app.run(ui);
```

and returns generated HTML plus browser dependencies.

Interactive widgets send `POST /ui` payloads such as:

```json
{
  "id": "widget-id",
  "value": "new-value"
}
```

The value is stored in session state and the application is rerun. JUI applications are therefore deterministic render functions over application data plus UI/session state.

## Browser runtime

The browser shell does four small jobs:

1. maintains a browser session id;
2. sends widget values to `/ui`;
3. replaces the rendered application fragment;
4. loads declared browser dependencies and executes component scripts after every rerender.

The same transport supports scalar values, lists and structured JSON values. It is used by ordinary inputs as well as multi-select values, uploaded-file metadata/content and interactive `MapState` updates.

The default file uploader sends base64 content through the normal `/ui` transport and applies a 5 MB browser-side limit. Large-file applications should provide a dedicated upload/storage path rather than storing binary data in JUI session state.

## Session state

`ISessionManager` abstracts widget/session storage. The default server uses `InMemorySessionManager`.

Interactive widgets use stable keys through `UIContext.getNextWidgetId(String)`. Actions such as buttons use one-shot state through `consumeBoolean(...)`.

`UIContext` also exposes framework primitives used by composite components:

```java
getRawValue(...)
setValue(...)
removeValue(...)
capture(Runnable)
```

`capture(...)` lets immediate-mode layout APIs render nested JUI content and then wrap the resulting fragment without introducing a retained component tree.

## UI API composition

`UIContext` delegates to focused API classes:

- `TextElements`
- `InputElements`
- `StatusElements`
- `LayoutElements`
- `NavigationElements`
- `ListElements`
- `DataElements`
- `FormElements`
- `CrudElements`
- `MapElements`
- `MediaElements`
- `ChartElements`
- `AuthElements`

The application still sees one compact API:

```java
ui.title("Customers");
ui.metric("Customers", customers.size());
ui.crud(Customer.class, customers);
```

Higher-level APIs compose the same immediate-mode primitives rather than creating a second UI model.

## Composite layout

Layout components render nested lambdas on the same `UIContext`:

```java
ui.columns(
    () -> ui.metric("Users", 42),
    () -> ui.metric("Revenue", 120000)
);

ui.expander("Advanced", () -> ui.text("Options"));
ui.dialog("Details", () -> ui.table(customers));
```

Sidebar navigation uses the same mechanism and passes the current selection to a content renderer.

## Type-driven tables and forms

`table(List<T>)` infers presentation from records, bean-style POJOs, maps and scalar values.

`form(Class<T>)` creates objects and `form(T)` edits existing objects. Records are the preferred model because component order and construction are deterministic. Current form inference includes strings, numbers, booleans, `LocalDate` and enums.

## CRUD and persistence

The compact prototype API is:

```java
ui.crud(Customer.class, customers);
```

Persistent or externally managed data uses:

```java
CrudRepository<T, ID>
```

with create/update/delete operations and stable identity. JUI does not need to know whether the implementation is backed by H2, PostgreSQL, REST or another store.

`InMemoryCrudRepository<T, ID>` provides the same contract for memory-backed applications.

## Charts, maps and media

Charts declare ApexCharts as a browser dependency and serialize deterministic chart options from Java values. Record/POJO based chart methods can infer the x-axis and series from property names.

Leaflet maps expose their browser center and zoom as `MapState`. `moveend`/zoom events use the normal JUI update/rerun transport instead of the legacy frontend/backend relation graph.

Image, audio and video APIs render native browser media elements.

## Authentication

Authentication is session-scoped but separated from UI rendering.

The built-in Google OAuth support:

- uses the standard authorization-code flow;
- exchanges tokens server-side with `HttpClient`;
- reads OpenID Connect user info;
- stores a canonical `AuthUser` in JUI session state;
- signs and expires the OAuth `state` value instead of relying on the old global application singleton.

Applications can inspect the user through `ui.authUser()` and render login/logout controls through `AuthElements`.

## jui-data

Data loading is intentionally outside the UI core.

The optional `jui-data` module provides:

```text
DataFrame
DataFrames.readCsv(...)
DataFrames.readJson(...)
DataFrames.readSql(...)
```

A `DataFrame` supports `select`, `limit`, row/column access and `toMaps()`, which can be passed directly to JUI tables.

This replaces the former `com.st` prototype without coupling data access to `jui-core`.

## Application data vs UI state

JUI session state is for widget values, authentication and transient interaction state. Business data should remain in application-owned collections, repositories or services.

This separation is particularly important for stateless/container deployments.

## LLM-first design

JUI remains Java-first; there is no required application IR or textual DSL.

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

The architecture rule is semantic compression: when JUI can infer structure deterministically from Java types and values, the caller should not have to describe it again.

## Legacy removal

The former `jui-core-old` retained-mode implementation (`com.jui.*`, custom element tree, custom HTTP/WebSocket rendering and frontend/backend relation graph) has been removed. Useful capabilities were reimplemented on the canonical `UIContext` rerun architecture rather than copied forward.
