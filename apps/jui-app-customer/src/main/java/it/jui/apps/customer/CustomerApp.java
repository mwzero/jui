package it.jui.apps.customer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import it.jui.Jui;
import it.jui.JuiApp;
import it.jui.UIContext;

/**
 * Canonical JUI example used as a deployment smoke test and LLM-generation benchmark.
 * Data is intentionally in-memory and therefore not durable across container restarts.
 */
public class CustomerApp implements JuiApp {

    private final List<Customer> customers = new CopyOnWriteArrayList<>(List.of(
            new Customer("Ada Lovelace", "ada@example.com", 36, true),
            new Customer("Alan Turing", "alan@example.com", 41, true)));

    @Override
    public void run(UIContext ui) {
        ui.title("Customer Manager");
        ui.text("A compact JUI app generated from Java types.");
        ui.metric("Customers", customers.size());
        ui.crud(Customer.class, customers);
    }

    public static void main(String[] args) throws Exception {
        Jui.run(new CustomerApp());
    }

    public record Customer(String name, String email, int age, boolean active) {}
}
