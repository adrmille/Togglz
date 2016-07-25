
package com.homedepot.interns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class enables access to the Togglz feature boolean value and assists in Togglz console proper initial display
 * @author RXS6631, DXR3590
 *
 */

public class MyOptionsHelper {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	public boolean getTogglzState(String name){
		
		return MyFeatures.valueOf(name).isActive();
	}
	/**
	 * Returns all feature switches with the on/off value.
	 * isActive needs to be called on all the switches in order to initially display them in the Togglz console, otherwise it will
	 * only display them when the connection to the database is made for an update
	 */
	public void listFeatures(){
		
		//logger.info("Printing features:");
		for(MyFeatures value: MyFeatures.values()){
			value.isActive();
		}
	}
}
