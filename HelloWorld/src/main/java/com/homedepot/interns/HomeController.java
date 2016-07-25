package com.homedepot.interns;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.homedepot.interns.MyCachingStateRepo.CacheEntry;

//import com.homedepot.interns.MyCachingStateRepo.CacheEntry;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private static final Map<String, CacheEntry> String = null;

	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test(Model model) throws Exception{
		MyOptionsRegistry me = MyOptionsRegistry.getInstance();
		me.populate();
		String greetings = "Populating the OptionsRegistry HashMap";
		model.addAttribute("message", greetings);
		return "test";
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/retCache", method=RequestMethod.GET, produces = "application/json")
	public @ResponseBody Map<String, CacheEntry> retCache() throws Exception{
		
		MyOptionsHelper op = new MyOptionsHelper();
		op.listFeatures();
		
		MyCachingStateRepo cache = MyCachingStateRepo.getInstance();
		
		/*String json = "{\"FEATURE_THREE\":{\"state\":null,\"timestamp\":1468854064451,\"last_UPD_SYSUSR_ID\":null,\"last_UPD_TS\":null,\"app_ENV\":null,\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":null},\"HFMobileTypeAhead\":{\"state\":{\"feature\":\"HFMobileTypeAhead\",\"enabled\":false,\"strategyId\":null,\"users\":[],\"parameterMap\":{},\"parameterNames\":[]},\"timestamp\":1468854064592,\"last_UPD_SYSUSR_ID\":\"TAUSER02\",\"last_UPD_TS\":\"2016-06-27 13:03:14.0\",\"app_ENV\":\"live\",\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":\"5522016d-483a-11e6-9da2-08002727ab87\"},\"MyAccountSOCCFeature\":{\"state\":{\"feature\":\"MyAccountSOCCFeature\",\"enabled\":true,\"strategyId\":null,\"users\":[],\"parameterMap\":{},\"parameterNames\":[]},\"timestamp\":1468854064554,\"last_UPD_SYSUSR_ID\":\"TAUSER02\",\"last_UPD_TS\":\"2016-06-29 16:19:01.0\",\"app_ENV\":\"live\",\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":\"5521fffa-483a-11e6-9da2-08002727ab87\"},\"FEATURE_ONE\":{\"state\":null,\"timestamp\":1468854064374,\"last_UPD_SYSUSR_ID\":null,\"last_UPD_TS\":null,\"app_ENV\":null,\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":null},\"PipSearchNav2BannerFeatureSwitch\":{\"state\":{\"feature\":\"PipSearchNav2BannerFeatureSwitch\",\"enabled\":true,\"strategyId\":null,\"users\":[],\"parameterMap\":{},\"parameterNames\":[]},\"timestamp\":1468854064509,\"last_UPD_SYSUSR_ID\":\"TAUSER02\",\"last_UPD_TS\":\"2016-05-25 13:43:30.0\",\"app_ENV\":\"live\",\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":\"5521fafe-483a-11e6-9da2-08002727ab87\"},\"FEATURE_TWO\":{\"state\":null,\"timestamp\":1468854064420,\"last_UPD_SYSUSR_ID\":null,\"last_UPD_TS\":null,\"app_ENV\":null,\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":null}}";
		HashMap<String,MyCacheEntry> map = new Gson().fromJson(json, new TypeToken<HashMap<String, MyCacheEntry>>(){}.getType());
		System.out.println(map);*/

		return (Map<String, CacheEntry>) cache.getMap();
	}
	
}
