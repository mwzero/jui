package it.jui.framework.apis;

import it.jui.framework.core.UIContext;

public class BaseElements {

    protected UIContext ctx;

    public BaseElements(UIContext ctx) {
        this.ctx = ctx;
    }

    protected String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&#39;");
    }
    
    
}