package app;

import java.util.Map;
import groovy.text.markup.MarkupTemplateEngine;

@SuppressWarnings("serial")
public class ActionSupport extends org.metamorphosis.core.ActionSupport {
	
	@SuppressWarnings("rawtypes")
	public String parseTemplate(String template, Map map) throws Exception {
		return new MarkupTemplateEngine().createTemplate(readFile("templates/"+template+".groovy")).make(map).toString();
	}
	
	public String getAppURL() {
		return "https://app.thinktech.sn";
	}
	
	public String getCrmURL() {
		return getBaseUrl();
	}

}
