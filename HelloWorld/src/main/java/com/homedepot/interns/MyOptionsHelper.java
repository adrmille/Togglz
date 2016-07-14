package com.homedepot.interns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//given feature name, can get value
public class MyOptionsHelper {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	//helper function to get boolean
	public boolean getTogglzState(String name){
		
		return MyFeatures.valueOf(name).isActive();
	}
	
	//function returns all feature switches with the on/off value
	public void listFeatures(){
		
		logger.info("Printing features:");
		for(MyFeatures value: MyFeatures.values()){
			value.isActive();
			//logger.info("Name "+ value.name() + " Value: " + value.isActive());
		}
	}
}
