import it.my.framework.*;
import java.util.*;

public class MyApp implements UIApp {
    @Override
    public void run(UIContext ui) {
        // Gestione Navigazione Multi-Page
        String currentPage = ui.getState("page", "Home");

        // --- SIDEBAR ---
        ui.sidebar(() -> {
            ui.title("Menu");
            if (ui.button("🏠 Home")) ui.updateState("page", "Home");
            if (ui.button("📊 Analisi")) ui.updateState("page", "Analisi");
            if (ui.button("⚙️ Settings")) ui.updateState("page", "Settings");
        });

        // --- MAIN CONTENT ---
        if ("Home".equals(currentPage)) {
            ui.title("Benvenuto nella Home");
            ui.text("Questa è la pagina principale.");
            ui.info("Usa la sidebar a sinistra per navigare.");
        } else if ("Analisi".equals(currentPage)) {
            ui.title("Pagina di Analisi");
            int val = ui.slider("Filtro Dati", 0, 100, 50);
            ui.text("Valore filtro: " + val);
        } else {
            ui.title("Impostazioni");
            boolean dark = ui.checkbox("Modalità Esperto", false);
            ui.text("Modalità esperto: " + dark);
        }
    }
}
