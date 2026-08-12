package it.jui.framework.core;

import java.io.File;

public final class HotReloadAppProvider implements AppProvider {
    
  private final HotReloadService hotReload;
  
  public HotReloadAppProvider(File sourceFile) {
    this.hotReload = new HotReloadService(sourceFile);
  }
  
  @Override public UIApp getApp() { return hotReload.getApp(); }
}