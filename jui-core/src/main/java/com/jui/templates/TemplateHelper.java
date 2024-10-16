package com.jui.templates;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TemplateHelper {
	
	private Configuration cfg;
	
	public TemplateHelper(boolean templateClassLoading, String templateFolder) throws IOException {
		
		cfg = new Configuration(Configuration.VERSION_2_3_22);
		
		//support for iterable in FreeMarker
		DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_22);
		owb.setIterableSupport(true);
		cfg.setObjectWrapper(owb.build());
		cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        
        try {
        	if ( templateClassLoading ) cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), templateFolder);
        	else
        		cfg.setDirectoryForTemplateLoading(new File(templateFolder));
        	
		} catch (IOException e) {
			
			log.error("Error Reading template folder [{}]. Error [{}]", templateFolder, e.getLocalizedMessage());
			log.error("Error freeamrker template folder reading", e);
			
			throw e;
		}
        
    }
	
	public String renderTemplate(String templateName, Map<String, Object> variables) throws Exception {
		
		try (StringWriter out = new StringWriter()) {
			
			Template template = cfg.getTemplate(
					templateName
					.replace(".", "/") + ".ftl");
			template.process(variables, out);
			out.flush();
			
			return out.getBuffer().toString();
			

		} catch (TemplateException | IOException e) {
			
			log.error("Error processing template [{}]. Error [{}]", templateName, e.getLocalizedMessage());
			log.error("Error freeamrker template", e);
			
			throw e;
		}
	}
	
}
