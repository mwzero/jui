package com.jui.processors;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlProcessor {

	private Properties emojiProperties;
	
	public HtmlProcessor(String propertiesFilePath) throws IOException {
    	
        emojiProperties = new Properties();
        try (FileInputStream fis = new FileInputStream(propertiesFilePath)) {
            emojiProperties.load(fis);
        }
    }
	
	protected String replaceEmojis(String input) {
		
		Pattern emojiPattern = Pattern.compile(":(\\w+):");
        
		Matcher matcher = emojiPattern.matcher(input);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            String emoji = emojiProperties.getProperty(key, key);
            matcher.appendReplacement(result, Matcher.quoteReplacement(emoji));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
	
	public String convertTextToHtml(String input) {
        
		if (input == null || input.isEmpty()) {
            return "";
        }

        String html = input
                .replace("&", "&amp;")  
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        html = html
                .replace("\n", "<br>")
                .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");

        String colorPattern = ":([a-zA-Z]+)\\[([^\\]]+)]";
        Pattern pattern = Pattern.compile(colorPattern);
        Matcher matcher = pattern.matcher(html);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String color = matcher.group(1);  // Es. "blue"
            String text = matcher.group(2);   // Es. "cool"
            String replacement = String.format("<span style=\"color:%s;\">%s</span>", color, text);
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        String result = replaceEmojis(sb.toString());

        return result;
    }
    
    

    

   

}
