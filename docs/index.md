# JUI

JUI is a lightweight Java framework for building interactive web applications without writing frontend code.

The canonical implementation is `jui-core` under `it.jui.framework`. Applications implement `JuiApp`, receive a `UIContext`, and describe their UI with compact Java calls.

## Key ideas

- **Java-only application code** — no application HTML/CSS/JavaScript is required for normal use.
- **Type-driven UI** — records, POJOs, maps and scalar values can drive tables, forms, charts and CRUD.
- **High-semantic-density APIs** — `table`, `metric`, `form`, `crud`, layout and navigation APIs compress common application behavior.
- **Deterministic state** — interactive widgets, maps and composite components use stable IDs across rerenders.
- **Built-in HTTP runtime** — Jetty serves the browser shell and `/ui` rendering endpoint.
- **Pluggable persistence** — `CrudRepository<T, ID>` keeps CRUD independent of H2, PostgreSQL, REST or other stores.
- **Optional data module** — `jui-data` provides CSV, JSON and JDBC `DataFrame` utilities without coupling data access to the UI runtime.
- **Optional Google OAuth** — authentication integrates with the same session model without a global application singleton.
- **LLM-first design** — compact deterministic APIs reduce generated code and context requirements for small local models.

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

See [Getting Started](getting-started.md) for build/run instructions and [Architecture](architecture.md) for the runtime model.

## Modules

- `jui-core` — canonical interactive UI framework.
- `jui-data` — optional CSV/JSON/JDBC DataFrame utilities.
- `examples/customer-app` — deployable smoke test and compact-generation benchmark.

The former `jui-core-old` module has been removed after its useful capabilities were reimplemented on the canonical rerun architecture.
