package it.jui.apis;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import it.jui.UIContext;
import it.jui.server.InMemorySessionManager;

class ChartElementsTest {

    record Month(String name, int revenue, int cost) {}

    @Test
    void lineAndBarChartsDeclareApexChartsAndSerializeData() {
        UIContext ui = ui();
        ui.lineChart("Revenue", List.of("Jan", "Feb"), Map.of("Revenue", List.of(10, 20)));
        ui.barChart("Costs", List.of(3, 5));

        String html = ui.getHtml();
        assertTrue(ui.getHtmlDependencies().containsKey("apexcharts"));
        assertTrue(html.contains("ApexCharts"));
        assertTrue(html.contains("Revenue"));
        assertTrue(html.contains("Jan"));
        assertTrue(html.contains("\"type\":\"line\""));
        assertTrue(html.contains("\"type\":\"bar\""));
    }

    @Test
    void objectChartReadsRecordProperties() {
        UIContext ui = ui();
        ui.lineChart("Monthly", List.of(
                new Month("Jan", 10, 4),
                new Month("Feb", 20, 7)),
                "name", "revenue", "cost");

        String html = ui.getHtml();
        assertTrue(html.contains("Jan"));
        assertTrue(html.contains("Feb"));
        assertTrue(html.contains("revenue"));
        assertTrue(html.contains("cost"));
    }

    @Test
    void unknownChartPropertyFailsFast() {
        UIContext ui = ui();
        assertThrows(IllegalArgumentException.class,
                () -> ui.barChart("Broken", List.of(new Month("Jan", 10, 4)), "name", "missing"));
    }

    private UIContext ui() {
        return new UIContext("s1", new InMemorySessionManager());
    }
}
