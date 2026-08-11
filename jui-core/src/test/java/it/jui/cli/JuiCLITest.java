package it.jui.cli;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.eclipse.jetty.server.Server;

import it.jui.framework.core.AppProvider;
import it.jui.framework.core.HotReloadAppProvider;
import it.jui.framework.core.StaticAppProvider;
import it.jui.framework.core.UIContext;

public class JuiCLITest {

  public static void main(String[] args) throws Exception {
    
    AppProvider appProvider = new StaticAppProvider(ui -> 
      {
        ui.title("Hello JUI");
        ui.header("Map Elements");
        
        ui.map("Neapolis", 40.8518, 14.2681, 10);

      }
    );
    JuiCLI.startServer(appProvider).start();
  }

  @Test
  public void runStaticAppProvider() throws Exception {
    
    AppProvider appProvider = new StaticAppProvider(ui -> 
      {
        ui.title("Ciao da JUI!", "dashboard");
        ui.text("Prova il menu in alto a sinistra per cambiare tema!");

        String nome = ui.textInput("Come ti chiami?", "Ospite");
        int age = ui.slider("La tua età", 0, 100, 25);

        if (ui.button("Conferma Dati")) {
              ui.info("Dati salvati: " + nome + ", anni: " + age);
        }
      }
    );

    JuiCLI.startServer(appProvider).start();
  }

  @Test
  public void runHotReloadAppProvider() throws Exception {

    String sourceFilePath = "TestApp.java";
    JuiCLI.initProject(sourceFilePath);

    System.out.println("=== Avvio Server Watch Mode ===");
    System.out.println("Sorgente: " + sourceFilePath);

    AppProvider appProvider = new HotReloadAppProvider(new File(sourceFilePath));
    JuiCLI.startServer(appProvider).start();
  }

}