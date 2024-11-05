package com.jui.helpers;

import lombok.Builder;

@Builder
public class MarkdownProcessor {

    public String render(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }

        // Process lines one by one
        StringBuilder html = new StringBuilder();
        String[] lines = markdown.split("\n");

        for (String line : lines) {
            html.append(renderLine(line)).append("\n");
        }

        return html.toString().trim();
    }

    private String renderLine(String line) {
        line = line.trim();

        if (line.startsWith("# ")) {
            return "<h1>" + line.substring(2).trim() + "</h1>";
        } else if (line.startsWith("## ")) {
            return "<h2>" + line.substring(3).trim() + "</h2>";
        } else if (line.startsWith("### ")) {
            return "<h3>" + line.substring(4).trim() + "</h3>";
        } else if (line.startsWith("#### ")) {
            return "<h4>" + line.substring(5).trim() + "</h4>";
        } else if (line.startsWith("##### ")) {
            return "<h5>" + line.substring(6).trim() + "</h5>";
        } else if (line.startsWith("###### ")) {
            return "<h6>" + line.substring(7).trim() + "</h6>";
        } else if (line.startsWith("* ")) {
            return "<ul><li>" + line.substring(2).trim() + "</li></ul>";
        } else if (line.startsWith("- ")) {
            return "<ul><li>" + line.substring(2).trim() + "</li></ul>";
        } else if (line.startsWith("1. ")) {
            return "<ol><li>" + line.substring(3).trim() + "</li></ol>";
        } else {
            line = renderInline(line);
            return "<p>" + line + "</p>";
        }
    }

    private String renderInline(String text) {
        // Bold
        text = text.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
        text = text.replaceAll("__(.*?)__", "<strong>$1</strong>");

        // Italic
        text = text.replaceAll("\\*(.*?)\\*", "<em>$1</em>");
        text = text.replaceAll("_(.*?)_", "<em>$1</em>");

        return text;
    }
}
