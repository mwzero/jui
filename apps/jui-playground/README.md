# JUI Playground

The playground is itself a JUI application. Its safe default mode lets you browse and edit example source while rendering a trusted reference preview inline.

```bash
mvn -B -pl apps/jui-playground -am package
JUI_PLAYGROUND_EXECUTION_ENABLED=true \
java -jar apps/jui-playground/target/jui-playground-0.0.1-SNAPSHOT.jar
```

Open `http://localhost:8080`.

Dynamic compilation is disabled unless `JUI_PLAYGROUND_EXECUTION_ENABLED=true`. When enabled, the playground executes arbitrary Java code with the permissions of its process and must only be used in a trusted local environment.

The Vercel container intentionally leaves execution disabled and serves the trusted cloud demo mode. Its runtime image contains a JRE rather than a JDK as an additional safeguard.
