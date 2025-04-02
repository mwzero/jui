package com.jui.template;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jui.utils.FS;

import lombok.extern.java.Log;

/**
 * The TemplateEngine class is responsible for rendering templates with dynamic content.
 * It supports loading templates from files, replacing variables, handling conditionals,
 * and iterating over collections within the templates.
 * 
 * This class provides methods to render templates by substituting placeholders with
 * actual values from a provided context. It can process loops and conditionals to
 * generate dynamic HTML or other text-based content.
 * 
 * Features:
 * - Load templates from files or strings.
 * - Replace variables with values from a context map.
 * - Handle conditional blocks based on boolean values.
 * - Iterate over collections and arrays to render repeated content.
 * 
 * Example usage:
 * <pre>
 * {@code
 * TemplateEngine engine = new TemplateEngine();
 * Map<String, Object> context = new HashMap<>();
 * context.put("name", "John");
 * String template = "Hello, {{name}}!";
 * String result = engine.render(template, context);
 * System.out.println(result); // Outputs: Hello, John!
 * }
 * </pre>
 * 
 * This class is designed to be flexible and extensible, allowing for easy integration
 * into various applications that require dynamic content rendering.
 * 
 * <pre>
 * an exaustive example: 
 * <ul class="nav flex-column">
 * {{#items}}
 *   <li class="nav-item">
 *     {{?this.link}}
 *       <a class="nav-link" href="#" data-url="{{this.link}}">
 *     {{/this.link}}
 *     {{^link}}
 *       <a class="nav-link" href="#" data-content="{{this.content}}">
 *     {{/link}}
 *         <i class="bi bi-{{icon}}-fill"></i> <span>{{this.label}}</span>
 *       </a>
 *   </li>
 * {{/items}}
* </ul>
* </pre>
  */
@Log
public class TemplateEngine {
	
	boolean classLoading;
	String folder;

    String templateContent;

    public TemplateEngine() {
        this.classLoading = false;
        this.folder = null;
    }
	
    public TemplateEngine(boolean templateClassLoading, String templateFolder) {
		
		this.classLoading = templateClassLoading;
		this.folder = templateFolder;
        
    }

    public TemplateEngine compileFromFile(String templateName) throws Exception {
		
	    String fileName;
	    if (folder != null)
	        fileName = this.folder + "/" + templateName.replace(".", "/") + ".ftl";
	    else
	        fileName = templateName.replace(".", "/") + ".ftl";

        templateContent = FS.getFileContentAsString(fileName, classLoading);
        return this;
	    
	}

    public TemplateEngine compile(String template) throws IOException, URISyntaxException {
		
        templateContent = template;
        return this;
	    
	}

    public String execute(Map<String, Object> context) {

        return this.render(templateContent, context);
    }

    protected String render(String template, Map<String, Object> context) {
        String result = renderLoops(template, context);
        result = renderConditionals(result, context);
        return renderVariables(result, context);
    }

    private String renderVariables(String template, Map<String, Object> context) {
        Pattern pattern = Pattern.compile("\\{\\{([^{}]+)\\}\\}");
        Matcher matcher = pattern.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1).trim();
            Object value = resolveKey(context, key);
            matcher.appendReplacement(result, Matcher.quoteReplacement(value != null ? value.toString() : ""));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private Object resolveKey(Map<String, Object> context, String key) {
        if (context == null || key == null || key.isEmpty()) {
            log.warning("Invalid context or key");
            return null;
        }
        String[] parts = key.split("\\.");
        Object current = context;

        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                current = getFieldValue(current, part);
            }
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private Object getFieldValue(Object obj, String fieldName) {
        try {
            return obj.getClass().getField(fieldName).get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                String methodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                return obj.getClass().getMethod(methodName).invoke(obj);
            } catch (Exception ex) {
                return null;
            }
        }
    }


    private String renderConditionals(String template, Map<String, Object> context) {
        Pattern pattern = Pattern.compile("\\{\\{\\?([a-zA-Z0-9_]+)\\}\\}([\\s\\S]+?)\\{\\{/\\1\\}\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String conditionKey = matcher.group(1).trim();
            String block = matcher.group(2);
            boolean condition = evaluateCondition(context, conditionKey);
            

            if (condition) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(block));
            } else {
                matcher.appendReplacement(result, "");
            }
        }
        matcher.appendTail(result);

        pattern = Pattern.compile("\\{\\{\\^([a-zA-Z0-9_]+)\\}\\}(.+?)\\{\\{/\\1\\}\\}", Pattern.DOTALL);
        matcher = pattern.matcher(result.toString());
        StringBuffer conditionalResult = new StringBuffer();

        while (matcher.find()) {
            String conditionKey = matcher.group(1).trim();
            String block = matcher.group(2);
            boolean condition = evaluateCondition(context, conditionKey);

            if (!condition) {
                matcher.appendReplacement(conditionalResult, Matcher.quoteReplacement(block));
            } else {
                matcher.appendReplacement(conditionalResult, "");
            }
        }
        matcher.appendTail(conditionalResult);
        return conditionalResult.toString();
    }
    
    private boolean evaluateCondition(Map<String, Object> context, String conditionKey) {
    	
    	boolean condition = true;
        
        if ( context.containsKey(conditionKey)) {
        	
        	if ( context.get(conditionKey) != null ) {
            	if ( context.get(conditionKey) instanceof Boolean ) {
            		
            		condition = (boolean) context.get(conditionKey);
            		
            	} else
            		condition = true;
        	} else
        		condition = false;
        		
        } else
        	condition = false;
        
        return condition;
    	
    }

    private String renderLoops(String template, Map<String, Object> context) {

    	Pattern pattern = Pattern.compile("\\{\\{#([a-zA-Z0-9_.]+)\\}\\}([\\s\\S]+?)\\{\\{/\\1\\}\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String listKey = matcher.group(1).trim();
            String block = matcher.group(2);

            Object listObject = resolveKey(context,listKey);
            if (listObject instanceof List<?>) {
                List<?> list = (List<?>) listObject;
                StringBuilder loopResult = new StringBuilder();
                int idx =0;
                for (Object item : list) {
                    Map<String, Object> itemContext = new HashMap<>(context);
                    if (item instanceof Map) {
                        itemContext.putAll((Map<String, Object>) item);
                    } else {
                        itemContext.put("this", item);
                        itemContext.put("getIndex", idx++);
                    }
                    loopResult.append(render(block, itemContext));
                }
                matcher.appendReplacement(result, Matcher.quoteReplacement(loopResult.toString()));
            } else if (listObject instanceof String[]) { // Gestione di array String[]
                String[] array = (String[]) listObject;
                StringBuilder loopResult = new StringBuilder();
                int idx =0;
                for (String item : array) {
                    Map<String, Object> itemContext = new HashMap<>(context);
                    itemContext.put("this", item);
                    itemContext.put("getIndex", idx++);
                    loopResult.append(render(block, itemContext));
                }
                matcher.appendReplacement(result, Matcher.quoteReplacement(loopResult.toString()));
            } else {
                matcher.appendReplacement(result, matcher.group(0)); // Mantieni il blocco originale
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }


}

