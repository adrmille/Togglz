package com.homedepot.interns;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;

/**
 * 
 * Simple implementation of {@link StateRepository} which adds caching capabilities to an existing repository. You should
 * consider using this class if lookups in your {@link StateRepository} are expensive (like database queries).
 * 
 * @author Christian Kaltepoth
 * 
 */
public class MyCachingStateRepo implements StateRepository {

    private final StateRepository delegate;

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<String, CacheEntry>();

    private long ttl;
    private static MyCachingStateRepo instance = null;
    public static MyCachingStateRepo getInstance(StateRepository delegate, long ttl){
    	if(instance == null){
    		instance = new MyCachingStateRepo(delegate, ttl);
    	}
    	return instance; 
    	
    }
    
    public static MyCachingStateRepo getInstance(){
    	return instance;
    }
    /*
     * public class ClassicSingleton {

   private static ClassicSingleton instance = null;
   private ClassicSingleton() {
      // Exists only to defeat instantiation.
   }
   public static ClassicSingleton getInstance() {
      if(instance == null) {
         instance = new ClassicSingleton();
      }
      return instance;
   }
}
     * 
     * 
     */

    /*
	private OptionsRegistry(){}
	
	/**
	 * The Lazy Initialization-on-demand holder, until we need an instance, the OptionsRegistryHolder class will not be initialized 
	 * until required and it can still use other static members of OptionsRegistry class.
	 * 
	 * @author AXG8965
	 *
	 
	private static class OptionsRegistryHolder {
		private static final OptionsRegistry INSTANCE = new OptionsRegistry();
	}
	
	/** 
	 * It returns singleton instance of the OptionsRegistry class
	 * @return
	 
	public static OptionsRegistry getInstance() {
		return OptionsRegistryHolder.INSTANCE;
	}
	*/
    
    /**
     * Creates a caching facade for the supplied {@link StateRepository}. The cached state of a feature will only expire if
     * {@link #setFeatureState(FeatureState)} is invoked. You should therefore never use this constructor if the feature state
     * is modified directly (for example by modifying the database table or the properties file).
     * 
     * @param delegate The repository to delegate invocations to
     */
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    public void testFunction(){
    	logger.info("testing testing 123 abc lafdkmfdsk");
    }
    private MyCachingStateRepo(StateRepository delegate) {
        this(delegate, 0);
    }

    /**
     * Creates a caching facade for the supplied {@link StateRepository}. The cached state of a feature will expire after the
     * supplied TTL or if {@link #setFeatureState(FeatureState)} is invoked.
     * 
     * @param delegate The repository to delegate invocations to
     * @param ttl The time in milliseconds after which a cache entry will expire
     * @throws IllegalArgumentException if the specified ttl is negative
     */
    private MyCachingStateRepo(StateRepository delegate, long ttl) {
        if (ttl < 0) {
            throw new IllegalArgumentException("Negative TTL value: " + ttl);
        }

        this.delegate = delegate;
        this.ttl = ttl;
    }

    /**
     * Creates a caching facade for the supplied {@link StateRepository}. The cached state of a feature will expire after the
     * supplied TTL rounded down to milliseconds or if {@link #setFeatureState(FeatureState)} is invoked.
     *
     * @param delegate The repository to delegate invocations to
     * @param ttl The time in a given {@code ttlTimeUnit} after which a cache entry will expire
     * @param ttlTimeUnit The unit that {@code ttl} is expressed in
     */
    private MyCachingStateRepo(StateRepository delegate, long ttl, TimeUnit ttlTimeUnit) {
        this(delegate, ttlTimeUnit.toMillis(ttl));
    }
   
    @Override
    public FeatureState getFeatureState(Feature feature) {

        // first try to find it from the cache
        CacheEntry entry = cache.get(feature.name());
        if (entry != null && !isExpired(entry)) {
            return entry.getState() != null ? entry.getState().copy() : null;
        }

        // no cache hit
        FeatureState featureState = delegate.getFeatureState(feature);

        // cache the result (may be null)
        
        cache.put(feature.name(), new CacheEntry(featureState != null ? featureState.copy() : null));

        logger.info("UPDATING THE CACHE");
        // return the result
        return featureState;

    }

    @Override
    public void setFeatureState(FeatureState featureState) {
        delegate.setFeatureState(featureState);
        cache.remove(featureState.getFeature().name());
    }

    /**
     * Clears the contents of the cache
     */
    public void clear() {
        cache.clear();
    }
    
    /**
     * Checks whether this supplied {@link CacheEntry} should be ignored.
     */
    private boolean isExpired(CacheEntry entry) {
        if (ttl == 0) {
            return false;
        }

        return entry.getTimestamp() + ttl < System.currentTimeMillis();
    }
    
    public void showCache(){
    	for(Map.Entry<String, CacheEntry> entry : cache.entrySet()){
    		CacheEntry obj = entry.getValue();
    		logger.info("STATE: " + obj.getState());
    		/*logger.info("LAST_UPD_SYSUSR_TD: " + obj.getLAST_UPD_SYSUSR_ID());
    		logger.info("LAST_UPD_TS: " + obj.getLAST_UPD_TS());
    		logger.info("APP_ENV: " + obj.getAPP_ENV());
    		logger.info("STRATEGY_ID: " + obj.getSTRATEGY_ID());
    		logger.info("STRATEGY_PARAMS: " + obj.getSTRATEGY_PARAMS());*/
    		
    	}
    }

    /**
     * This class represents a cached repository lookup
     */
    private static class CacheEntry {

        private final FeatureState state;
        private final String LAST_UPD_SYSUSR_ID;
        private final String LAST_UPD_TS;
        private final String APP_ENV;
        private final String STRATEGY_ID;
        private final String STRATEGY_PARAMS;
        private final String FEATURE_ID;
        private final long timestamp;

        public CacheEntry(FeatureState state) {
            this.state = state;
            this.timestamp = System.currentTimeMillis();
            this.STRATEGY_PARAMS = "";
            this.STRATEGY_ID = "";
            this.APP_ENV = "";
            this.LAST_UPD_TS = "";
            this.LAST_UPD_SYSUSR_ID = "";
            this.FEATURE_ID = "";
        }

        public FeatureState getState() {
            return state;
        }

        public long getTimestamp() {
            return timestamp;
        }

		public String getLAST_UPD_SYSUSR_ID() {
			return LAST_UPD_SYSUSR_ID;
		}

		public String getLAST_UPD_TS() {
			return LAST_UPD_TS;
		}

		public String getAPP_ENV() {
			return APP_ENV;
		}

		public String getSTRATEGY_ID() {
			return STRATEGY_ID;
		}

		public String getSTRATEGY_PARAMS() {
			return STRATEGY_PARAMS;
		}

		public String getFEATURE_ID() {
			return FEATURE_ID;
		}

    }

}
