package com.jui;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Map;

import com.jui.utils.FS;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

public class TemplateHelper {
	
	boolean classLoading;
	String folder;
	
	public TemplateHelper(boolean templateClassLoading, String templateFolder) throws IOException {
		
		this.classLoading = templateClassLoading;
		this.folder = templateFolder;
        
    }
	
	public String renderTemplate(String templateName, Map<String, Object> variables) throws Exception {
		
		String fileName;
		if ( folder != null )
			fileName = this.folder + "/" + templateName.replace(".", "/") + ".ftl";
		else 
			fileName = templateName.replace(".", "/") + ".ftl";
		
		Reader reader = FS.getFile(fileName, Map.of("classLoading", String.valueOf(classLoading)));
		
		Template tmpl = Mustache.compiler().compile(reader);
		
		try (StringWriter out = new StringWriter()) {
			
			tmpl.execute(variables, out);
			out.flush();
			
			return out.getBuffer().toString();
			

		} 
	}
	
}
