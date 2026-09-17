import it.jui.JuiApp;
import it.jui.UIContext;

import java.util.List;

public class SurveyExample implements JuiApp {
    @Override
    public void run(UIContext ui) {
        ui.title("Developer Survey");
        String level = ui.radio("Experience", List.of("Junior", "Mid", "Senior"), "Mid");
        List<String> interests = ui.multiCheckbox(
                "Interests", List.of("Java", "AI", "Cloud"), List.of("Java"));
        if (ui.button("Submit")) {
            ui.success("Saved: " + level + " · " + interests);
        }
    }
}
