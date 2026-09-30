# JUI Architecture

JUI is a Java-first server-rendered UI framework. Application code implements `JuiApp` and is rerun whenever the browser initializes or sends a widget update.

## Runtime overview

```text
Browser
  │
  │ GET static shell/resources
  │ GET/POST /ui
  ▼
JDK HttpServer / JuiServer
  │
  ├── static browser runtime
  ├── optional auth routes
  └── UiHandler
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

`JuiServer` uses the JDK `com.sun.net.httpserver.HttpServer` from the `jdk.httpserver` module. JUI does not require Jetty, a Servlet container or a server framework dependency.

- context path: `/`
- static browser shell: packaged under `/static`
- UI endpoint: `/ui`
- one virtual thread per HTTP exchange through `Executors.newVirtualThreadPerTaskExecutor()`
- default local port: `8080`
- deployment port: `PORT` environment variable when present
- optional Google OAuth routes can be installed before `start()`

The default bootstrap hides the provider and server infrastructure:

```java
Jui.run(app);
```

`JuiServer` and `JuiProvider` remain available when infrastructure must be configured explicitly, such as for optional Google authentication:

```java
JuiServer server = new JuiServer(new JuiProvider(app))
        .googleOAuth(GoogleOAuthConfig.fromEnv());
server.start();
```

The HTTP layer is intentionally small. `HttpSupport` only provides query parsing and response/body helpers; it is not a replacement Servlet framework.

## Render cycle

A `GET /ui?viewId=...` resolves or creates a server-owned cookie session, creates a fresh `UIContext` for that view, and executes:

```java
app.run(ui);
```

and returns generated HTML plus browser dependencies.

Interactive widgets send JSON to `POST /ui?viewId=...`, with the session cookie and `X-JUI-CSRF` header, using payloads such as:

```json
{
  "id": "widget-id",
  "value": "new-value",
  "revision": 1
}
```

The server validates the session, CSRF token, view revision, registered widget and its value before changing widget state or rerunning the application. Update plus render is serialized per session. Each response includes `csrfToken` and `revision` alongside HTML and dependencies; it never includes the session identifier.

Invalid requests do not invoke application code: malformed JSON or invalid values return 400, invalid CSRF/unknown widgets return 403, missing or expired sessions return 401, and obsolete/missing views return 409. Request bodies remain limited to 8 MB (413 when exceeded). On application render failure, partial HTML is discarded and the view's input registry is revoked.

## Browser runtime

The browser shell does four small jobs:

1. maintains a per-tab `viewId` in sessionStorage; the browser carries the HttpOnly session cookie automatically;
2. queues widget updates with a CSRF token and the latest view revision;
3. replaces the rendered application fragment;
4. loads declared browser dependencies and executes component scripts after every rerender.

The same transport supports scalar values, lists and structured JSON values. It is used by ordinary inputs as well as multi-select values, uploaded-file metadata/content and interactive `MapState` updates.

The default file uploader sends base64 content through the normal `/ui` transport. Both browser and server enforce a 5 MB file limit; the server verifies the decoded bytes against the declared size. The JDK HTTP handler also bounds request bodies. Large-file applications should provide a dedicated upload/storage path rather than storing binary data in JUI session state.

## Session state

`ISessionManager` provides structured `SessionState` instances. `InMemorySessionManager` is the default implementation. A session contains typed `AuthUser` identity, CSRF/OAuth metadata and independent `ViewState` objects. Each view separates internal application values from widget values and the current widget registry.

The `JUI_SESSION` cookie is generated on the server, HttpOnly, SameSite=Lax, Path=/ and host-only. OAuth with an HTTPS callback enables Secure; HTTP callbacks are limited to loopback. Proxy headers never determine this policy. Login/logout invalidate the old session, generate a new cookie and CSRF token and clear all views. Defaults are 30 minutes idle and 8 hours absolute lifetime; server startup enables periodic cleanup and shutdown closes it.

`viewId` selects only a view inside the cookie session and is not an authentication credential. Newly navigated tabs generate their own identifier, including tabs inheriting their opener's sessionStorage; page reloads retain it. Views refresh on focus. After session changes or stale-view responses, the browser reloads without replaying queued actions.

### Custom components and protocol migration

High-level UI/authentication method signatures remain unchanged. Low-level state has separate responsibilities:

- `getValue`, `getRawValue`, `setValue`, `removeValue`, `consumeBoolean`: trusted internal application state.
- `getWidgetValue`, `getRawWidgetValue`, `setWidgetValue`, `removeWidgetValue`, `consumeWidgetBoolean`: widget state and one-shot actions.
- `registerWidget(id, WidgetSpec)`: permit a browser update only for an input emitted by this render. Creating an ID or reading a value does not register it.

For example, a custom text component must register its input, then read widget state:

```java
String id = ui.getNextWidgetId("custom:notes");
ui.registerWidget(id, WidgetSpec.text());
String value = ui.getWidgetValue(id, "");
// Emit an escaped input that invokes sendUpdate(id, this.value).
```

Use `WidgetSpec.action()` with `consumeWidgetBoolean` for ordinary buttons. Built-in validators cover text, booleans, choices, multi-selection, integer ranges, dates, colors, map coordinates and uploads. Numeric form fields remain strings while editing and are converted/validated at submission. `capture(...)` continues to compose nested markup within the same render.

The HTTP runtime publishes the entire registry after each render via `completeRender`; removed controls cannot receive updates. Custom `ISessionManager` implementations must implement the new session lifecycle operations. The legacy `getState`/`updateState` helpers now address internal state in the default view. `AuthUser.SESSION_KEY` is deprecated and ignored for authentication, including when a typed object is placed under that key.

Custom HTTP clients must first GET a view to obtain its cookie, CSRF token and revision, and include them on POST. The old `sessionId` query parameter is rejected. `window.juiSessionId()` has been removed; `window.juiViewId()` exposes only the tab selector. Reload pages after upgrading; old protocol clients require migration.

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

- registers JDK `HttpServer` contexts directly;
- uses the standard authorization-code flow;
- exchanges tokens server-side with the JDK `HttpClient`;
- reads OpenID Connect user info;
- stores a canonical `AuthUser` in a dedicated typed session field, requiring a nonempty Google subject;
- binds random, single-use OAuth state to the initiating cookie, with a 10-minute lifetime;
- rechecks the session after network calls before rotating it and setting identity;
- uses finite network timeouts and generic user-facing errors; tokens and client secrets never reach the browser.

Applications can inspect the user through `ui.authUser()` and render login/logout controls through `AuthElements`. Logout is a registered action processed before application execution: it clears every view and renders the anonymous application immediately. Its boolean result remains one-shot if the app renders the corresponding button during that response.

The [authentication example](../apps/jui-app-auth/README.md) demonstrates a guarded profile and per-tab note. Authentication does not impose an account allowlist or roles; application-specific authorization belongs in Java application logic.

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

## Verification

`mvn test` includes HTTP integration tests on temporary loopback ports and OAuth tests with a simulated Google client. They require no Google credentials. Browser transport tests use Node's built-in test runner (Node 22 in CI):

```bash
node --test jui-core/src/test/js/browser-runtime.test.cjs
```

These exercise event ordering, CSRF/revision propagation, stale-session recovery and cancellation of queued actions after logout. Node is only needed for these development tests, not to build or run a JUI application.
