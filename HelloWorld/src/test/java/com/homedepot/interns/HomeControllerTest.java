package com.homedepot.interns;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.homedepot.interns.MyCachingStateRepo.CacheEntry; 

public class HomeControllerTest {
	Map<String, CacheEntry> test;
	@Mock
	MyCachingStateRepo mockedObj;
	@InjectMocks
	HomeController obj;
	
	@Test
	public void testRetCache(){
		//MyCachingStateRepo repo = mock(MyCachingStateRepo.class);
		
		Map<String, MyCacheEntry> map;
		
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
		MyCachingStateRepo repo;
		repo.set//setting the map
		//
		//Map<String, CacheEntry> actual = new ConcurrentHashMap<String, CacheEntry>();
		//when(map.get(anyString()).thenReturn("success");
		//when()
		when(mockedObj.getInstance()).thenReturn(repo);
	
		mockMvc.perform("/retCache"); //mockMvc search
	
	}
	@Test
	public void setup(){
		initMocks(this);
	}
//	@Test (expected = java.lang.Exception.class)
//	public void testException(){
//		
//	}
	@Test(expected = java.lang.NullPointerException.class)
	public void testNullInput(){
		MyCachingStateRepo repo = null;
		
		
	}
	//test failure, issue with getting with the cache
	//when(mockedObj.getInstance()).thenReturn(repo);
	//	doThrow(new Exception()).when(m)
}
