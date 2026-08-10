package it.jui.cli;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.eclipse.jetty.server.Server;

import it.jui.framework.core.AppProvider;
import it.jui.framework.core.HotReloadAppProvider;
import it.jui.framework.core.StaticAppProvider;

public class JuiCLITest {

  public static void main(String[] args) throws Exception {
    
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

    this.start(appProvider);
  }

  @Test
  public void runHotReloadAppProvider() throws Exception {

    String sourceFilePath = "TestApp.java";
    JuiCLI.initProject(sourceFilePath);

    System.out.println("=== Avvio Server Watch Mode ===");
    System.out.println("Sorgente: " + sourceFilePath);

    AppProvider appProvider = new HotReloadAppProvider(new File(sourceFilePath));
    this.start(appProvider);
  }

  private void start(AppProvider appProvider) throws Exception {
    Server server = JuiCLI.startServer(appProvider, 0);
    try {
      server.start();
    } finally {
      server.stop();
      server.join();
    }
  }


}