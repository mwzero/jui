package com.jui.recipes;

import static com.jui.JuiApp.jui; // Import statico per l'API fluente
import com.jui.html.elements.Slider; // Import necessario per il tipo della variabile

public class InteractiveLineChart {

    public static void main(String... args) {

        jui.markdown("## Grafico a Linee Interattivo"); // Titolo
        jui.divider();

        // Crea tre slider per controllare i valori del grafico
        //Slider sliderVal1 = jui.slider("Valore 1", 0, 100, 20).cols(4); 
        Slider sliderVal1 = jui.slider("Valore 1", 0, 100, 20); 
        Slider sliderVal2 = jui.slider("Valore 2", 0, 100, 50);
        Slider sliderVal3 = jui.slider("Valore 3", 0, 100, 80);

        jui.container() // Mette gli slider su una riga (ipotizzando .container() crei un contenitore flessibile)
                .add(sliderVal1, sliderVal2, sliderVal3);

        //jui.divider().title("Grafico Risultante");

        // Crea il grafico a linee
        jui.lines()
            //.values(sliderVal1, sliderVal2, sliderVal3)
            //.labels("Gen", "Feb", "Mar")
            ; 

        // Avvia il server JUI
        jui.server().start();
    }
}
