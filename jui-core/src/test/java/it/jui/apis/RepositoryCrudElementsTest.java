package it.jui.apis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import it.jui.UIContext;
import it.jui.data.InMemoryCrudRepository;
import it.jui.server.InMemorySessionManager;

class RepositoryCrudElementsTest {

    record Customer(long id, String name, int age) {}

    @Test
    void repositoryCrudRendersValuesAndActions() {
        var values = new ArrayList<>(List.of(new Customer(10, "Ada", 36)));
        var repository = new InMemoryCrudRepository<Customer, Long>(values, Customer::id);
        UIContext ui = new UIContext("s1", new InMemorySessionManager());

        ui.crud(Customer.class, repository);

        String html = ui.getHtml();
        assertTrue(html.contains("Ada"));
        assertTrue(html.contains("data-jui='crud-edit'"));
        assertTrue(html.contains("data-jui='crud-delete'"));
    }

    @Test
    void repositoryCrudDeletesByStableRepositoryId() {
        var values = new ArrayList<>(List.of(
                new Customer(10, "Ada", 36),
                new Customer(20, "Alan", 41)));
        var repository = new InMemoryCrudRepository<Customer, Long>(values, Customer::id);
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext ui = new UIContext("s1", sessions);

        String crudKey = "crud:repo:" + Customer.class.getName();
        String token = token(10L);
        sessions.updateState("s1", ui.getNextWidgetId(crudKey + ":delete:" + token), true);

        ui.crud(Customer.class, repository);

        assertEquals(List.of(new Customer(20, "Alan", 41)), repository.findAll());
    }

    private String token(Object id) {
        return Integer.toUnsignedString(Objects.hashCode(id), 36)
                + "-" + Integer.toUnsignedString(String.valueOf(id).hashCode(), 36);
    }
}
