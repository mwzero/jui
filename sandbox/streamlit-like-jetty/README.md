# streamlit-like-jetty

Questa sandbox illustra una mini applicazione stile Streamlit basata su Jetty. Le API principali sono esposte tramite la classe `UIContext`, che orchestra la generazione di HTML Tailwind per ogni richiesta.

## Componenti di testo
`src/main/java/it/jui/framework/apis/TextElements.java` fornisce helper per titoli, paragrafi e campi di input decorati con classi TailwindCSS (es. `text-3xl font-bold`, `bg-indigo-600`, `border-gray-300`). Questi metodi aggiungono HTML al contesto tramite `ctx.addHtml(...)` e mantengono lo stato con `ctx.getValue(...)` usando identificativi univoci generati da `ctx.getNextWidgetId(...)`.

## Componenti di stato
`src/main/java/it/jui/framework/apis/StatusElements.java` aggiunge messaggi di stato e indicatori di caricamento con stili Tailwind (`bg-green-50`, `bg-yellow-50`, progress bar animate). Anche qui `ctx.addHtml(...)` inserisce il markup nella risposta mentre `ctx.getValue(...)` consente di conservare i valori correnti, ad esempio le percentuali delle progress bar.

## Navigazione e layout
`src/main/java/it/jui/framework/apis/NavigationElements.java` espone tab con stile Tailwind, aggiornando lo stato attivo tramite `sendUpdate(...)` e `ctx.getValue(...)` per evidenziare il pulsante selezionato. `src/main/java/it/jui/framework/apis/LayoutElements.java` propone card e pannelli di metrica con bordi, ombre e colorazione condizionale per gli andamenti (verde/rosso).

## Dati e badge
`src/main/java/it/jui/framework/apis/DataElements.java` consente di rendere badge colorati e tabelle responsive: gli header e le righe sono generati da liste Java e stilizzati con classi Tailwind (`divide-y`, `rounded-xl`, `dark:*`). Anche qui il markup viene accumulato con `ctx.addHtml(...)`.
