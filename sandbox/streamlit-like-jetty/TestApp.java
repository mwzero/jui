import it.jui.framework.core.*;
import java.util.*;

public class TestApp implements UIApp {

    @Override
    public void run(UIContext ui) {
        ui.title("Ciao da TestApp");
        ui.header("Benvenuto");
        ui.subheader("Benvenuto2");
        ui.text("Prova il menu in alto a sinistra per cambiare tema!");

        String nome = ui.textInput("Come ti chiami?", "Ospite");
        long age = ui.slider("La tua età", 0, 100, 25);
        if (ui.button("Conferma Dati")) {

            ui.text("Dati confermati!");
            ui.info("Dati salvati: " + nome + ", anni: " + age);
        }
        
    }
}