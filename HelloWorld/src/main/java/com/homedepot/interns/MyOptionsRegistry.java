package com.homedepot.interns;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homedepot.interns.MyCachingStateRepo.CacheEntry;

public class MyOptionsRegistry {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	private Map<String,CacheEntry> cacheMap;
	
	private MyOptionsRegistry(){
		
	}
	
	private static class MyOptionsRegistryHolder{
		private static final MyOptionsRegistry INSTANCE = new MyOptionsRegistry();
	}
	
	public static MyOptionsRegistry getInstance(){
		return MyOptionsRegistryHolder.INSTANCE;
	}
	
	protected void populate() throws Exception{
		populateKillSwitchData();
	}
	
	private void populateKillSwitchData() throws Exception{
		//http://localhost:8080/interns/retCache
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
			//String json = "{\"FEATURE_THREE\":{\"state\":null,\"timestamp\":1468854064451,\"last_UPD_SYSUSR_ID\":null,\"last_UPD_TS\":null,\"app_ENV\":null,\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":null},\"HFMobileTypeAhead\":{\"state\":{\"feature\":\"HFMobileTypeAhead\",\"enabled\":false,\"strategyId\":null,\"users\":[],\"parameterMap\":{},\"parameterNames\":[]},\"timestamp\":1468854064592,\"last_UPD_SYSUSR_ID\":\"TAUSER02\",\"last_UPD_TS\":\"2016-06-27 13:03:14.0\",\"app_ENV\":\"live\",\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":\"5522016d-483a-11e6-9da2-08002727ab87\"},\"MyAccountSOCCFeature\":{\"state\":{\"feature\":\"MyAccountSOCCFeature\",\"enabled\":true,\"strategyId\":null,\"users\":[],\"parameterMap\":{},\"parameterNames\":[]},\"timestamp\":1468854064554,\"last_UPD_SYSUSR_ID\":\"TAUSER02\",\"last_UPD_TS\":\"2016-06-29 16:19:01.0\",\"app_ENV\":\"live\",\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":\"5521fffa-483a-11e6-9da2-08002727ab87\"},\"FEATURE_ONE\":{\"state\":null,\"timestamp\":1468854064374,\"last_UPD_SYSUSR_ID\":null,\"last_UPD_TS\":null,\"app_ENV\":null,\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":null},\"PipSearchNav2BannerFeatureSwitch\":{\"state\":{\"feature\":\"PipSearchNav2BannerFeatureSwitch\",\"enabled\":true,\"strategyId\":null,\"users\":[],\"parameterMap\":{},\"parameterNames\":[]},\"timestamp\":1468854064509,\"last_UPD_SYSUSR_ID\":\"TAUSER02\",\"last_UPD_TS\":\"2016-05-25 13:43:30.0\",\"app_ENV\":\"live\",\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":\"5521fafe-483a-11e6-9da2-08002727ab87\"},\"FEATURE_TWO\":{\"state\":null,\"timestamp\":1468854064420,\"last_UPD_SYSUSR_ID\":null,\"last_UPD_TS\":null,\"app_ENV\":null,\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":null}}";
			Map<String, MyCacheEntry> temp = null;  //cache.getMap();
			temp = mapper.readValue(str, new TypeReference<Map<String, MyCacheEntry>>(){});
			//System.out.println(temp);
			for(Map.Entry<String, MyCacheEntry> entry : temp.entrySet()){
	    		MyCacheEntry obj = entry.getValue();
	    		logger.info(entry.getKey());
	    		logger.info("STATE: " + obj.getState().isEnabled());
	    		logger.info("LAST_UPD_SYSUSR_TD: " + obj.getLAST_UPD_SYSUSR_ID());
	    		logger.info("LAST_UPD_TS: " + obj.getLAST_UPD_TS());
	    		logger.info("APP_ENV: " + obj.getAPP_ENV());
	    		logger.info("STRATEGY_ID: " + obj.getSTRATEGY_ID());
	    		logger.info("STRATEGY_PARAMS: " + obj.getSTRATEGY_PARAMS());
	    		
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
