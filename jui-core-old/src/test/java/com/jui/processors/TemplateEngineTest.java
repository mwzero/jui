package com.jui.processors;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.jui.template.TemplateEngine;
import com.jui.template.TemplateFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TemplateEngineTest {
    
    @Test
    public void if_then_else() throws IOException, URISyntaxException {
		
        TemplateEngine engine = TemplateFactory.buildTemplateEngine();

        String template = """
            <button id="{{clientId}}" 
                    onclick="{{?onServerSide}}{{clientClick}};sendEvent('{{clientId}}', 'click', {}){{/onServerSide}}{{^onServerSide}}{{clientClick}}{{/onServerSide}};return false;"
                    class=\"btn btn-{{type}} ms-1\">
                    {{label}}
            </button>
            """;

        Map<String, Object> context = new HashMap<>();
        context.put("clientId", "12345");
        context.put("onServerSide", true);
        context.put("clientClick", "alert('clicked')");
        context.put("type", "primary");
        context.put("label", "Click Me");

        String output = engine.compile(template).execute(context);
        assertEquals("""
            <button id="12345"
                onclick="alert('clicked');sendEvent('12345', 'click', {});return false;"
                class="btn btn-primary ms-1">
                Click Me
            </button>
            """.replaceAll("\\s+", ""), output.replaceAll("\\s+", ""));

        System.out.println(output);
    }
	
	@Test
	public void loop() throws IOException, URISyntaxException {
		
        TemplateEngine engine = new TemplateEngine();

        String template = """
        	<ul>
            {{#items}}
                <li>{{this}}+{{getIndex}}</li>
            {{/items}}
            </ul>
            """;

        Map<String, Object> context = new HashMap<>();
        List<String> items = Arrays.asList("Apple", "Banana", "Cherry");
        context.put("items", items);

        String output = engine.compile(template).execute(context);
        

        assertEquals("""
            <ul>
                <li>Apple+0</li>
                <li>Banana+1</li>
                <li>Cherry+2</li>
            </ul>
            """.replaceAll("\\s+", ""), output.replaceAll("\\s+", ""));

        System.out.println(output);

       
    }
	
	@Test
	public void templatesFromFile() throws Exception {
		
        TemplateEngine engine = new TemplateEngine(true, "templates");

        Map<String, Object> context = new HashMap<>();
        List<String> items = Arrays.asList("Apple", "Banana", "Cherry");
        context.put("items", items);

        String output = engine.compileFromFile("ul").execute(context);
        assertEquals("""
            <ul>
                <li>Apple+0</li>
                <li>Banana+1</li>
                <li>Cherry+2</li>
            </ul>
            """.replaceAll("\\s+", ""), output.replaceAll("\\s+", ""));
        System.out.println(output);
    }
	
	@Test
	 public void nestedObject() throws IOException, URISyntaxException {
        TemplateEngine engine = new TemplateEngine();

        String template = "<div>{{user.address.street}}</div>";

        Map<String, Object> context = new HashMap<>();
        User user = new User(new Address("123 Main St"));
        context.put("user", user);

        String output = engine.compile(template).execute(context);
        assertEquals("""
            <div>123 Main St</div>
            """.replaceAll("\\s+", ""), output.replaceAll("\\s+", ""));
        System.out.println(output);
    }

    static class User {
        private Address address;

        public User(Address address) {
            this.address = address;
        }

        public Address getAddress() {
            return address;
        }
    }

    static class Address {

        private String street;

        public Address(String street) {
            this.street = street;
        }

        public String getStreet() {
            return street;
        }
    }

}
