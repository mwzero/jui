package com.jui.processors;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jui.utils.FS;

import lombok.extern.java.Log;

@Log
public class HtmlProcessor {

	private Properties emojiProperties;
	
	public HtmlProcessor() {
    	
        emojiProperties = new Properties();
        try {
        	Path filePath = FS.getFilePath("emoji.properties", Map.of("classLoading","True"));
        	FileInputStream fis = new FileInputStream(filePath.toString());
        	log.fine("Loading FILE:" + filePath.toString());
    		
            emojiProperties.load(fis);
            
        } catch (IOException | URISyntaxException e) {
			log.severe("emoji properties file not loaded. Err:" + e.getLocalizedMessage());
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
