# Java Streamlit-like CLI

Un framework Java UI con **Hot Reloading** che preserva lo stato della sessione.

## Istruzioni

1. **Compila il Framework (una tantum):**
   ```bash
   cd streamlit-like-jetty
   mvn clean package
   ```
   Il file eseguibile sarà: `target/streamlit-like-jetty-1.0-SNAPSHOT.jar`

2. **Inizializza un progetto:**
   ```bash
   java -jar target/streamlit-like-jetty-1.0-SNAPSHOT.jar init MyApp.java
   ```

3. **Sviluppa in tempo reale:**
   ```bash
   java -jar target/streamlit-like-jetty-1.0-SNAPSHOT.jar watch MyApp.java
   ```
   
4. **Browser:** `http://localhost:8080/ui`