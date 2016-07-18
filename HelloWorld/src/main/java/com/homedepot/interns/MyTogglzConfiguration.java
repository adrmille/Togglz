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
//import org.togglz.core.repository.cache.CachingStateRepository;
import com.homedepot.interns.MyCachingStateRepo;
//import org.togglz.core.repository.jdbc.JDBCStateRepository;
import com.homedepot.interns.MyJDBCStateRepo; //OUR jdbc
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;

@Component
public class MyTogglzConfiguration implements TogglzConfig {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	//how to configure? have put pom xml, and beans 


	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public Class<? extends Feature> getFeatureClass() {
        return MyFeatures.class;
    }

	@Override
    public StateRepository getStateRepository(){
    	
//    	return new FileBasedStateRepository(new File("/tmp/features.properties"));

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		

    	DataSource source = new DriverManagerDataSource("jdbc:mysql://localhost:3306/togglz", "root", "Rcs12345");

    	MyJDBCStateRepo repo = null;

    	try {
    		repo = new MyJDBCStateRepo(source);
    	}
    	catch(Exception e) {
    		e.printStackTrace();
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
