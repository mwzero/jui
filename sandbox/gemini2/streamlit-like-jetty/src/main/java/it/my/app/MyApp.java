package it.my.app;
import it.my.framework.*;
import java.util.Arrays;
public class MyApp implements UIApp {
    @Override
    public void run(UIContext ui) {
        ui.title("Java Streamlit (HTTP/AJAX)");
        String name = ui.textInput("Nome", "Ospite");
        if(ui.button("Saluta")) ui.info("Ciao " + name + "! (Via HTTP POST)");
        ui.slider("Valore", 0, 100, 50);
        ui.datePicker("Data", "2025-01-01");
        ui.selectBox("Scelta", Arrays.asList("A","B","C"), "A");
    }
}