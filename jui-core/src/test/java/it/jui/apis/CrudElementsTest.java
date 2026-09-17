package it.jui.apis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import it.jui.UIContext;
import it.jui.server.InMemorySessionManager;

class CrudElementsTest {

    record Customer(String name, int age, boolean active) {}

    @Test
    void listModeRendersTableAndActions() {
        List<Customer> customers = new ArrayList<>(List.of(new Customer("Ada", 36, true)));
        UIContext ui = new UIContext("s1", new InMemorySessionManager());

        ui.crud(Customer.class, customers);

        String html = ui.getHtml();
        assertTrue(html.contains("+ New Customer"));
        assertTrue(html.contains("Ada"));
        assertTrue(html.contains("data-jui='crud-edit'"));
        assertTrue(html.contains("data-jui='crud-delete'"));
    }

    @Test
    void createFlowAddsValueToMutableList() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        List<Customer> customers = new ArrayList<>();
        String crudKey = "crud:" + Customer.class.getName();

        UIContext clickNew = new UIContext("s1", sessions);
        sessions.updateState("s1", clickNew.getNextWidgetId(crudKey + ":new"), true);
        clickNew.crud(Customer.class, customers);
        assertTrue(clickNew.getHtml().contains("New Customer"));

        String formKey = "form:" + crudKey + ":create:" + Customer.class.getName();
        UIContext seed = new UIContext("s1", sessions);
        sessions.updateState("s1", seed.getNextWidgetId(formKey + ":name"), "Grace");
        sessions.updateState("s1", seed.getNextWidgetId(formKey + ":age"), "40");
        sessions.updateState("s1", seed.getNextWidgetId(formKey + ":active"), true);
        sessions.updateState("s1", seed.getNextWidgetId(formKey + ":submit"), true);

        UIContext save = new UIContext("s1", sessions);
        save.crud(Customer.class, customers);

        assertEquals(1, customers.size());
        assertEquals(new Customer("Grace", 40, true), customers.get(0));
        assertTrue(save.getHtml().contains("Customer saved"));
    }

    @Test
    void editFlowReplacesSelectedListValue() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        List<Customer> customers = new ArrayList<>(List.of(new Customer("Ada", 36, true)));
        String crudKey = "crud:" + Customer.class.getName();

        UIContext clickEdit = new UIContext("s1", sessions);
        sessions.updateState("s1", clickEdit.getNextWidgetId(crudKey + ":edit:0"), true);
        clickEdit.crud(Customer.class, customers);
        assertTrue(clickEdit.getHtml().contains("Edit Customer"));

        String formKey = "form:" + crudKey + ":edit:0:" + Customer.class.getName();
        UIContext seed = new UIContext("s1", sessions);
        sessions.updateState("s1", seed.getNextWidgetId(formKey + ":name"), "Ada Updated");
        sessions.updateState("s1", seed.getNextWidgetId(formKey + ":age"), "37");
        sessions.updateState("s1", seed.getNextWidgetId(formKey + ":active"), false);
        sessions.updateState("s1", seed.getNextWidgetId(formKey + ":submit"), true);

        UIContext save = new UIContext("s1", sessions);
        save.crud(Customer.class, customers);

        assertEquals(new Customer("Ada Updated", 37, false), customers.get(0));
        assertTrue(save.getHtml().contains("Customer updated"));
    }

    @Test
    void deleteRemovesSelectedRow() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        List<Customer> customers = new ArrayList<>(List.of(
                new Customer("Ada", 36, true),
                new Customer("Alan", 41, true)));
        String crudKey = "crud:" + Customer.class.getName();

        UIContext ui = new UIContext("s1", sessions);
        sessions.updateState("s1", ui.getNextWidgetId(crudKey + ":delete:0"), true);
        ui.crud(Customer.class, customers);

        assertEquals(List.of(new Customer("Alan", 41, true)), customers);
    }

    @Test
    void cancelReturnsToListWithoutCreatingValue() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        List<Customer> customers = new ArrayList<>();
        String crudKey = "crud:" + Customer.class.getName();

        UIContext clickNew = new UIContext("s1", sessions);
        sessions.updateState("s1", clickNew.getNextWidgetId(crudKey + ":new"), true);
        clickNew.crud(Customer.class, customers);

        UIContext cancel = new UIContext("s1", sessions);
        sessions.updateState("s1", cancel.getNextWidgetId(crudKey + ":cancel"), true);
        cancel.crud(Customer.class, customers);

        assertTrue(customers.isEmpty());
        assertTrue(cancel.getHtml().contains("+ New Customer"));
    }
}
