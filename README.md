# JUI

JUI is a lightweight Java framework for building interactive web applications with a very small application surface.

The canonical API lives under `it.jui.framework`. Applications stay Java-only: application code describes intent while JUI handles browser rendering, HTTP and session state.

The design principle is:

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

JUI listens on port `8080` locally. When the `PORT` environment variable is present, JUI uses it automatically for container/serverless deployments.

## Interactive application

Interactive widgets keep their state across rerenders, while button clicks use one-shot event semantics.

```java
import it.jui.framework.app.JuiApp;
import it.jui.framework.core.UIContext;

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

Each user interaction updates session state and reruns the application, so application code stays sequential and entirely in Java.

## High-semantic-density APIs

JUI infers common UI structure directly from Java types and values.

```java
record Customer(String name, String email, int age, boolean active) {}

ui.metric("Customers", customers.size());
ui.form(Customer.class).ifPresent(customers::add);
ui.table("Customers", customers);
```

For complete create/list/edit/delete behavior:

```java
ui.crud(Customer.class, customers);
```

The list overload is intended for small in-memory applications and prototypes.

## Repository-backed CRUD

Persistent applications can provide a repository without coupling JUI to a database technology:

```java
CrudRepository<Customer, Long> repository = new H2CustomerRepository(dataSource);
ui.crud(Customer.class, repository);
```

`CrudRepository<T, ID>` exposes a deliberately small persistence boundary:

```java
List<T> findAll();
ID id(T value);
T create(T value);
T update(ID id, T value);
void deleteById(ID id);
Optional<T> findById(ID id); // default implementation available
```

JUI also provides `InMemoryCrudRepository<T, ID>`. User implementations can use H2, PostgreSQL, REST services or any other persistence mechanism.

## Forms

Forms are inferred from Java records or bean-style POJOs.

Supported field mappings currently include:

- `String` -> text input
- numeric primitives/wrappers -> number input
- `boolean` / `Boolean` -> checkbox
- `LocalDate` -> date input
- `enum` -> select

```java
ui.form(Customer.class).ifPresent(customer -> repository.create(customer));
ui.form(existingCustomer).ifPresent(updated -> repository.update(id, updated));
```

Records are the preferred model because their field order and constructor are deterministic.

## Tables and metrics

```java
ui.table(customers);
ui.table("Customers", customers);
ui.metric("Revenue", 125000);
ui.metric("Revenue", 125000, "+12%");
```

`table(List<T>)` supports records, bean-style POJOs, maps and scalar values. Content is HTML-escaped by default.

Raw HTML must be explicit:

```java
ui.text("<b>escaped text</b>");
ui.html("<b>trusted raw HTML</b>");
```

## Canonical example

`examples/customer-app` is the deployment smoke test and compact-generation benchmark:

```java
@Override
public void run(UIContext ui) {
    ui.title("Customer Manager");
    ui.text("A compact JUI app generated from Java types.");
    ui.metric("Customers", customers.size());
    ui.crud(Customer.class, customers);
}
```

Build and run it from the repository root:

```bash
mvn -B -pl examples/customer-app -am package
java -jar examples/customer-app/target/customer-app-0.0.1-SNAPSHOT.jar
```

Then open `http://localhost:8080`.

The example is intentionally in-memory and is not durable across process/container restarts.

## CLI

Create a source file from the canonical template:

```bash
java -jar jui-core.jar init MyApp.java
```

Run it:

```bash
java -jar jui-core.jar run MyApp.java
```

`watch` currently follows the same server startup path:

```bash
java -jar jui-core.jar watch MyApp.java
```

## Build and CI

JUI currently targets Java 25.

```bash
mvn test
```

GitHub Actions runs focused tests for `jui-core` and the canonical customer application and also verifies its executable package.

## Vercel deployment

The repository contains `Dockerfile.vercel` and `vercel.json`. The container builds `examples/customer-app` and starts the JUI server on Vercel's `PORT`.

The deployed example is stateless. Durable application data should live behind a `CrudRepository` backed by an external database or service.

## Modules

- `jui-core` — canonical framework and current API.
- `examples/customer-app` — compact deployable example and integration target.
- `jui-core-old` — legacy implementation kept for historical/reference purposes.
- other playground/legacy modules may still contain older APIs and are not the canonical API source.

## LLM-first direction

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

JUI remains Java-first. If information can be inferred deterministically from Java types or values, application code — and therefore an LLM — should not have to generate it again.

## License

Apache License 2.0.
