package com.homedepot.interns;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum MyFeatures implements Feature {

	//will add the enums later 
	 @EnabledByDefault
	    FEATURE_ONE,
	 @EnabledByDefault 
	    FEATURE_TWO,
	 @EnabledByDefault 
	    FEATURE_THREE,
	    PipSearchNav2BannerFeatureSwitch,
	    MyAccountSOCCFeature,
	    HFMobileTypeAhead;
	
	
	 

	    public boolean isActive() {
	        return FeatureContext.getFeatureManager().isActive(this);
	    }
	
}
