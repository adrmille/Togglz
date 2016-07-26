package com.homedepot.interns;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
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


/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * @return
	 * will return the feature switch values cache map as a json whenever they hit this endpoint
	 * @throws Exception
	 */
	@RequestMapping(value = "/retCache", method=RequestMethod.GET, produces = "application/json")
	public @ResponseBody Map<String, CacheEntry> retCache() throws ConcurrentModificationException {
		
		MyOptionsHelper op = new MyOptionsHelper();
		//use togglz values to populate the map
		op.listFeatures();
		
		MyCachingStateRepo cache = MyCachingStateRepo.getInstance();

		return (Map<String, CacheEntry>) cache.getMap();
	}
	
}
