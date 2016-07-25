package com.homedepot.interns;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.*;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * This class replaces the OptionsRegistry in TouchWebServices. It must be added to the Home Depot application that is attempting to 
 * use the Togglz application. It makes a connection to the database in order to populate the cache with the most updated values.
 * 
 * @author RXS6631, DXR3590
 *
 */
public class MyOptionsRegistry {
	
	private final Logger LOGGER = Logger.getLogger(MyOptionsRegistry.class.getName());
	static Map<String, MyCacheEntry> cache;
	private MyOptionsRegistry(){
		cache = null;
	}
	
	private static class MyOptionsRegistryHolder{
		private static final MyOptionsRegistry INSTANCE = new MyOptionsRegistry();
	}
	
	public static MyOptionsRegistry getInstance(){
		return MyOptionsRegistryHolder.INSTANCE;
	}
	/**
	 * Calls the function to populate the cache
	 * @throws Exception
	 */
	public void populate() throws Exception{
		populateKillSwitchData();
	}
	public static boolean isFeatureEnabled(String featureName){
		if(cache.get(featureName).getState().isEnabled() == true){
			return true;
		}
		else
			return false;
	}
	/**
	 * Hits the Togglz application endpoint and deserializes the JSON receieved in order to get the cache with the most updated feature switch values
	 * @throws Exception
	 */
	private void populateKillSwitchData() throws Exception{
		String s = "http://localhost:8080/interns/retCache";
		URL url = new URL(s);
		Scanner scan = new Scanner(url.openStream());
		String str = new String();
		while(scan.hasNext()){
			str += scan.nextLine();
		}
		scan.close();
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			cache = mapper.readValue(str, new TypeReference<Map<String, MyCacheEntry>>(){});
			for(Map.Entry<String, MyCacheEntry> entry : cache.entrySet()){
	    		MyCacheEntry obj = entry.getValue();
	    		LOGGER.info(entry.getKey());
	    		LOGGER.info("STATE: " + obj.getState().isEnabled());
	    		LOGGER.info("LAST_UPD_SYSUSR_TD: " + obj.getLAST_UPD_SYSUSR_ID());
	    		LOGGER.info("LAST_UPD_TS: " + obj.getLAST_UPD_TS());
	    		LOGGER.info("APP_ENV: " + obj.getAPP_ENV());
	    		LOGGER.info("STRATEGY_ID: " + obj.getSTRATEGY_ID());
	    		LOGGER.info("STRATEGY_PARAMS: " + obj.getSTRATEGY_PARAMS());
	    		
	    	}
			} catch (JsonGenerationException e){
				e.printStackTrace();
			} catch (JsonMappingException e){
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
		
	}
}
