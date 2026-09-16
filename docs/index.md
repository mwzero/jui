# JUI

JUI is a lightweight Java framework for building interactive web applications without writing frontend code.

The canonical implementation is `jui-core` under the `it.jui.framework` packages. New applications implement `JuiApp`, receive a `UIContext`, and describe their UI with compact Java calls.

## Key ideas

- **Java-only application code** — no application HTML/CSS/JavaScript is required for normal use.
- **Type-driven UI** — records, POJOs, maps and scalar values can drive tables and forms.
- **High-semantic-density APIs** — `table`, `metric`, `form` and `crud` compress common application behavior into a few calls.
- **Deterministic state** — interactive widgets use stable IDs across rerenders.
- **Built-in HTTP runtime** — Jetty serves the browser shell and `/ui` rendering endpoint.
- **Pluggable persistence boundary** — `CrudRepository<T, ID>` keeps CRUD UI independent of H2, PostgreSQL, REST services or other stores.
- **LLM-first design** — APIs are intentionally compact and deterministic so small local code models need less context and generate fewer tokens.

## Small example

```java
record Customer(String name, String email, int age, boolean active) {}

@Override
public void run(UIContext ui) {
    ui.title("Customer Manager");
    ui.metric("Customers", customers.size());
    ui.crud(Customer.class, customers);
}
```

See [Getting Started](getting-started.md) for build and run instructions and [Architecture](architecture.md) for the runtime model.

## Canonical deployable example

`examples/customer-app` is the current smoke test and deployment benchmark. It can be built as an executable JAR and is also the application packaged by the Vercel container configuration.

## Legacy modules

`jui-core-old`, `com.jui.*`, and older playground examples are retained for historical/reference purposes. They are not the source of truth for new JUI applications.
