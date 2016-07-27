package com.homedepot.interns;

import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.homedepot.interns.MyCachingStateRepo.CacheEntry;

import junit.framework.Assert;

public class HomeControllerTest {

	Map<String, CacheEntry> map = new ConcurrentHashMap<String, CacheEntry>();

	@Test
	public void testNull() {
		MyCachingStateRepo mycache = mock(MyCachingStateRepo.class);
		when(mycache.getMap()).thenReturn(null);
		Assert.assertNull(mycache.getMap());
	}

	@Test
	public void testNotNull() {
		MyCachingStateRepo mycache = mock(MyCachingStateRepo.class);
		when(mycache.getMap()).thenReturn(this.getMap());
		Assert.assertNotNull(mycache.getMap());

	}

	public Map<String, CacheEntry> getMap() {

		CacheEntry obj = new CacheEntry();

		String test = "feature1";
		map.put(test, obj);
		System.out.println("check");
		return map;
	}


}
