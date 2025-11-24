import it.my.framework.*;
import java.util.*;

public class MyApp implements UIApp {
    
    @Override
    public void run(UIContext ui) {
        ui.title("Ciao da MyApp");
        ui.text("Questa app supporta Hot Reload con stato persistente.");

        String nome = ui.textInput("Come ti chiami?", "Ospite");
        int age = ui.slider("La tua età", 0, 100, 25);

        ui.info(age  + "");
        if ( age > 10 ) {
            if (ui.button("Conferma Dati")) {
                ui.info("Dati salvati: " + nome + ", anni: " + age);
            }
        }
    }
}
