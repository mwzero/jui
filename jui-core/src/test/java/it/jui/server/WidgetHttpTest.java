package it.jui.server;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import static it.jui.server.HttpTestRuntime.id;
import static org.junit.jupiter.api.Assertions.*;

public class WidgetHttpTest {
    public enum Role { USER, ADMIN }
    public record Profile(String name, int age, boolean active, LocalDate since, Role role) {}
    public record Customer(String name) {}

    @Test void allBuiltInInputFamiliesRegisterAndAcceptTheirBrowserPayloads() throws Exception {
        try (var runtime = new HttpTestRuntime(ui -> {
            ui.textInput("Name", "Guest");
            ui.slider("Age", 0, 100, 25);
            ui.checkbox("Active", true);
            ui.selectBox("Country", List.of("Italy", "France"), "Italy");
            ui.datePicker("Start", "2026-01-01");
            ui.textarea("Notes", "");
            ui.codeEditor("Source", "");
            ui.radio("Size", List.of("S", "L"), "S");
            ui.multiCheckbox("Tags", List.of("Java", "AI"), List.of());
            ui.select("Language", List.of("Java", "Scala"), "Java");
            ui.colorPicker("Accent", "#ffffff");
            ui.dateInput("End", "2026-01-01");
            ui.fileUploader("File");
            ui.map("Map", 40, 14, 10);
            ui.tabs("Pages", List.of("Home", "Settings"), "Home");
            ui.sidebar("App", List.of("Home", "Settings"), "Home", ui::text);
            ui.dropdownButton("Export", List.of("CSV", "JSON"));
            ui.button("Run");
        })) {
            runtime.start();
            var browser = runtime.browser();
            browser.getUi();
            Map<String, Object> updates = Map.ofEntries(
                    Map.entry("Name", "Ada"), Map.entry("Age", 37), Map.entry("Active", false),
                    Map.entry("Country", "France"), Map.entry("Start", "2026-09-30"),
                    Map.entry("Notes", "hello"), Map.entry("Source", "class Example {}"),
                    Map.entry("radio:Size", "L"), Map.entry("multi-checkbox:Tags", List.of("AI")),
                    Map.entry("select:Language", "Scala"), Map.entry("color:Accent", "#112233"),
                    Map.entry("date:End", "2026-10-01"),
                    Map.entry("file:File", Map.of("name", "file.txt", "contentType", "text/plain", "size", 3, "base64", "YWJj")),
                    Map.entry("map:Map", Map.of("latitude", 41, "longitude", 15, "zoom", 12)),
                    Map.entry("tabs:Pages", "Settings"), Map.entry("sidebar:App", "Settings"),
                    Map.entry("dropdown:Export", "JSON"), Map.entry("Run", true));
            for (var update : updates.entrySet()) {
                assertEquals(200, browser.update(id(update.getKey()), update.getValue()).statusCode(), update.getKey());
                assertFalse(browser.html().contains("Runtime Error"), update.getKey());
            }
            assertEquals(400, browser.update(id("Age"), 101).statusCode());
            assertEquals(400, browser.update(id("radio:Size"), "unknown").statusCode());
            assertEquals(400, browser.update(id("file:File"), Map.of("name", "file", "contentType", "text/plain", "size", 1, "base64", "YWJj")).statusCode());
        }
    }

    @Test void numericFormEditingStaysTextualAndValidationHappensAtSubmission() throws Exception {
        var submitted = new AtomicReference<Profile>();
        try (var runtime = new HttpTestRuntime(ui -> ui.form(Profile.class).ifPresent(submitted::set))) {
            runtime.start();
            var browser = runtime.browser();
            browser.getUi();
            String prefix = "form:" + Profile.class.getName();
            for (var field : Map.of("name", "Ada", "age", "", "active", true,
                    "since", "2026-09-30", "role", "ADMIN").entrySet()) {
                assertEquals(200, browser.update(id(prefix + ":" + field.getKey()), field.getValue()).statusCode());
            }
            browser.update(id(prefix + ":submit"), true);
            assertNull(submitted.get());
            assertTrue(browser.html().contains("Cannot create Profile"));
            browser.update(id(prefix + ":age"), "37");
            browser.update(id(prefix + ":submit"), true);
            assertEquals(new Profile("Ada", 37, true, LocalDate.of(2026, 9, 30), Role.ADMIN), submitted.get());
        }
    }

    @Test void crudAllowsRenderedActionsButNotDirectModeOrSelectionChanges() throws Exception {
        var customers = new CopyOnWriteArrayList<>(List.of(new Customer("Ada")));
        try (var runtime = new HttpTestRuntime(ui -> ui.crud(Customer.class, customers))) {
            runtime.start();
            var browser = runtime.browser();
            browser.getUi();
            assertFalse(browser.html().contains("Runtime Error"));
            String crud = "crud:" + Customer.class.getName();
            assertEquals(403, browser.update(id(crud + ":mode"), "edit").statusCode());
            assertEquals(403, browser.update(id(crud + ":index"), 0).statusCode());
            assertEquals(200, browser.update(id(crud + ":new"), true).statusCode());
            assertEquals(403, browser.update(id(crud + ":delete:0"), true).statusCode());
            String form = "form:" + crud + ":create:" + Customer.class.getName();
            assertEquals(200, browser.update(id(form + ":name"), "Grace").statusCode());
            assertEquals(200, browser.update(id(form + ":submit"), true).statusCode());
            assertEquals(List.of(new Customer("Ada"), new Customer("Grace")), customers);
            browser.getUi();
            assertEquals(200, browser.update(id(crud + ":delete:0"), true).statusCode());
            assertEquals(List.of(new Customer("Grace")), customers);
        }
    }
}
