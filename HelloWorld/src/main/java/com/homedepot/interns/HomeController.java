package com.homedepot.interns;

import java.io.IOException;
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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homedepot.interns.MyCachingStateRepo.CacheEntry;
import com.homedepot.interns.MyCachingStateRepo.MyCacheEntry;

//import com.homedepot.interns.MyCachingStateRepo.CacheEntry;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private static final Map<String, CacheEntry> String = null;
	//
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/time", method = RequestMethod.GET)
	public ModelAndView timing(){
		logger.info("User has entered timing page");
		return new ModelAndView("time", "command", new User());
	}
	
	@RequestMapping(value = "/addTime", method = RequestMethod.POST)
	public String addTiming(@ModelAttribute("interns")User user, ModelMap model){
		//model.addAttribute("name", user.getName());
		MyOptionsHelper op = new MyOptionsHelper();
		op.listFeatures();
		
		MyCachingStateRepo cache = MyCachingStateRepo.getInstance();
		cache.testFunction();
		logger.info("Show contents of cache");
		cache.showCache();

		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		String hr = String.valueOf(hour);
		if(MyFeatures.FEATURE_TWO.isActive()){
			logger.info("The hour is " + hr);
			model.addAttribute("time", hr);
		}
		if(hour < 12){
			model.addAttribute("hour", "Morning");
		}
		else if(hour < 18){
			model.addAttribute("hour", "Afternoon");
		}
		else{
			model.addAttribute("hour", "Evening");
		}
		
		model.addAttribute("name", user.getName());
		
		if(MyFeatures.FEATURE_ONE.isActive()){
			model.addAttribute("secret", "You're able to see the secret message");
			logger.info("The user can see the secret message");
		}
		else{
			model.addAttribute("secret", "You are not able to see the secret message :(");
			logger.info("The user can't see the message");
		}
		
		MyFeatures.class.getEnumConstants();
	
		return "timeresult";
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test(Model model){
		logger.info("User has entered test page");
		String greetings = "Hello world again";
		model.addAttribute("message", greetings);
		return "test";
	}
	
	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public ModelAndView form(){
		logger.info("User has entered form page");
		return new ModelAndView("form", "command", new Greeting());
	}
	 
	@RequestMapping(value = "/addForm", method = RequestMethod.POST)
	public String addForm(@ModelAttribute("interns")Greeting greeting,
			ModelMap model){
		model.addAttribute("id", greeting.getId());
		model.addAttribute("message", greeting.getMessage());
		model.addAttribute("sender", greeting.getSender());
		model.addAttribute("recipient", greeting.getRecipient());
		logger.info("Result page has been submitted");
		return "result";
	}
	
	@RequestMapping(value = "/retCache", method=RequestMethod.GET, produces = "application/json")
	public @ResponseBody Map<String, CacheEntry> retCache(){
		/*MyOptionsHelper obj = new MyOptionsHelper();
		obj.listFeatures();
		Map rand = null;
		return rand;*/
//		Greeting greet = new Greeting();
//		greet.setId(1);
//		greet.setRecipient("me");
//		greet.setMessage("hello");
//		greet.setSender("you");
//		logger.info("RETURN GREETING");
//		
//		return greet;
		MyOptionsHelper op = new MyOptionsHelper();
		op.listFeatures();
		
		MyCachingStateRepo cache = MyCachingStateRepo.getInstance();
		try{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String json = "{\"FEATURE_THREE\":{\"state\":null,\"timestamp\":1468854064451,\"last_UPD_SYSUSR_ID\":null,\"last_UPD_TS\":null,\"app_ENV\":null,\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":null},\"HFMobileTypeAhead\":{\"state\":{\"feature\":\"HFMobileTypeAhead\",\"enabled\":false,\"strategyId\":null,\"users\":[],\"parameterMap\":{},\"parameterNames\":[]},\"timestamp\":1468854064592,\"last_UPD_SYSUSR_ID\":\"TAUSER02\",\"last_UPD_TS\":\"2016-06-27 13:03:14.0\",\"app_ENV\":\"live\",\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":\"5522016d-483a-11e6-9da2-08002727ab87\"},\"MyAccountSOCCFeature\":{\"state\":{\"feature\":\"MyAccountSOCCFeature\",\"enabled\":true,\"strategyId\":null,\"users\":[],\"parameterMap\":{},\"parameterNames\":[]},\"timestamp\":1468854064554,\"last_UPD_SYSUSR_ID\":\"TAUSER02\",\"last_UPD_TS\":\"2016-06-29 16:19:01.0\",\"app_ENV\":\"live\",\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":\"5521fffa-483a-11e6-9da2-08002727ab87\"},\"FEATURE_ONE\":{\"state\":null,\"timestamp\":1468854064374,\"last_UPD_SYSUSR_ID\":null,\"last_UPD_TS\":null,\"app_ENV\":null,\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":null},\"PipSearchNav2BannerFeatureSwitch\":{\"state\":{\"feature\":\"PipSearchNav2BannerFeatureSwitch\",\"enabled\":true,\"strategyId\":null,\"users\":[],\"parameterMap\":{},\"parameterNames\":[]},\"timestamp\":1468854064509,\"last_UPD_SYSUSR_ID\":\"TAUSER02\",\"last_UPD_TS\":\"2016-05-25 13:43:30.0\",\"app_ENV\":\"live\",\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":\"5521fafe-483a-11e6-9da2-08002727ab87\"},\"FEATURE_TWO\":{\"state\":null,\"timestamp\":1468854064420,\"last_UPD_SYSUSR_ID\":null,\"last_UPD_TS\":null,\"app_ENV\":null,\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":null}}";
		Map<String, MyCacheEntry> temp = null;  //cache.getMap();
		temp = mapper.readValue(json, new TypeReference<Map<String, MyCacheEntry>>(){});
		System.out.println(temp);
		} catch (JsonGenerationException e){
			e.printStackTrace();
		} catch (JsonMappingException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		/*String json = "{\"FEATURE_THREE\":{\"state\":null,\"timestamp\":1468854064451,\"last_UPD_SYSUSR_ID\":null,\"last_UPD_TS\":null,\"app_ENV\":null,\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":null},\"HFMobileTypeAhead\":{\"state\":{\"feature\":\"HFMobileTypeAhead\",\"enabled\":false,\"strategyId\":null,\"users\":[],\"parameterMap\":{},\"parameterNames\":[]},\"timestamp\":1468854064592,\"last_UPD_SYSUSR_ID\":\"TAUSER02\",\"last_UPD_TS\":\"2016-06-27 13:03:14.0\",\"app_ENV\":\"live\",\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":\"5522016d-483a-11e6-9da2-08002727ab87\"},\"MyAccountSOCCFeature\":{\"state\":{\"feature\":\"MyAccountSOCCFeature\",\"enabled\":true,\"strategyId\":null,\"users\":[],\"parameterMap\":{},\"parameterNames\":[]},\"timestamp\":1468854064554,\"last_UPD_SYSUSR_ID\":\"TAUSER02\",\"last_UPD_TS\":\"2016-06-29 16:19:01.0\",\"app_ENV\":\"live\",\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":\"5521fffa-483a-11e6-9da2-08002727ab87\"},\"FEATURE_ONE\":{\"state\":null,\"timestamp\":1468854064374,\"last_UPD_SYSUSR_ID\":null,\"last_UPD_TS\":null,\"app_ENV\":null,\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":null},\"PipSearchNav2BannerFeatureSwitch\":{\"state\":{\"feature\":\"PipSearchNav2BannerFeatureSwitch\",\"enabled\":true,\"strategyId\":null,\"users\":[],\"parameterMap\":{},\"parameterNames\":[]},\"timestamp\":1468854064509,\"last_UPD_SYSUSR_ID\":\"TAUSER02\",\"last_UPD_TS\":\"2016-05-25 13:43:30.0\",\"app_ENV\":\"live\",\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":\"5521fafe-483a-11e6-9da2-08002727ab87\"},\"FEATURE_TWO\":{\"state\":null,\"timestamp\":1468854064420,\"last_UPD_SYSUSR_ID\":null,\"last_UPD_TS\":null,\"app_ENV\":null,\"strategy_ID\":null,\"strategy_PARAMS\":null,\"feature_ID\":null}}";
		HashMap<String,MyCacheEntry> map = new Gson().fromJson(json, new TypeToken<HashMap<String, MyCacheEntry>>(){}.getType());
		System.out.println(map);*/
		return (Map<String, CacheEntry>) cache.getMap();
	}
	
}
