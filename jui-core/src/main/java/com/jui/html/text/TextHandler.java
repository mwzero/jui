package com.jui.html.text;

import com.jui.WebContext;

import com.jui.html.Divider;
import com.jui.utils.Markdown;

public class TextHandler {

	WebContext context;

	public TextHandler(WebContext context) {
		this.context = context;
	}

	public Text title(String text) {
		return (Text) context.add(new Text(getMarkdown(text), true, true));
	}

	public Text header(String text, boolean divider) {
		Text web = new Text(getMarkdown(text), false, true);
		context.add(web);
		context.add(this.context.add(new Divider()));
		return web;
	}
	
	public Text header(String text, String dividerColor) {
		
		Text web = (Text) context.add(new Text(text, false, true));
		context.add(new Divider(dividerColor));
		return web;
	}

	public Text subHeader(String text) {
		return (Text) context.add(new Text(getMarkdown(text), false, true));
	}

	public Text caption(String text) {
		return (Text) context.add(new Text(getMarkdown(text), false, true));
	}

	public Text markdown(String... args) {
		return (Text) this.context.add(new Text(getMarkdown(args), false, true));
	}

	public String code(String text, String language, boolean line_numbers) {
		
		return """
				<pre><code>
				%s
				</code></pre>
		""".formatted(text);
	}

	protected String getMarkdown(String... args) {

		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			sb.append(Markdown.builder().build().render(arg));
		}
		return sb.toString();
	}

}
