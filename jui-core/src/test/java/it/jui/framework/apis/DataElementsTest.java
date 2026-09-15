package it.jui.framework.apis;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import it.jui.framework.core.UIContext;
import it.jui.framework.server.InMemorySessionManager;

class DataElementsTest {

    record Customer(String name, String email, int age) {}

    static class Product {
        private final String name;
        private final double price;

        Product(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }

    @Test
    void recordTableInfersDeclaredComponents() {
        UIContext ui = context();

        ui.table(List.of(
                new Customer("Mario", "mario@example.com", 42),
                new Customer("Anna", "anna@example.com", 35)));

        String html = ui.getHtml();
        assertTrue(html.contains(">Name<"));
        assertTrue(html.contains(">Email<"));
        assertTrue(html.contains(">Age<"));
        assertTrue(html.indexOf(">Name<") < html.indexOf(">Email<"));
        assertTrue(html.indexOf(">Email<") < html.indexOf(">Age<"));
        assertTrue(html.contains("Mario"));
        assertTrue(html.contains("42"));
    }

    @Test
    void pojoTableUsesSortedBeanProperties() {
        UIContext ui = context();

        ui.table("Products", List.of(new Product("Keyboard", 99.9)));

        String html = ui.getHtml();
        assertTrue(html.contains("Products"));
        assertTrue(html.contains(">Name<"));
        assertTrue(html.contains(">Price<"));
        assertTrue(html.indexOf(">Name<") < html.indexOf(">Price<"));
        assertTrue(html.contains("Keyboard"));
        assertTrue(html.contains("99.9"));
    }

    @Test
    void mapTableUsesStableSortedKeys() {
        UIContext ui = context();

        ui.table(List.of(Map.of("city", "Napoli", "population", 900000)));

        String html = ui.getHtml();
        assertTrue(html.contains(">City<"));
        assertTrue(html.contains(">Population<"));
        assertTrue(html.indexOf(">City<") < html.indexOf(">Population<"));
        assertTrue(html.contains("Napoli"));
    }

    @Test
    void scalarTableUsesSingleValueColumn() {
        UIContext ui = context();

        ui.table(List.of("Napoli", "Roma"));

        String html = ui.getHtml();
        assertTrue(html.contains(">Value<"));
        assertTrue(html.contains("Napoli"));
        assertTrue(html.contains("Roma"));
    }

    @Test
    void emptyListRendersEmptyStateWithoutGuessingSchema() {
        UIContext ui = context();

        ui.table("Customers", List.of());

        String html = ui.getHtml();
        assertTrue(html.contains("Customers"));
        assertTrue(html.contains("data-jui='table-empty'"));
        assertTrue(html.contains("No data"));
    }

    @Test
    void inferredValuesAreEscaped() {
        UIContext ui = context();

        ui.table(List.of(new Customer("<script>alert(1)</script>", "x@y.z", 1)));

        String html = ui.getHtml();
        assertTrue(html.contains("&lt;script&gt;alert(1)&lt;/script&gt;"));
        assertFalse(html.contains("<script>alert(1)</script>"));
    }

    private UIContext context() {
        return new UIContext("test", new InMemorySessionManager());
    }
}
