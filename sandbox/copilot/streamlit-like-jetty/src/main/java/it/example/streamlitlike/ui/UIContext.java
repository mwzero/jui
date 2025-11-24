
package it.example.streamlitlike.ui;

import java.util.List;
import java.util.Map;

public class UIContext {
    private final Map<String, String> state;
    private final Map<String, String> params;
    private final StringBuilder html = new StringBuilder();

    public UIContext(Map<String, String> state, Map<String, String> params) {
        this.state = state;
        this.params = params;
    }

    public void sidebar(Runnable content) {
        html.append("<div style='width:280px;border-right:1px solid #ccc;padding:10px;'>");
        content.run();
        html.append("</div>");
    }

    public void title(String text) {
        html.append("<h2>").append(escape(text)).append("</h2>");
    }

    public String textInput(String label, String key) {
        String value = state.getOrDefault(key, "");
        html.append("<label for='").append(key).append("'>").append(escape(label)).append("</label><br>");
        html.append("<input type='text' id='").append(key).append("' value='").append(escape(value))
            .append("' oninput=\"sendEvent('").append(key).append("')\"><br>");
        return value;
    }

    public boolean checkbox(String label, String key) {
        boolean checked = Boolean.parseBoolean(state.getOrDefault(key, "false"));
        html.append("<label><input type='checkbox' id='").append(key).append("' ")
            .append(checked ? "checked" : "")
            .append(" onchange=\"sendEventCheckbox('").append(key).append("')\"> ")
            .append(escape(label)).append("</label><br>");
        return checked;
    }

    public String selectBox(String label, String key, List<String> options) {
        String selected = state.getOrDefault(key, options.isEmpty() ? "" : options.get(0));
        html.append("<label for='").append(key).append("'>").append(escape(label)).append("</label><br>");
        html.append("<select id='").append(key).append("' onchange=\"sendEvent('").append(key).append("')\">");
        for (String opt : options) {
            html.append("<option value='").append(escape(opt)).append("'")
                .append(opt.equals(selected) ? " selected" : "")
                .append(">").append(escape(opt)).append("</option>");
        }
        html.append("</select><br>");
        return selected;
    }

    public void fileUpload(String label, String key) {
        html.append("<label for='").append(key).append("'>").append(escape(label)).append("</label><br>");
        html.append("<input type='file' id='").append(key).append("' /> ");
        html.append("<button type='button' onclick=\"sendFile('").append(key).append("')\">Upload</button><br>");
    }

    public void error(String msg) {
        html.append("<p style='color:#b00020;'>").append(escape(msg)).append("</p>");
    }

    public void success(String msg) {
        html.append("<p style='color:#0b7a0b;'>").append(escape(msg)).append("</p>");
    }

    public String render() {
        return html.toString();
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("&", "&amp;")
                                 .replace("<", "&lt;")
                                 .replace(">", "&gt;");
    }
}
