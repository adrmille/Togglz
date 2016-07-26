package com.homedepot.interns;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;

import com.homedepot.interns.MyCachingStateRepo.CacheEntry;
import com.homedepot.interns.MyCacheEntry;

public class HomeControllerTest {
	//Map<String, CacheEntry> test;
	Map<String, MyCacheEntry> map;
	@Mock
	MyCachingStateRepo mockedObj;
	@InjectMocks
	HomeController obj;
	
	@Test
	public void testRetCache(){
		//MyCachingStateRepo repo = mock(MyCachingStateRepo.class);
		
		MyCachingStateRepo repo;
		//repo.set//setting the map
		
		//Map<String, CacheEntry> actual = new ConcurrentHashMap<String, CacheEntry>();
		//when(map.get(anyString()).thenReturn("success");
		//when()
		
		
		//in thenReturn call the method that creates your map
		
		//mockMvc.perform("/retCache"); //mockMvc search
		
		assertNotNull(when(((OngoingStubbing<Object>) mockedObj.getMap()).thenReturn(getMap())));
		
		//make your assert statements
	}
	public Map<String, MyCacheEntry> getMap(){
		MyFeatureState obj2 = new MyFeatureState();
		obj2.setEnabled(true);
		obj2.setFeature(null);
		obj2.setParameters(null);
		obj2.setStrategyId(null);
		MyCacheEntry obj = new MyCacheEntry();
		obj.setState(obj2);
		obj.setLAST_UPD_SYSUSR_ID("40061870");
		obj.setLAST_UPD_TS("04-2016");
		obj.setAPP_ENV("beta");
		obj.setSTRATEGY_ID(null);
		obj.setFEATURE_ID(null);
		obj.setSTRATEGY_PARAMS(null);
		
		String test = "feature1";
		map.put(test,obj);
		return map;
	}
//	@Test
//	public void setup(){
//		initMocks(this);
//	}
//	@Test (expected = java.lang.Exception.class)
//	public void testException(){
//		
//	}
	@Test(expected = java.lang.NullPointerException.class)
	public void testNullInput(){
		//MyCachingStateRepo repo = null;
		assertNull(map);
		
	}
	//test failure, issue with getting with the cache
	//when(mockedObj.getInstance()).thenReturn(repo);
	//	doThrow(new Exception()).when(m)
}
