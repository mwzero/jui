# Customer App

Canonical JUI example used for three purposes:

1. demonstrate the high-semantic-density Java API;
2. act as an integration/smoke-test application;
3. provide the first deployable Vercel example.

The application itself is intentionally small:

```java
record Customer(String name, String email, int age, boolean active) {}

ui.form(Customer.class).ifPresent(customers::add);
ui.metric("Customers", customers.size());
ui.table("Customers", customers);
```

## Run locally

From the repository root:

```bash
mvn -B -pl examples/customer-app -am package
java -jar examples/customer-app/target/customer-app-0.0.1-SNAPSHOT.jar
```

Then open `http://localhost:8080`.

Set `PORT` to override the local port:

```bash
PORT=9090 java -jar examples/customer-app/target/customer-app-0.0.1-SNAPSHOT.jar
```

## Deploy on Vercel

The repository root contains `Dockerfile.vercel`. Import this GitHub repository as a Vercel project and deploy the repository root. Vercel builds the customer application and starts its JUI/Jetty server on the platform-provided `PORT`.

The customer list is in-memory only. It is suitable for demonstrating JUI but is not durable across container restarts or scale-out. A persistent example should use an external database.
