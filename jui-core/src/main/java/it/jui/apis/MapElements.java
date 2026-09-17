package it.jui.apis;

import java.util.Map;

import it.jui.UIContext;
import it.jui.map.MapState;

public class MapElements extends BaseElements {

    public MapElements(UIContext ctx) {
        super(ctx);
    }

    /**
     * Renders a Leaflet map and returns its current center/zoom. Browser move and
     * zoom events are sent back through normal JUI session state and rerun flow.
     */
    public MapState map(String title, double latitude, double longitude, int zoom) {
        String widgetId = ctx.getNextWidgetId("map:" + title);
        MapState state = readState(ctx.getRawValue(widgetId), new MapState(latitude, longitude, zoom));
        String domId = widgetId + "-map";

        ctx.addHtmlDependency("leaflet-css", "<link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\" crossorigin=\"\"/>");
        ctx.addHtmlDependency("leaflet-js", "<script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\" crossorigin=\"\"></script>");

        ctx.addHtml(String.format(
            "<div class='mb-6 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm overflow-hidden'>" +
            "<div class='px-4 py-3 bg-gray-50 dark:bg-gray-900/40 border-b border-gray-200 dark:border-gray-700'>" +
            "<h3 class='text-sm font-semibold text-gray-800 dark:text-gray-100'>%s</h3></div>" +
            "<div class='w-full h-64' id='%s'></div>" +
            "<script>(function(){if(!window.L)return;var m=L.map('%s').setView([%s,%s],%d);" +
            "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{maxZoom:19,attribution:'© OpenStreetMap'}).addTo(m);" +
            "m.on('moveend zoomend',function(){var c=m.getCenter();sendUpdate('%s',{latitude:c.lat,longitude:c.lng,zoom:m.getZoom()});});})();</script></div>",
            escapeHtml(title), domId, domId,
            Double.toString(state.latitude()), Double.toString(state.longitude()), state.zoom(), widgetId));
        return state;
    }

    private MapState readState(Object raw, MapState fallback) {
        if (raw instanceof MapState state) return state;
        if (!(raw instanceof Map<?, ?> map)) return fallback;
        return new MapState(
                number(map.get("latitude"), fallback.latitude()).doubleValue(),
                number(map.get("longitude"), fallback.longitude()).doubleValue(),
                number(map.get("zoom"), fallback.zoom()).intValue());
    }

    private Number number(Object value, Number fallback) {
        return value instanceof Number number ? number : fallback;
    }
}
