package it.jui.examples.survey;

import java.util.List;

import it.jui.framework.app.JuiApp;
import it.jui.framework.app.JuiProvider;
import it.jui.framework.core.UIContext;
import it.jui.framework.server.JuiServer;

public class SurveyApp implements JuiApp {

    @Override
    public void run(UIContext ui) {
        ui.title("Developer Survey", "checklist");
        ui.text("A compact example of stateful inputs on the immediate-mode rerun model.");

        String level = ui.radio("Experience", List.of("Junior", "Mid", "Senior"), "Mid");
        List<String> interests = ui.multiCheckbox("Interests",
                List.of("Java", "AI", "Cloud", "Data"), List.of("Java"));
        String editor = ui.select("Editor", List.of("VS Code", "IntelliJ", "Other"), "VS Code");
        String date = ui.dateInput("Available from", "2026-10-01");
        String accent = ui.colorPicker("Accent", "#4f46e5");

        int completion = 20;
        if (!level.isBlank()) completion += 20;
        if (!interests.isEmpty()) completion += 20;
        if (!editor.isBlank()) completion += 20;
        if (!date.isBlank()) completion += 20;
        ui.progressBar("Completion", completion);

        if (ui.button("Submit")) {
            ui.success("Saved: " + level + ", " + interests + ", " + editor + ", " + date + ", " + accent);
        }
    }

    public static void main(String[] args) throws Exception {
        new JuiServer(new JuiProvider(new SurveyApp())).start();
    }
}
