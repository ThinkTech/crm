package app;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import groovy.sql.Sql;
import groovy.text.markup.MarkupTemplateEngine;

@SuppressWarnings("serial")
public class ActionSupport extends org.metamorphosis.core.ActionSupport {
	
	public Sql getConnection()  {
		 HttpServletRequest request = getRequest();
		 Sql connection = (Sql) request.getAttribute("connection");
		 if(connection == null) {
			 connection = new Sql(getDataSource());
			 request.setAttribute("connection",connection);
		 }
		 return connection;	
   }
	
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
