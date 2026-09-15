package it.jui.framework.apis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import it.jui.framework.core.UIContext;
import it.jui.framework.server.InMemorySessionManager;

class FormElementsTest {

    enum Role { ADMIN, USER }

    record Customer(String name, int age, boolean active, LocalDate since, Role role) {}

    public static class CustomerBean {
        private String name;
        private int age;

        public CustomerBean() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    @Test
    void recordFormRendersFieldsFromJavaTypes() {
        UIContext ui = new UIContext("s1", new InMemorySessionManager());

        Optional<Customer> result = ui.form(Customer.class);

        assertTrue(result.isEmpty());
        String html = ui.getHtml();
        assertTrue(html.contains("Name"));
        assertTrue(html.contains("type='number'"));
        assertTrue(html.contains("type='checkbox'"));
        assertTrue(html.contains("type='date'"));
        assertTrue(html.contains("<select"));
        assertTrue(html.contains("Save"));
    }

    @Test
    void recordFormBuildsValueOnlyOnSave() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext seed = new UIContext("s1", sessions);

        String prefix = "form:" + Customer.class.getName();
        sessions.updateState("s1", seed.getNextWidgetId(prefix + ":name"), "Maurizio");
        sessions.updateState("s1", seed.getNextWidgetId(prefix + ":age"), 52L);
        sessions.updateState("s1", seed.getNextWidgetId(prefix + ":active"), true);
        sessions.updateState("s1", seed.getNextWidgetId(prefix + ":since"), "2026-09-11");
        sessions.updateState("s1", seed.getNextWidgetId(prefix + ":role"), "ADMIN");
        sessions.updateState("s1", seed.getNextWidgetId(prefix + ":submit"), true);

        UIContext submitRender = new UIContext("s1", sessions);
        Optional<Customer> result = submitRender.form(Customer.class);

        assertTrue(result.isPresent());
        assertEquals("Maurizio", result.get().name());
        assertEquals(52, result.get().age());
        assertTrue(result.get().active());
        assertEquals(LocalDate.of(2026, 9, 11), result.get().since());
        assertEquals(Role.ADMIN, result.get().role());

        UIContext nextRender = new UIContext("s1", sessions);
        assertFalse(nextRender.form(Customer.class).isPresent());
    }

    @Test
    void editRecordUsesInitialValues() {
        UIContext ui = new UIContext("s1", new InMemorySessionManager());
        Customer customer = new Customer("Anna", 31, true, LocalDate.of(2025, 1, 2), Role.USER);

        ui.form(customer);

        String html = ui.getHtml();
        assertTrue(html.contains("value='Anna'"));
        assertTrue(html.contains("value='31'"));
        assertTrue(html.contains("checked"));
        assertTrue(html.contains("value='2025-01-02'"));
        assertTrue(html.contains("value='USER' selected"));
    }

    @Test
    void beanFormUsesGettersSettersAndNoArgConstructor() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext seed = new UIContext("s1", sessions);
        String prefix = "form:" + CustomerBean.class.getName();

        sessions.updateState("s1", seed.getNextWidgetId(prefix + ":age"), 40L);
        sessions.updateState("s1", seed.getNextWidgetId(prefix + ":name"), "Luca");
        sessions.updateState("s1", seed.getNextWidgetId(prefix + ":submit"), true);

        UIContext ui = new UIContext("s1", sessions);
        Optional<CustomerBean> result = ui.form(CustomerBean.class);

        assertTrue(result.isPresent());
        assertEquals("Luca", result.get().getName());
        assertEquals(40, result.get().getAge());
    }

    @Test
    void invalidNumericValueReturnsEmptyAndShowsError() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext seed = new UIContext("s1", sessions);
        String prefix = "form:" + Customer.class.getName();

        sessions.updateState("s1", seed.getNextWidgetId(prefix + ":age"), "not-a-number");
        sessions.updateState("s1", seed.getNextWidgetId(prefix + ":submit"), true);

        UIContext ui = new UIContext("s1", sessions);
        Optional<Customer> result = ui.form(Customer.class);

        assertTrue(result.isEmpty());
        assertTrue(ui.getHtml().contains("Cannot create Customer"));
    }
}
