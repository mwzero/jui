import it.jui.JuiApp;
import it.jui.UIContext;

public class TestApp implements JuiApp {

    @Override
    public void run(UIContext ui) {
        ui.title("Hello JUI");

        String name = ui.textInput("Name", "Guest");
        int age = ui.slider("Age", 0, 100, 25);

        if (ui.button("Save")) {
            ui.info("Saved: " + name + ", age " + age);
        }
    }
}
