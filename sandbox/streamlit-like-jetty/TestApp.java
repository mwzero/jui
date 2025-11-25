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
        
    }
}