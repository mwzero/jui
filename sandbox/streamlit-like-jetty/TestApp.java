import it.jui.framework.core.*;
import java.util.*;

public class TestApp implements UIApp {

    @Override
    public void run(UIContext ui) {

        ui.title("Hello JUI");
        ui.header("Elements");
        ui.subheader("Text Elements");
        ui.text("Prova il menu in alto a sinistra per cambiare tema!");

        String nome = ui.textInput("Come ti chiami?", "Ospite");
        long age = ui.slider("La tua età", 0, 100, 25);
        if (ui.button("Conferma Dati")) {

            ui.info("Dati salvati: " + nome + ", anni: " + age);
        }
        ui.checkbox("Sesso", false);
        ui.selectBox("Città", List.of("A","B","C"), "Napoli");
        ui.datePicker("Nato il", null);
        ui.textarea("descrizione", null);
        ui.fileUpload("aggiungi avatar");

        ui.subheader("Text Elements");
        ui.success("Success");
        ui.info("Info");
        ui.warning("warning");
        ui.error("error");
        ui.spinner("spinner");
        ui.progressBar("progress1", 10);
        ui.progressBarAnimated("progress2", 10);

        ui.subheader("Navigazione");
        String activeTab = ui.tabs("Sezioni", List.of("Profilo", "Team", "Impostazioni"), "Profilo");
        ui.info("Tab attiva: " + activeTab);

        ui.subheader("Layout & metriche");
        ui.card("Benvenuto, " + nome + "!", "Riepilogo rapido dei dati forniti: età " + age + ".");
        ui.metricCard("Utenti attivi", "1.248", "+12% rispetto a ieri", true);
        ui.metricCard("Errori giornalieri", "12", "-3% rispetto alla media", false);

        ui.subheader("Dati");
        ui.table(
            "Ultime richieste",
            List.of("ID", "Utente", "Stato"),
            List.of(
                List.of("#1024", "Anna", "Completata"),
                List.of("#1025", "Luca", "In corso"),
                List.of("#1026", "Sara", "In attesa")
            )
        );
        ui.badge("Nuovo", "info");

    }
}
