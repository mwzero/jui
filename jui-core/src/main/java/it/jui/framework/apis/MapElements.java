package it.jui.framework.apis;

import it.jui.framework.core.UIContext;

public class MapElements extends BaseElements {

    int mapIdCounter = 0;
    
    public MapElements(UIContext ctx) {
        super(ctx);
    }

    public void map(String title, double latitude, double longitude, int zoom) {

        ctx.addHtmlDependency("leaflet-css", "<link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\" integrity=\"sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=\" crossorigin=\"\"/>");
        ctx.addHtmlDependency("leaflet-js", "<script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\" integrity=\"sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=\" crossorigin=\"\"></script>");

        String mapHtml = String.format(
            "<div class='mb-6 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm'>" +
            "<div class='px-4 py-3 bg-gray-50 dark:bg-gray-900/40 border-b border-gray-200 dark:border-gray-700'>" +
            "<h3 class='text-sm font-semibold text-gray-800 dark:text-gray-100'>%s</h3>" +
            "</div>" +
            "<div class='w-full h-64' id='map-%d'></div>" +
            "<script>" +
            "  var map = L.map('map-%d').setView([%f, %f], %d);" +
            "  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {" +
            "    maxZoom: 19," +
            "    attribution: '© OpenStreetMap'" +
            "  }).addTo(map);" +
            "</script>" +
            "</div>",
            title, mapIdCounter, mapIdCounter, latitude, longitude, zoom);

        ctx.addHtml(mapHtml);
        mapIdCounter++;
    }

}
