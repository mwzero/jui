package it.jui.examples.map;

import it.jui.framework.Jui;
import it.jui.framework.app.JuiApp;
import it.jui.framework.core.UIContext;
import it.jui.framework.map.MapState;

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
