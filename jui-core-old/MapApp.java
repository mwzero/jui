import it.jui.framework.core.*;
import java.util.*;

public class MapApp implements UIApp {

    @Override
    public void run(UIContext ui) {

        ui.title("Hello JUI");
        ui.header("Map Elements");
        
        ui.map("Neapolis", 40.8518, 14.2681, 10);

        /*
        ui.mapElement("Neapolis", 40.8518, 14.2681)
          .title("Naples")
          .description("A beautiful city in Italy.")
          .icon("https://example.com/naples-icon.png");
        */
    }
}
