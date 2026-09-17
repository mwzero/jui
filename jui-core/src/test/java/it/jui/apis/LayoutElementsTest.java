package it.jui.apis;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import it.jui.UIContext;
import it.jui.server.InMemorySessionManager;

class LayoutElementsTest {

    @Test
    void compositeLayoutsCaptureNestedContentAndEscapeLabels() {
        UIContext ui = ui();

        ui.container(() -> ui.text("Container body"));
        ui.columns(() -> ui.text("Column A"), () -> ui.text("Column B"));
        ui.expander("<More>", () -> ui.text("Expanded body"));
        ui.popover("<Help>", () -> ui.text("Popover body"));
        ui.dialog("<Details>", () -> ui.text("Dialog body"));
        ui.card("<Card>", "<Body>");

        String html = ui.getHtml();
        assertTrue(html.contains("Container body"));
        assertTrue(html.contains("md:grid-cols-2"));
        assertTrue(html.contains("Column A"));
        assertTrue(html.contains("Column B"));
        assertTrue(html.contains("Expanded body"));
        assertTrue(html.contains("Popover body"));
        assertTrue(html.contains("Dialog body"));
        assertTrue(html.contains("<dialog"));
        assertTrue(html.contains("&lt;More&gt;"));
        assertTrue(html.contains("&lt;Help&gt;"));
        assertTrue(html.contains("&lt;Details&gt;"));
        assertTrue(html.contains("&lt;Card&gt;"));
        assertTrue(html.contains("&lt;Body&gt;"));
        assertFalse(html.contains("<Card>"));
    }

    @Test
    void emptyColumnsRenderNothing() {
        UIContext ui = ui();
        ui.columns();
        assertTrue(ui.getHtml().isEmpty());
    }

    @Test
    void metricRendersLabelAndNumericValue() {
        UIContext ui = ui();

        ui.metric("Revenue", 125000);

        String html = ui.getHtml();
        assertTrue(html.contains("Revenue"));
        assertTrue(html.contains("125000"));
        assertTrue(html.contains("data-jui='metric'"));
        assertFalse(html.contains("data-jui='metric-trend'"));
    }

    @Test
    void metricInfersPositiveTrend() {
        UIContext ui = ui();

        ui.metric("Revenue", 125000, "+12%");

        String html = ui.getHtml();
        assertTrue(html.contains("+12%"));
        assertTrue(html.contains("▲"));
        assertTrue(html.contains("text-green-600"));
    }

    @Test
    void metricInfersNegativeTrend() {
        UIContext ui = ui();

        ui.metric("Churn", 4.2, "-1.3%");

        String html = ui.getHtml();
        assertTrue(html.contains("-1.3%"));
        assertTrue(html.contains("▼"));
        assertTrue(html.contains("text-red-600"));
    }

    @Test
    void metricUsesNeutralStyleWhenTrendHasNoSign() {
        UIContext ui = ui();

        ui.metric("Users", 42, "stable");

        String html = ui.getHtml();
        assertTrue(html.contains("stable"));
        assertTrue(html.contains("text-gray-500"));
        assertFalse(html.contains("▲"));
        assertFalse(html.contains("▼"));
    }

    @Test
    void metricEscapesContent() {
        UIContext ui = ui();

        ui.metric("<b>Revenue</b>", "<script>x</script>", "+<img>");

        String html = ui.getHtml();
        assertTrue(html.contains("&lt;b&gt;Revenue&lt;/b&gt;"));
        assertTrue(html.contains("&lt;script&gt;x&lt;/script&gt;"));
        assertTrue(html.contains("+&lt;img&gt;"));
        assertFalse(html.contains("<script>x</script>"));
    }

    @Test
    void legacyMetricCardStillWorks() {
        UIContext ui = ui();

        ui.metricCard("Revenue", "100", "+5%", true);

        String html = ui.getHtml();
        assertTrue(html.contains("Revenue"));
        assertTrue(html.contains("100"));
        assertTrue(html.contains("+5%"));
        assertTrue(html.contains("▲"));
    }

    private UIContext ui() {
        return new UIContext("s1", new InMemorySessionManager());
    }
}
