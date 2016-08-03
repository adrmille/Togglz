package com.homedepot.interns;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import com.homedepot.interns.MyCachingStateRepo;
import com.homedepot.interns.MyJDBCStateRepo; 
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;

/**
 * 
 * Establishes database connection
 * @author RXS6631, DXR3590
 *
 */
@Component
public class MyTogglzConfiguration implements TogglzConfig {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);


	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public Class<? extends Feature> getFeatureClass() {
        return MyFeatures.class;
    }

	@Override
    public StateRepository getStateRepository(){
    	
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			logger.info("MyTogglzConfig getStateRepository() Driver not found");
		}
		

    	DataSource source = new DriverManagerDataSource("jdbc:mysql://localhost:3306/togglz", "remote", "tester123");

    	MyJDBCStateRepo repo = null;


    	try {
    		repo = new MyJDBCStateRepo(source);
    	}
    	catch(Exception e) {
    		logger.info("MyTogglzConfig getStateRepository() Cannot instantiate JDBC state repo");
    	}
    	
    	MyCachingStateRepo myCache = MyCachingStateRepo.getInstance(repo, 100000);
    	return myCache;

    }

    public UserProvider getUserProvider() {
    	 return new UserProvider() {
             @Override
             public FeatureUser getCurrentUser() {
                 return new SimpleFeatureUser("admin", true);
             }
         };
    }
}
