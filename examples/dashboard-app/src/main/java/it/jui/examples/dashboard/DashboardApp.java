package it.jui.examples.dashboard;

import java.util.List;

import it.jui.framework.app.JuiApp;
import it.jui.framework.app.JuiProvider;
import it.jui.framework.core.UIContext;
import it.jui.framework.server.JuiServer;

public class DashboardApp implements JuiApp {

    private final List<Sale> sales = List.of(
            new Sale("Jan", 18_200, 124),
            new Sale("Feb", 21_600, 143),
            new Sale("Mar", 24_900, 161),
            new Sale("Apr", 27_300, 176));

    @Override
    public void run(UIContext ui) {
        ui.title("Sales Dashboard", "monitoring");
        ui.sidebar("Dashboard", List.of("Overview", "Sales"), "Overview", page -> {
            if (page.equals("Sales")) renderSales(ui);
            else renderOverview(ui);
        });
    }

    private void renderOverview(UIContext ui) {
        Sale latest = sales.getLast();
        ui.columns(
                () -> ui.metric("Revenue", "€" + latest.revenue(), "+9.6%"),
                () -> ui.metric("Orders", latest.orders(), "+6.1%"),
                () -> ui.metric("Months", sales.size()));
        ui.lineChart("Revenue", sales, "month", "revenue");
        ui.barChart("Orders", sales, "month", "orders");
        ui.info("Charts are generated from Java records; no chart configuration DSL is required.");
    }

    private void renderSales(UIContext ui) {
        ui.header("Monthly sales");
        ui.table("Sales", sales);
    }

    public static void main(String[] args) throws Exception {
        new JuiServer(new JuiProvider(new DashboardApp())).start();
    }

    public record Sale(String month, int revenue, int orders) {}
}
