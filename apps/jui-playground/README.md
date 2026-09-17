# JUI Playground

The playground is itself a JUI application. It lets you select an example, edit its Java source, compile it with the local JDK and render the resulting `JuiApp` inline.

```bash
mvn -B -pl apps/jui-playground -am package
java -jar apps/jui-playground/target/jui-playground-0.0.1-SNAPSHOT.jar
```

Open `http://localhost:8080`.

The playground executes arbitrary Java code locally with the permissions of its process. It is a development tool and must not be exposed to untrusted users or a public network.
