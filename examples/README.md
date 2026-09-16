# JUI Examples

Small standalone applications that demonstrate one area of the canonical JUI API at a time.

| Project | Focus |
| --- | --- |
| `customer-app` | type-driven CRUD, table and metrics |
| `dashboard-app` | sidebar navigation, metrics, charts and tables |
| `survey-app` | interactive inputs, progress and one-shot buttons |
| `map-app` | interactive Leaflet map and `MapState` |
| `media-app` | Markdown, lists, layout and browser media |

Each project is a normal Maven module and packages as an executable shaded JAR.

From the repository root:

```bash
mvn -B -pl examples/dashboard-app -am package
java -jar examples/dashboard-app/target/dashboard-app-0.0.1-SNAPSHOT.jar
```

Replace `dashboard-app` with any other example name. JUI listens on `http://localhost:8080` by default.
