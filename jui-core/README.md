# jui-core

`jui-core` is the canonical JUI runtime and Java UI API.

Applications implement `it.jui.JuiApp` and render through `UIContext`. The runtime uses the JDK HttpServer plus a thin browser shell; interactions update session state and rerun the Java application.

Core capabilities include:

- text, Markdown, code and status elements
- interactive inputs and small file uploads
- layout, tabs, sidebar and dialogs
- type-driven tables and forms
- CRUD with pluggable `CrudRepository<T, ID>`
- charts, media and interactive Leaflet maps
- optional Google OAuth

See the repository root `README.md` and `docs/` for the current API and architecture.

Authentication uses a server-issued HttpOnly cookie and typed identity stored separately from UI state. Browser updates require a CSRF token, the current view revision and a registered widget with a valid value. See `docs/architecture.md` for the protocol and custom-component migration.
