package com.homedepot.interns;

import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homedepot.interns.MyCachingStateRepo.CacheEntry;

public class MyOptionsRegistry {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	/*
	make sure to copy class over to TWS
	CONTENTS OF JSON
	"FEATURE_THREE":{"state":{"feature":"FEATURE_THREE",
	"enabled":true,"strategyId":null,"users":[],"parameterMap":{},"parameterNames":[]},
	"timestamp":1468848145769,"last_UPD_SYSUSR_ID":null,"last_UPD_TS":null,"app_ENV":null,
	"strategy_ID":null,"strategy_PARAMS":null,"feature_ID":"19737eba-4839-11e6-a160-080027b4ff49"},
	*/
	
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
		
	}
}
