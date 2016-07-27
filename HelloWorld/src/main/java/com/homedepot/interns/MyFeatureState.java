/**
 * This class needs to be added to whichever application will be hitting Togglz' endpoints. 
 */
package com.homedepot.interns;

import java.util.HashMap;
import java.util.Map;

import org.togglz.core.Feature;

public class MyFeatureState {

	private String feature;
  
	private boolean enabled;
    private String strategyId;
    private Map<String, String> parameters = new HashMap<String, String>();
	
	public MyFeatureState(){
		this.enabled = true; 
		this.feature=null;
		this.strategyId=null;
		this.parameters = null;
	}

	  public String getFeature() {
			return feature;
		}

		public void setFeature(String feature) {
			this.feature = feature;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getStrategyId() {
			return strategyId;
		}

		public void setStrategyId(String strategyId) {
			this.strategyId = strategyId;
		}

		public Map<String, String> getParameters() {
			return parameters;
		}

		public void setParameters(Map<String, String> parameters) {
			this.parameters = parameters;
		}
}
