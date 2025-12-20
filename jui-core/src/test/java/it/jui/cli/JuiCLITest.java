package it.jui.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

import it.jui.framework.core.AppProvider;
import it.jui.framework.core.HotReloadAppProvider;
import it.jui.framework.core.StaticAppProvider;

public class JuiCLITest {

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
    
    Thread t = new Thread(() -> {
      try {
        JuiCLI.start(appProvider); // qui blocca su join()
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, "jetty");
    t.setDaemon(true);
    t.start();

    System.out.println("Apri http://localhost:8080/");
    System.out.println("Premi INVIO qui per terminare il test...");

    new BufferedReader(new InputStreamReader(System.in)).readLine();
  }


}