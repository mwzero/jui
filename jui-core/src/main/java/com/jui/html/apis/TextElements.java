package com.jui.html.apis;

import com.jui.html.WebElementContext;
import com.jui.html.elements.Divider;
import com.jui.html.elements.Text;
import com.jui.processors.MarkdownProcessor;

public class TextElements extends BaseElements {
	
	public TextElements(WebElementContext context) {
		
		super(context);
		
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

	public void code(String text, String language, boolean line_numbers) {
		
		String code = """
				<pre><code>
				%s
				</code></pre>
		""".formatted(text);
		
		this.context.add(new Text(code, false, true));
	}
	
	protected String getMarkdown(String... args) {

		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			sb.append(MarkdownProcessor.builder().build().render(arg));
		}
		return sb.toString();
	}

}
