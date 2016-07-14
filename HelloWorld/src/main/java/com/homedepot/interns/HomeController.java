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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

//import com.homedepot.interns.MyCachingStateRepo.CacheEntry;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
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
	@RequestMapping(value = "/retCache", method = RequestMethod.GET, produces={"application/xml","application/json","text/plain"})
	public Map retCache(){
		MyOptionsHelper obj = new MyOptionsHelper();
		obj.listFeatures();
		Map rand = null;
		return rand;
	}
	
}
