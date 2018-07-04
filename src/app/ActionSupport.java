package app;

import javax.sql.DataSource;
import groovy.sql.Sql;

@SuppressWarnings("serial")
public class ActionSupport extends org.metamorphosis.core.ActionSupport {
	
	public Object getConnection()  {
		 return new Sql((DataSource) getDataSource());	
    }

}
