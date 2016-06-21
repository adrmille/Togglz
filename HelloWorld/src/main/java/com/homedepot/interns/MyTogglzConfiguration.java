package com.homedepot.interns;

import javax.activation.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;

@Component
public class MyTogglzConfiguration implements TogglzConfig {
	public Class<? extends Feature> getFeatureClass() {
        return MyFeatures.class;
    }

    public StateRepository getStateRepository() {
    	 try {
    		 Context initCtx = new InitialContext();
    		 Context envCtx = (Context) initCtx.lookup("java:comp/env");
    		 DataSource ds = (DataSource)
    		   envCtx.lookup("jdbc/switches");
 
    		//DataSource dataSource = (DataSource) new InitialContext().lookup("jdbc/switches");
    		
             //InitialContext context = new InitialContext();
             //DataSource dataSource = (DataSource) context.lookup("jboss/datasources/ExampleDS");
             return new JDBCStateRepository((javax.sql.DataSource) ds, "MYTABLE");

         } catch (NamingException e) {
             throw new IllegalArgumentException("Could not find datasource");
         }

    	//return new FileBasedStateRepository(new File("/tmp/features.properties"));
    }

    public UserProvider getUserProvider() {
    	 return new UserProvider() {
             @Override
             public FeatureUser getCurrentUser() {
                 return new SimpleFeatureUser("admin", true);
             }
         };
    	//String user = HttpServletRequest.getUserPrincipal().getName();
        //return new ServletUserProvider("admin");
    }
}
