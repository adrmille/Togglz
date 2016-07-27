package com.homedepot.interns;

import org.togglz.core.repository.FeatureState;

/**
 * Class for the objects that will be stored in the hashmap that stores the
 * feature switch values, each attribute is a column in the mysql database table
 * @author danielle
 *
 */

public class MyCacheEntry {
	
	private MyFeatureState state;
    private String LAST_UPD_SYSUSR_ID;
    private String LAST_UPD_TS;
    private String APP_ENV;
    private String STRATEGY_ID;
    private String STRATEGY_PARAMS;
    private String FEATURE_ID;
    private long timestamp;
    
    public MyCacheEntry(){
    	this.state = null;
        this.timestamp = System.currentTimeMillis();
        this.STRATEGY_PARAMS = "";
        this.STRATEGY_ID = "";
        this.APP_ENV = "";
        this.LAST_UPD_TS = "";
        this.LAST_UPD_SYSUSR_ID = "" ;
        this.FEATURE_ID = ""; 
    }
	public MyFeatureState getState() {
		return state;
	}
	public void setState(MyFeatureState state) {
		this.state = state;
	}
	public String getLAST_UPD_SYSUSR_ID() {
		return LAST_UPD_SYSUSR_ID;
	}
	public void setLAST_UPD_SYSUSR_ID(String lAST_UPD_SYSUSR_ID) {
		LAST_UPD_SYSUSR_ID = lAST_UPD_SYSUSR_ID;
	}
	public String getLAST_UPD_TS() {
		return LAST_UPD_TS;
	}
	public void setLAST_UPD_TS(String lAST_UPD_TS) {
		LAST_UPD_TS = lAST_UPD_TS;
	}
	public String getAPP_ENV() {
		return APP_ENV;
	}
	public void setAPP_ENV(String aPP_ENV) {
		APP_ENV = aPP_ENV;
	}
	public String getSTRATEGY_ID() {
		return STRATEGY_ID;
	}
	public void setSTRATEGY_ID(String sTRATEGY_ID) {
		STRATEGY_ID = sTRATEGY_ID;
	}
	public String getSTRATEGY_PARAMS() {
		return STRATEGY_PARAMS;
	}
	public void setSTRATEGY_PARAMS(String sTRATEGY_PARAMS) {
		STRATEGY_PARAMS = sTRATEGY_PARAMS;
	}
	public String getFEATURE_ID() {
		return FEATURE_ID;
	}
	public void setFEATURE_ID(String fEATURE_ID) {
		FEATURE_ID = fEATURE_ID;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
