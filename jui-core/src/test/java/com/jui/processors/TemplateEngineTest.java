package com.jui.processors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TemplateEngineTest {
	
	@Test
	@Disabled
    public void if_then_else() {
		
        TemplateEngine engine = new TemplateEngine();

        String template = """
        		<button id="{{clientId}}" onclick="{{?onServerSide}}{{clientClick}};sendEvent('{{clientId}}', 'click', {}){{/onServerSide}}{{^onServerSide}}{{clientClick}}{{/onServerSide}};return false;"
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

        String output = engine.render(template, context);
        System.out.println(output);
    }
	
	@Test
	@Disabled
    public void loop() {
		
        TemplateEngine engine = new TemplateEngine();

        String template = """
        	<ul>
        	{{#items}}<li>{{this}}+{{getIndex}}</li>
            {{/items}}</ul>
                """;

        Map<String, Object> context = new HashMap<>();
        List<String> items = Arrays.asList("Apple", "Banana", "Cherry");
        context.put("items", items);

        String output = engine.render(template, context);
        System.out.println(output);
    }
	
	@Test
	@Disabled
    public void templatesFromFile() throws Exception {
		
        TemplateEngine engine = new TemplateEngine(true, "templates");

        Map<String, Object> context = new HashMap<>();
        List<String> items = Arrays.asList("Apple", "Banana", "Cherry");
        context.put("items", items);

        String output = engine.renderFromFile("ul", context);
        System.out.println(output);
    }
	
	@Test
	 public void nestedObject() {
        TemplateEngine engine = new TemplateEngine();

        String template = "<div>{{user.address.street}}</div>";

        Map<String, Object> context = new HashMap<>();
        User user = new User(new Address("123 Main St"));
        context.put("user", user);

        String output = engine.render(template, context);
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
