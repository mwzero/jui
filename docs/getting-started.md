# Getting Started with JUI

JUI's canonical API is the Java-only `it.jui.framework` API in the `jui-core` module.

## Requirements

- Java 25
- Maven 3.9+

## Build JUI locally

From the repository root:

```bash
mvn -B -pl jui-core -am install
```

This installs the current snapshot in your local Maven repository.

For a local standalone Maven project, depend on:

```xml
<dependency>
    <groupId>com.jui</groupId>
    <artifactId>jui-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Your first application

```java
import it.jui.framework.app.JuiApp;
import it.jui.framework.app.JuiProvider;
import it.jui.framework.core.UIContext;
import it.jui.framework.server.JuiServer;

public class HelloApp implements JuiApp {

    @Override
    public void run(UIContext ui) {
        ui.title("Hello JUI");
        ui.text("Everything in this application is described in Java.");
    }

    public static void main(String[] args) throws Exception {
        new JuiServer(new JuiProvider(new HelloApp())).start();
    }
}
```

By default JUI listens on port `8080`. Open:

```text
http://localhost:8080
```

If the environment defines `PORT`, JUI uses that value automatically.

## Build the canonical example

The easiest complete example is `examples/customer-app`:

```bash
mvn -B -pl examples/customer-app -am package
java -jar examples/customer-app/target/customer-app-0.0.1-SNAPSHOT.jar
```

Its application body is intentionally compact:

```java
record Customer(String name, String email, int age, boolean active) {}

@Override
public void run(UIContext ui) {
    ui.title("Customer Manager");
    ui.metric("Customers", customers.size());
    ui.crud(Customer.class, customers);
}
```

## Type-driven APIs

JUI can infer UI structure from Java values and types.

### Table

```java
ui.table(customers);
ui.table("Customers", customers);
```

Records keep declaration order. Bean-style POJO columns are inferred from getters. Maps use stable sorted keys. Scalar lists use a single `Value` column.

### Metric

```java
ui.metric("Customers", customers.size());
ui.metric("Revenue", 125000, "+12%");
```

### Form

```java
ui.form(Customer.class).ifPresent(customers::add);
```

For editing:

```java
ui.form(existingCustomer).ifPresent(updated -> {
    // persist the updated value
});
```

Forms currently support `String`, numeric primitives/wrappers, booleans, `LocalDate` and enums.

### CRUD

For a mutable in-memory list:

```java
ui.crud(Customer.class, customers);
```

This composes create, list, edit, delete and cancel behavior.

## Persistent CRUD

For persistence, implement `CrudRepository<T, ID>`:

```java
import it.jui.framework.data.CrudRepository;

public final class H2CustomerRepository
        implements CrudRepository<Customer, Long> {

    @Override
    public List<Customer> findAll() {
        // SELECT ...
    }

    @Override
    public Long id(Customer value) {
        return value.id();
    }

    @Override
    public Customer create(Customer value) {
        // INSERT ... and return persisted value
    }

    @Override
    public Customer update(Long id, Customer value) {
        // UPDATE ... WHERE id = ?
    }

    @Override
    public void deleteById(Long id) {
        // DELETE ... WHERE id = ?
    }
}
```

Then the JUI application stays independent of H2/JDBC:

```java
CrudRepository<Customer, Long> repository = new H2CustomerRepository(dataSource);
ui.crud(Customer.class, repository);
```

JUI also provides `InMemoryCrudRepository<T, ID>` for memory-backed data with a stable identity function.

## Events and state

Interactive widgets use deterministic IDs so their values survive a rerender. Button events are one-shot: the click is consumed on the render caused by that click.

Normal text-oriented APIs escape HTML. Use `ui.html(...)` only for explicitly trusted raw HTML.

## CLI

If you have built the executable `jui-core` JAR, the CLI can create and run canonical source files:

```bash
java -jar jui-core.jar init MyApp.java
java -jar jui-core.jar run MyApp.java
```

`watch` currently uses the same runtime path:

```bash
java -jar jui-core.jar watch MyApp.java
```

## Deployment

`JuiServer` reads the `PORT` environment variable, which allows the same application to run locally and in container platforms.

This repository includes a Vercel container setup that deploys the canonical customer example. Because containers are stateless, use a database-backed `CrudRepository` for durable data.

## Legacy code

`jui-core-old` and `com.jui.*` belong to the previous implementation and should not be used as the API source for new applications.
