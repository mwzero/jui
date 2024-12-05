package com.jui.html.apis;

import com.jui.html.WebElementContext;
import com.jui.html.elements.Callout;
import com.jui.html.elements.Callout.CalloutStatus;
import com.jui.html.elements.ProgressBar;
import com.jui.html.elements.Spinner;
import com.jui.processors.HtmlProcessor;

public class StatusElements extends BaseElements {
	
	HtmlProcessor htmlProcessor = new HtmlProcessor();
	
	public StatusElements(WebElementContext context) {
		
		super(context);
		
	}
	
	//callout
	protected Callout addCallout(CalloutStatus status, String title, String text) {
		
		Callout callout = new Callout(status, title, htmlProcessor.convertTextToHtml(text));
		context.add(callout);

		return callout;
	}

	public Callout success(String title, String text) { return this.addCallout(CalloutStatus.SUCCESS, title, text);}
	public Callout info(String title, String text) { return this.addCallout(CalloutStatus.INFO, title, text);}
	public Callout warning(String title, String text) { return this.addCallout(CalloutStatus.WARNING, title, text);}
	public Callout error(String title, String text) { return this.addCallout(CalloutStatus.ERROR, title, text);}

	
	//other
	public ProgressBar progress(int value, String text) {
		
		ProgressBar progressBar = new ProgressBar(value, htmlProcessor.convertTextToHtml(text));
		context.add(progressBar);

		return progressBar;
	}
	
	
	public Spinner spinner(String text) {
		
		Spinner spinner = new Spinner(htmlProcessor.convertTextToHtml(text));
		context.add(spinner);

		return spinner;
	}
	
	
	
}
