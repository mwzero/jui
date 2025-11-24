**Obiettivi principali:**
1. Ogni volta che l’utente interagisce (clic, input), il codice UI viene rieseguito dall’inizio.
2. Mantieni lo stato dei widget tra le richieste usando un SessionManager pluggabile (InMemory, Redis, JDBC).
3. Fornisci un’API semplice per costruire la UI, simile a Streamlit:
   - UIContext con metodi: textInput, checkbox, selectBox, slider, datepicker, textarea, fileUpload, button.
4. Supporta due modalità:
   - **Full reload**: servlet che ricostruisce tutta la pagina ad ogni richiesta.
   - **Event-driven**: aggiornamenti parziali via AJAX o WebSocket.
5. Struttura Maven completa con:
   - `pom.xml` (Java 17, Jetty per WS, DuckDB per data app).
   - `Main.java` per bootstrap server.
   - `UiServlet.java` per rendering iniziale.
   - `ProductSocket.java` per gestione eventi WebSocket.
   - `SessionManager` + implementazioni.
   - `UIContext.java` per widget.
   - `MyApp.java` per definire la UI.
6. Aggiungi un secondo modulo per data app:
   - HttpServer + DuckDB.
   - DuckDataFrame.java con metodi: registerCsv, select, filter, groupAgg, toHtmlTable.
   - Handlers per upload CSV e query.
   - UIContext per widget data-oriented (multiselect, textarea, fileUpload, button).

**Requisiti tecnici:**
- Java 17.
- Maven con plugin `maven-dependency-plugin` (goal copy-dependencies).
- Jetty WebSocket per aggiornamenti real-time.
- DuckDB JDBC per operazioni sui dati.
- HTML + JS per eventi (AJAX o WebSocket).
- README con istruzioni di build e run.

**Output richiesto:**
- Due progetti Maven: `streamlit-like-jetty` e `duckdb-pandas-like`.
  - Codice completo funzionante per tutti i file Java.
  - `pom.xml` con dipendenze corrette.
  - `static/index.html` con client WebSocket.
  - README.md con istruzioni di avvio.