package com.homedepot.interns;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;

import com.mysql.jdbc.PreparedStatement;

/**
 * 
 * Simple implementation of {@link StateRepository} which adds caching capabilities to an existing repository. You should
 * consider using this class if lookups in your {@link StateRepository} are expensive (like database queries).
 * 
 * @author Christian Kaltepoth
 * 
 */
public class MyCachingStateRepo implements StateRepository {
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final StateRepository delegate;

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<String, CacheEntry>();

    private long ttl;

    /**
     * Creates a caching facade for the supplied {@link StateRepository}. The cached state of a feature will only expire if
     * {@link #setFeatureState(FeatureState)} is invoked. You should therefore never use this constructor if the feature state
     * is modified directly (for example by modifying the database table or the properties file).
     * 
     * @param delegate The repository to delegate invocations to
     */
    public MyCachingStateRepo(StateRepository delegate) {
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
    public MyCachingStateRepo(StateRepository delegate, long ttl) {
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
    public MyCachingStateRepo(StateRepository delegate, long ttl, TimeUnit ttlTimeUnit) {
        this(delegate, ttlTimeUnit.toMillis(ttl));
    }

    @Override
    public FeatureState getFeatureState(Feature feature){

        // first try to find it from the cache
        CacheEntry entry = cache.get(feature.name());
        if (entry != null && !isExpired(entry)) {
            return entry.getState() != null ? entry.getState().copy() : null;
        }

        // no cache hit
        FeatureState featureState = delegate.getFeatureState(feature);

        // cache the result (may be null)
        Connection connection = null;
        PreparedStatement statement = null;

try{
    	DataSource source = new DriverManagerDataSource("jdbc:mysql://localhost:3306/togglz", "root", "Rcs12345");
    	connection = source.getConnection();
    	String sql = "SELECT * FROM TOGGLZ;";
    	statement = (PreparedStatement) connection.prepareStatement(sql);
    	ResultSet resultSet = statement.executeQuery();
    	//String Feature_Name = null;
    	String LAST_UPD_SYSUSR_ID = null;
    	String LAST_UPD_TS = null;
    	String APP_ENV = null;
    	String STRATEGY_ID = null;
        String FEATURE_ID = null;
        String STRATEGY_PARAMS = null;
    	if(resultSet.next()){
    	//Feature_Name = resultSet.getString("FEATURE_NAME");
    		LAST_UPD_SYSUSR_ID = resultSet.getString("LAST_UPD_SYSUSR_ID");
        	LAST_UPD_TS = resultSet.getString("LAST_UPD_TS");
        	APP_ENV = resultSet.getString("APP_ENV");
        	STRATEGY_ID = resultSet.getString("STRATEGY_ID");
            FEATURE_ID = resultSet.getString("FEATURE_ID");
            STRATEGY_PARAMS = resultSet.getString("STRATEGY_PARAMS");
    	}
    	cache.put(feature.name(), new CacheEntry(featureState != null ? featureState.copy() : null, 
        		LAST_UPD_SYSUSR_ID, LAST_UPD_TS, APP_ENV, STRATEGY_ID, FEATURE_ID, STRATEGY_PARAMS));
}
catch(SQLException ex){
	
}
finally{
	if(statement != null){
		try{
			statement.close();
		}catch(SQLException ex){
			
		}
	} if (connection != null){
		try{
			connection.close();
		}
		catch(SQLException ex){
			
		}
	}
	
}
//catch (SQLException ex){
//	
//}
/*
 * finally {
  if (stmt != null) {
    try { 
      stmt.close();
    } catch (SQLException ex) {
    }
  }
  if (con != null) {
    try { 
      con.close();
    } catch (SQLException ex) {
    }
  }
}

 */
    	//logger.info(Feature_Name);
    	//public CacheEntry(FeatureState state, String LAST_UPD_SYSUSR_ID, String LAST_UPD_TS, String APP_ENV, String STRATEGY_ID, 
        //String FEATURE_ID, String STRATEGY_PARAMS) 
        

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
    public void setCache() throws SQLException{
    	
    	DataSource source = new DriverManagerDataSource("jdbc:mysql://localhost:3306/togglz", "root", "Rcs12345");
    	Connection connection = source.getConnection();
    	String sql = "SELECT FEATURE_NAME FROM TOGGLZ WHERE FEATURE_NAME = 'BeehiveFullAuthFeature';";
    	PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql);
    	ResultSet resultSet = statement.executeQuery();
    	String Feature_Name = null;
    	if(resultSet.next()){
    	Feature_Name = resultSet.getString("FEATURE_NAME");
    	}
    	logger.info(Feature_Name);
    	
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

        public CacheEntry(FeatureState state, String LAST_UPD_SYSUSR_ID, String LAST_UPD_TS, String APP_ENV, String STRATEGY_ID, String FEATURE_ID, String STRATEGY_PARAMS) {
            this.state = state;
            this.timestamp = System.currentTimeMillis();
            this.STRATEGY_PARAMS = STRATEGY_PARAMS;
            this.STRATEGY_ID = STRATEGY_ID;
            this.APP_ENV = APP_ENV;
            this.LAST_UPD_TS = LAST_UPD_TS;
            this.LAST_UPD_SYSUSR_ID = LAST_UPD_SYSUSR_ID ;
            this.FEATURE_ID = FEATURE_ID;
        }

        public FeatureState getState() {
            return state;
        }

        public long getTimestamp() {
            return timestamp;
        }

    }

}
