package it.jui.framework.core;

public final class StaticAppProvider implements AppProvider {
  
    private final UIApp app;
  
    public StaticAppProvider(UIApp app) { this.app = app; }
    
    @Override public UIApp getApp() { return app; }
}
