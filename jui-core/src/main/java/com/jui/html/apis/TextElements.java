package com.jui.html.apis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;

import com.jui.html.WebElementContext;
import com.jui.html.elements.Divider;
import com.jui.html.elements.Text;
import com.jui.processors.HtmlProcessor;
import com.jui.processors.MarkdownProcessor;
import com.jui.utils.FS;

import lombok.extern.java.Log;

@Log
public class TextElements extends BaseElements {
	
	HtmlProcessor htmlProcessor;
	
	public TextElements(WebElementContext context) {
		
		super(context);
		try {
			Path filePath = FS.getFilePath("emoji.properties", Map.of("classLoading","True"));
			System.out.println("FILE:" + filePath.toString());
			htmlProcessor = new HtmlProcessor(filePath.toString());
		} catch (IOException | URISyntaxException e) {
			log.severe("emoji properties file not loaded. Err:" + e.getLocalizedMessage());
		}
		
	}
	
	public Text title(String text) {
		
		String html = "<h1>%s</h1>".formatted(htmlProcessor.convertTextToHtml(text));
		return (Text) context.add(new Text(html, false, true));
	}
	
	public Text header(String text) {
		
		String html = "<h2>%s</h2>".formatted(htmlProcessor.convertTextToHtml(text));
		Text web = new Text(html, false, true);
		context.add(web);
		return web;
	}
	
	public Text header(String text, boolean divider) {
		Text web = header(text);
		context.add(this.context.add(new Divider()));
		return web;
	}
	
	public Text header(String text, String dividerColor) {
		Text web = header(text);
		context.add(new Divider(dividerColor));
		return web;
	}

	public Text subheader(String text) {
		String html = "<h3>%s</h3>".formatted(htmlProcessor.convertTextToHtml(text));
		Text web = new Text(html, false, true);
		context.add(web);
		return web;
	}
	
	public Text subheader(String text, boolean divider) {
		Text web = subheader(text);
		context.add(this.context.add(new Divider()));
		return web;
	}
	
	public Text subheader(String text, String dividerColor) {
		Text web = subheader(text);
		context.add(new Divider(dividerColor));
		return web;
	}
	
	
	public Text html(String html) {
		
		return (Text) context.add(new Text(html, false, true));
	}

	public Text text(String text) {
		
		return (Text) context.add(new Text(htmlProcessor.convertTextToHtml(text), false, true));
	}
	
	public Text caption(String text) {
		String html = "<p>%s</p>".formatted(htmlProcessor.convertTextToHtml(text));
		Text web = new Text(html, false, true);
		context.add(web);
		return web;
	}

	public Text markdown(String... args) {
		return (Text) this.context.add(new Text(getMarkdown(args), false, true));
	}

	public void code(String text, String language, boolean line_numbers) {
		
		String code = """
				<pre><code>
				%s
				</code></pre>
		""".formatted(text);
		
		this.context.add(new Text(code, false, true));
	}
	
	public Divider divider() {
		
		Divider divider = new Divider();
		context.add(divider);
		return divider;
		
	}
	
	protected String getMarkdown(String... args) {

		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			sb.append(MarkdownProcessor.render(arg));
		}
		return sb.toString();
	}

}
