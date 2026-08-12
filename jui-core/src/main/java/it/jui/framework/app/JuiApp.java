package it.jui.framework.app;

import it.jui.framework.core.UIContext;

@FunctionalInterface
public interface JuiApp {

    void run(UIContext ui);
}