package it.jui.examples.customer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import it.jui.framework.app.JuiApp;
import it.jui.framework.app.JuiProvider;
import it.jui.framework.core.UIContext;
import it.jui.framework.server.JuiServer;

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

        ui.form(Customer.class).ifPresent(customer -> {
            customers.add(customer);
            ui.success("Customer saved");
        });

        ui.metric("Customers", customers.size());
        ui.table("Customers", customers);
    }

    public static void main(String[] args) throws Exception {
        new JuiServer(new JuiProvider(new CustomerApp())).start();
    }

    public record Customer(String name, String email, int age, boolean active) {}
}
