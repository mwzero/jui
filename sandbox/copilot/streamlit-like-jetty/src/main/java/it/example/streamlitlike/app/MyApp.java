
package it.example.streamlitlike.app;

import it.example.streamlitlike.ui.UIContext;
import java.util.List;

public class MyApp {

    public void render(UIContext ctx) {

        ctx.sidebar(() -> {
            ctx.title("Add New Product");

            // text input
            String productUrl = ctx.textInput("Product URL", "productUrl");

            // checkbox
            boolean notify = ctx.checkbox("Notify me by email", "notifyEmail");

            if ( notify) 
                ctx.textInput("inserisci la mail", "email");

            // select
            String category = ctx.selectBox("Category", "category",
                    List.of("Electronics", "Books", "Clothing", "Home"));

            // file upload (es. immagine prodotto)
            ctx.fileUpload("Product Image", "productImage");

            // pulsante di azione
            boolean addButton = "true".equalsIgnoreCase(productUrl); // dummy, gestito via EventHandler

            // feedback dinamico gestito da EventHandler
        });
    }

    // Util se vuoi replicare validazioni lato server in EventHandler
    public static boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }
}
