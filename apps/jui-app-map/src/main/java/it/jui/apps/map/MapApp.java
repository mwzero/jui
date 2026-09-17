package it.jui.apps.map;

import it.jui.Jui;
import it.jui.JuiApp;
import it.jui.UIContext;
import it.jui.map.MapState;

public class MapApp implements JuiApp {

    @Override
    public void run(UIContext ui) {
        ui.title("Interactive Map", "map");
        ui.markdown("Pan or zoom the map. **MapState** is sent back through the same JUI session/rerun transport used by other widgets.");

        MapState state = ui.map("Naples", 40.8518, 14.2681, 12);
        ui.columns(
                () -> ui.metric("Latitude", "%.4f".formatted(state.latitude())),
                () -> ui.metric("Longitude", "%.4f".formatted(state.longitude())),
                () -> ui.metric("Zoom", state.zoom()));
    }

    public static void main(String[] args) throws Exception {
        Jui.run(new MapApp());
    }
}
