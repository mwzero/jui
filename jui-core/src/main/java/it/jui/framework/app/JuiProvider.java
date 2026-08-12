package it.jui.framework.app;
import java.util.function.Consumer;

import it.jui.framework.core.HotReloadService;
import it.jui.framework.core.UIContext;
import lombok.Getter;

public class JuiProvider {
    
    private final JuiApp app;
    private final HotReloadService hotReload;
  
    public JuiProvider(JuiApp app) {
        this(app, null);
    }
 
    public JuiProvider(String srcFile) {
        this(null, srcFile);
    }

    private JuiProvider(JuiApp app, String srcFile) {
        this.app = app;
        this.hotReload = (srcFile != null) ? new HotReloadService(srcFile) : null;
    }

    public JuiApp getApp() {

        if (app != null) {
            return app;
        } else if (hotReload != null) {
            return hotReload.getApp();
        } else {
            throw new IllegalStateException("No app or source file provided.");
        }
    }
}
