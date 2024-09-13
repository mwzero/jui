package com.jui;

import java.util.UUID;

public class Button extends UIComponent {
    private String label;
    private Runnable onClick;
    private String id;

    public Button(String label, Runnable onClick) {
        this.label = label;
        this.onClick = onClick;
        this.id = UUID.randomUUID().toString(); // Genera un ID univoco
    }

    public String getId() {
        return id;
    }

    @Override
    public String render() {
        return "<button onclick=\"sendClick('" + id + "')\">" + label + "</button>";
    }

    public void click() {
        onClick.run();
    }
}
