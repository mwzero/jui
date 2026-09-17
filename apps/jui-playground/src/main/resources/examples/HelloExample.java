import it.jui.JuiApp;
import it.jui.UIContext;

public class HelloExample implements JuiApp {
    @Override
    public void run(UIContext ui) {
        ui.title("Hello JUI");
        String name = ui.textInput("Name", "Guest");
        if (ui.button("Say hello")) {
            ui.success("Hello " + name + "!");
        }
    }
}
