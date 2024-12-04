package com.jui.processors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlProcessor {

	public static String convertTextToHtml(String text) {
        if (text == null) {
            return "";
        }

        String html = text
                .replace("&", "&amp;")  
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        html = html
                .replace("\n", "<br>")
                .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");

        html = convert(html);
        
        return html;
    }
	
    protected static String convert(String input) {
    	
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Pattern per il colore in formato :blue[text]
        String colorPattern = ":([a-zA-Z]+)\\[([^\\]]+)]";
        Pattern pattern = Pattern.compile(colorPattern);
        Matcher matcher = pattern.matcher(input);

        // Sostituzione del colore
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String color = matcher.group(1);  // Es. "blue"
            String text = matcher.group(2);   // Es. "cool"
            String replacement = String.format("<span style=\"color:%s;\">%s</span>", color, text);
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        // Sostituzione dell'emoji :sunglasses:
        String result = sb.toString().replace(":sunglasses:", "&#128526;");

        return result;
    }

}
