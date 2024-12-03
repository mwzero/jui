package com.jui.processors;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.jui.utils.FS;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.samskivert.mustache.Template.Fragment;

public class TemplateHelper {
	
	boolean classLoading;
	String folder;
	
	public TemplateHelper(boolean templateClassLoading, String templateFolder) throws IOException {
		
		this.classLoading = templateClassLoading;
		this.folder = templateFolder;
        
    }
	
	public String renderTemplate(String templateName, Map<String, Object> variables) throws Exception {
		
	    String fileName;
	    if (folder != null)
	        fileName = this.folder + "/" + templateName.replace(".", "/") + ".ftl";
	    else
	        fileName = templateName.replace(".", "/") + ".ftl";

	    Reader reader = FS.getFile(fileName, Map.of("classLoading", String.valueOf(classLoading)));

	    Template tmpl = Mustache.compiler().compile(reader);

	    AtomicInteger idx = new AtomicInteger(0);
	    try (StringWriter out = new StringWriter()) {
	    	
	        // Create a new context map that includes both variables and the index lambda
	        Map<String, Object> context = new HashMap<>(variables);
	        /* add special variables 
	        context.put("name", "Willy");
	        */
	        
		     // Lambda to get the current index without incrementing
	        context.put("getIndex", new Mustache.Lambda() {
	            @Override
	            public void execute(Fragment frag, Writer out) throws IOException {
	                out.write(String.valueOf(idx.get()));
	                // If you have content inside the lambda tags, you can process it
	                frag.execute(out);
	            }
	        });
	
	        // Lambda to increment the index
	        context.put("incrementIndex", new Mustache.Lambda() {
	            @Override
	            public void execute(Fragment frag, Writer out) throws IOException {
	                idx.incrementAndGet();
	                // Process any content inside the lambda tags
	                frag.execute(out);
	            }
	        });

	        tmpl.execute(context, out);
	        out.flush();
	        
	        return out.toString();
	    }
	}
}
