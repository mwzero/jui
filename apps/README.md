# JUI Examples

Small standalone applications that demonstrate one area of the canonical JUI API at a time.

| Project | Focus |
| --- | --- |
| `jui-app-customer` | type-driven CRUD, table and metrics |
| `jui-app-dashboard` | sidebar navigation, metrics, charts and tables |
| `jui-app-survey` | interactive inputs, progress and one-shot buttons |
| `jui-app-map` | interactive Leaflet map and `MapState` |
| `jui-app-media` | Markdown, lists, layout and browser media |

Each project is a normal Maven module and packages as an executable shaded JAR.

From the repository root:

```bash
mvn -B -pl apps/jui-app-dashboard -am package
java -jar apps/jui-app-dashboard/target/jui-app-dashboard-0.0.1-SNAPSHOT.jar
```

Replace `jui-app-dashboard` with any other example name. JUI listens on `http://localhost:8080` by default.
