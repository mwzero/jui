package com.jui.recipes;

import static com.jui.JuiApp.jui; // Import statico
import com.st.DataFrame;          // Necessario per creare i dati
import com.jui.html.elements.Input; // Necessario per il tipo della variabile

import java.util.List;

public class FilterableTable {

    public static void main(String... args) {

        jui.markdown("## Tabella Filtrabile");
        jui.divider();

        // 1. Crea i dati di esempio usando DataFrame (dalla libreria com.st)
        DataFrame data = new DataFrame();
        data.addColumn("ID", List.of(1, 2, 3, 4));
        data.addColumn("Nome", List.of("Alice", "Bob", "Charlie", "David"));
        data.addColumn("Città", List.of("Napoli", "Roma", "Napoli", "Milano"));
        data.addColumn("Valore", List.of(150, 220, 180, 300));

        // 2. Crea un campo di input per il filtro
        //Input filterInput = jui.input("filterText", "", "Filtra per Nome..."); // ID, Valore iniziale, Placeholder

        // 3. Crea la tabella e collega i dati e il filtro
        jui.table("dataTable")
                .df(data) // Imposta i dati della tabella
                // Assumendo un metodo .filter() che collega un Input a una colonna
                // per il filtering lato client. Parametri: elemento input, nome colonna.
                //.height(300) // Imposta un'altezza fissa per la tabella (opzionale)
                //.showRowIndex(true); // Mostra l'indice di riga (opzionale)
                ;

        // Avvia il server JUI
        jui.server().start();
    }
}
