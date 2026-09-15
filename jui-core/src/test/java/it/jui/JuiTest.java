package it.jui;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.jui.cli.JuiCLI;
import it.jui.framework.app.JuiProvider;
import it.jui.framework.server.JuiServer;

/**
 * Manual server demos. These tests intentionally start a blocking HTTP server and
 * therefore must not run as part of the normal Maven unit-test suite.
 */
@Disabled("Manual JUI server demos")
public class JuiTest {

  @Test
  public void runStatic() throws Exception {

    JuiProvider appProvider = new JuiProvider(ui ->
      {
        ui.title("Hello JUI");
        ui.header("Map Elements");
        ui.map("Neapolis", 40.8518, 14.2681, 10);
      }
    );

    new JuiServer(8080, appProvider).start();
  }

  @Test
  public void runStaticExtended() throws Exception {

    JuiProvider appProvider = new JuiProvider(ui ->
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

    new JuiServer(8080, appProvider).start();
  }

  @Test
  public void runHotReloadAppProvider() throws Exception {

    String sourceFilePath = JuiTest.class.getResource("/TestApp.java").getPath();
    JuiCLI.initProject(sourceFilePath);

    System.out.println("=== Avvio Server Watch Mode ===");
    System.out.println("Sorgente: " + sourceFilePath);

    JuiProvider appProvider = new JuiProvider(sourceFilePath);
    new JuiServer(8080, appProvider).start();
  }
}
