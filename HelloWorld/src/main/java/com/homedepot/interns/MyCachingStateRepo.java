package com.homedepot.interns;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;


//import org.apache.log4j;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpBackOffIOExceptionHandler;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.Sleeper;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.PubsubScopes;
import com.google.api.services.pubsub.model.PublishRequest;
import com.google.api.services.pubsub.model.PublishResponse;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mysql.jdbc.PreparedStatement;


/**
 * 
 * Simple implementation of {@link StateRepository} which adds caching capabilities to an existing repository. You should
 * consider using this class if lookups in your {@link StateRepository} are expensive (like database queries).
 * 
 * @author Christian Kaltepoth
 * 
 */

/**
 * Caching State Repository that is tailored to the needs of THD
 * The Cache holds MyCacheEntry Objects that contain all the information about the feature
 * switches that can be found in the mysql database
 * @author DXR3590, RXS6631
 *
 */
public class MyCachingStateRepo implements StateRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	private static final String MyCachingStateRepo = null;

	//the primary state repository, in this case the mysql database
	private final StateRepository delegate;
	private Pubsub pubsub;
	//the cache that holds all the feature switch values at a certain time,
	//which can be returned at the end point as a json
	private final Map<String, CacheEntry> cache = new ConcurrentHashMap<String, CacheEntry>();

	//cache time before refresh
	private long ttl;
	
	private static MyCachingStateRepo instance = null;

	//this is a singleton class, so public method to get the instance of the class
	public static MyCachingStateRepo getInstance(StateRepository delegate, long ttl) {
		if (instance == null) {
			instance = new MyCachingStateRepo(delegate, ttl);
		}
		return instance;

	}

	/**
	 * 
	 * @return the cache that holds all the feature switch information
	 */
	public Map<String, CacheEntry> getMap() {
		return cache;
	}

	/**
	 * 
	 * @return get the instance of the singletone MyCachingStateRepo cache
	 */
	public static MyCachingStateRepo getInstance() {
		return instance;
	}

	/**
	 * Creates a caching facade for the supplied {@link StateRepository}. The
	 * cached state of a feature will only expire if
	 * {@link #setFeatureState(FeatureState)} is invoked. You should therefore
	 * never use this constructor if the feature state is modified directly (for
	 * example by modifying the database table or the properties file).
	 * 
	 * @param delegate
	 *            The repository to delegate invocations to
	 */
	
	private MyCachingStateRepo(StateRepository delegate) {
		this(delegate, 0);
	}

	
	/**
	 * Creates a caching facade for the supplied {@link StateRepository}. The
	 * cached state of a feature will expire after the supplied TTL or if
	 * {@link #setFeatureState(FeatureState)} is invoked.
	 * 
	 * @param delegate
	 *            The repository to delegate invocations to
	 * @param ttl
	 *            The time in milliseconds after which a cache entry will expire
	 * @throws IllegalArgumentException
	 *             if the specified ttl is negative
	 */
	private MyCachingStateRepo(StateRepository delegate, long ttl) {
		if (ttl < 0) {
			throw new IllegalArgumentException("Negative TTL value: " + ttl);
		}
		try {
			pubsub = createPubsubClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.delegate = delegate;
		this.ttl = ttl;
	}

	/**
	 * Creates a caching facade for the supplied {@link StateRepository}. The
	 * cached state of a feature will expire after the supplied TTL rounded down
	 * to milliseconds or if {@link #setFeatureState(FeatureState)} is invoked.
	 *
	 * @param delegate
	 *            The repository to delegate invocations to
	 * @param ttl
	 *            The time in a given {@code ttlTimeUnit} after which a cache
	 *            entry will expire
	 * @param ttlTimeUnit
	 *            The unit that {@code ttl} is expressed in
	 */
	private MyCachingStateRepo(StateRepository delegate, long ttl, TimeUnit ttlTimeUnit) {
		this(delegate, ttlTimeUnit.toMillis(ttl));
		
	}

	
	/**
	 * when togglz feature switch is used, this function is called to check
	 * the state of the feature
	 * first checks if it is in the cache, otherwise it checks the
	 * original source, (the mysql db)
	 */
	
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
		Connection connection = null;
		PreparedStatement statement = null;
		
		/**
		 * the previous code only store the state of the feature in the cache
		 * since THD needs all the information this sql call is called any time 
		 * a feature is accessed from the db
		 */

		try {
			DataSource source = new DriverManagerDataSource("jdbc:mysql://localhost:3306/togglz", "remote", "tester123");
			connection = source.getConnection();
			String sql = "SELECT * FROM TOGGLZ WHERE FEATURE_NAME = '" + feature.name() + "' AND APP_ENV ='live';";
			statement = (PreparedStatement) connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();
			// String Feature_Name = null;
			String LAST_UPD_SYSUSR_ID = null;
			String LAST_UPD_TS = null;
			String APP_ENV = null;
			String STRATEGY_ID = null;
			String FEATURE_ID = null;
			String STRATEGY_PARAMS = null;
			if (resultSet.next()) {
				// Feature_Name = resultSet.getString("FEATURE_NAME");
				LAST_UPD_SYSUSR_ID = resultSet.getString("LAST_UPD_SYSUSR_ID");
				LAST_UPD_TS = resultSet.getString("LAST_UPD_TS");
				APP_ENV = resultSet.getString("APP_ENV");
				STRATEGY_ID = resultSet.getString("STRATEGY_ID");
				FEATURE_ID = resultSet.getString("FEATURE_ID");
				STRATEGY_PARAMS = resultSet.getString("STRATEGY_PARAMS");
			}
			cache.put(feature.name(), new CacheEntry(featureState != null ? featureState.copy() : null,
					LAST_UPD_SYSUSR_ID, LAST_UPD_TS, APP_ENV, STRATEGY_ID, FEATURE_ID, STRATEGY_PARAMS));
		} catch (SQLException ex) {

		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {

				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {

				}
			}

		}
		
		
		return featureState;

	}

/**
 * RetryHttpInitializerWrapper will automatically retry upon RPC
 * failures, preserving the auto-refresh behavior of the Google
 * Credentials.
 */
public class RetryHttpInitializerWrapper implements HttpRequestInitializer{
//	private final Logger LOG =
//	        Logger.getLogger(RetryHttpInitializerWrapper.class.getName());

	    // Intercepts the request for filling in the "Authorization"
	    // header field, as well as recovering from certain unsuccessful
	    // error codes wherein the Credential must refresh its token for a
	    // retry.
	    private final Credential wrappedCredential;

	    // A sleeper; you can replace it with a mock in your test.
	    private final Sleeper sleeper;

	    public RetryHttpInitializerWrapper(Credential wrappedCredential) {
	        this(wrappedCredential, Sleeper.DEFAULT);
	    }

	    // Use only for testing.
	    RetryHttpInitializerWrapper(
	            Credential wrappedCredential, Sleeper sleeper) {
	        this.wrappedCredential = Preconditions.checkNotNull(wrappedCredential);
	        this.sleeper = sleeper;
	    }

	    @Override
	    public void initialize(HttpRequest request) {
	        request.setReadTimeout(2 * 60000); // 2 minutes read timeout
	        final HttpUnsuccessfulResponseHandler backoffHandler =
	            new HttpBackOffUnsuccessfulResponseHandler(
	                new ExponentialBackOff())
	                    .setSleeper(sleeper);
	        request.setInterceptor(wrappedCredential);
	        request.setUnsuccessfulResponseHandler(
	                new HttpUnsuccessfulResponseHandler() {
	                    @Override
	                    public boolean handleResponse(
	                            HttpRequest request,
	                            HttpResponse response,
	                            boolean supportsRetry) throws IOException {
	                        if (wrappedCredential.handleResponse(
	                                request, response, supportsRetry)) {
	                            // If credential decides it can handle it,
	                            // the return code or message indicated
	                            // something specific to authentication,
	                            // and no backoff is desired.
	                            return true;
	                        } else if (backoffHandler.handleResponse(
	                                request, response, supportsRetry)) {
	                            // Otherwise, we defer to the judgement of
	                            // our internal backoff handler.
	                          //LOG.info("Retrying " + request.getUrl());
	                          return true;
	                        } else {
	                            return false;
	                        }
	                    }
	                });
	        request.setIOExceptionHandler(
	            new HttpBackOffIOExceptionHandler(new ExponentialBackOff())
	                .setSleeper(sleeper));
	    }
}

	    
//		// Default factory method.
//	    public static Pubsub createPubsubClient() throws IOException {
//	        return createPubsubClient(Utils.getDefaultTransport(),
//	                Utils.getDefaultJsonFactory());
//	    }
private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
public Pubsub createPubsubClient()
	    throws IOException, GeneralSecurityException {
	    HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
	    GoogleCredential credential = GoogleCredential.getApplicationDefault();
	    HttpRequestInitializer initializer =
	        new RetryHttpInitializerWrapper(credential);
	    return new Pubsub.Builder(transport, JSON_FACTORY, initializer).build();
	  }

	    // A factory method that allows you to use your own HttpTransport
	    // and JsonFactory.
//	    public static Pubsub createPubsubClient(HttpTransport httpTransport,
//	            JsonFactory jsonFactory) throws IOException {
//	        Preconditions.checkNotNull(httpTransport);
//	        Preconditions.checkNotNull(jsonFactory);
//	        GoogleCredential credential = GoogleCredential.getApplicationDefault(
//	                httpTransport, jsonFactory);
//	        // In some cases, you need to add the scope explicitly.
//	        if (credential.createScopedRequired()) {
//	            credential = credential.createScoped(PubsubScopes.all());
//	        }
//	        // Please use custom HttpRequestInitializer for automatic
//	        // retry upon failures.  We provide a simple reference
//	        // implementation in the "Retry Handling" section.
////	       MyCachingStateRepo repo = new MyCachingStateRepo(delegate);
//	        HttpRequestInitializer initializer =
//	                new HttpRequestInitializer(credential);
//	        return new Pubsub.Builder(httpTransport, jsonFactory, initializer)
//	               .build();
//	    }
	  
	
	/**
	 * whenever feature is toggled on or off, the feature state is set again
	 * and the old entry in the cache is removed
	 */
	@Override
	public void setFeatureState(FeatureState featureState) {
//		String message = "Hello Cloud Pub/Sub!";
//		PubsubMessage pubsubMessage = new PubsubMessage();
//		// You need to base64-encode your message with
//		// PubsubMessage#encodeData() method.
//		try {
//			pubsubMessage.encodeData(message.getBytes("UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		List<PubsubMessage> messages = ImmutableList.of(pubsubMessage);
//		PublishRequest publishRequest =
//		        new PublishRequest().setMessages(messages);
//		PublishResponse publishResponse = null;
//		try {
//			publishResponse = pubsub.projects().topics()
//			        .publish("projects/deductive-span-135023/topics/Togglz", publishRequest)
//			        .execute();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		List<String> messageIds = publishResponse.getMessageIds();
//		if (messageIds != null) {
//		    for (String messageId : messageIds) {
//		        System.out.println("messageId: " + messageId);
//		    }
//		}
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
	
	/**
	 * function that can show the contents of the cache
	 */
	public void showCache() {
		for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
			CacheEntry obj = entry.getValue();
			logger.info(entry.getKey());
			logger.info("STATE: " + obj.getState());
			logger.info("LAST_UPD_SYSUSR_TD: " + obj.getLAST_UPD_SYSUSR_ID());
			logger.info("LAST_UPD_TS: " + obj.getLAST_UPD_TS());
			logger.info("APP_ENV: " + obj.getAPP_ENV());
			logger.info("STRATEGY_ID: " + obj.getSTRATEGY_ID());
			logger.info("STRATEGY_PARAMS: " + obj.getSTRATEGY_PARAMS());

		}
	}

	/**
	 * This class represents a cached repository lookup
	 */
	
	static class CacheEntry {

		private final FeatureState state;
		private final String LAST_UPD_SYSUSR_ID;
		private final String LAST_UPD_TS;
		private final String APP_ENV;
		private final String STRATEGY_ID;
		private final String STRATEGY_PARAMS;
		private final String FEATURE_ID;
		private final long timestamp;

		// public Cache

		public CacheEntry(FeatureState state, String LAST_UPD_SYSUSR_ID, String LAST_UPD_TS, String APP_ENV,
				String STRATEGY_ID, String FEATURE_ID, String STRATEGY_PARAMS) {
			this.state = state;
			this.timestamp = System.currentTimeMillis();
			this.STRATEGY_PARAMS = STRATEGY_PARAMS;
			this.STRATEGY_ID = STRATEGY_ID;
			this.APP_ENV = APP_ENV;
			this.LAST_UPD_TS = LAST_UPD_TS;
			this.LAST_UPD_SYSUSR_ID = LAST_UPD_SYSUSR_ID;
			this.FEATURE_ID = FEATURE_ID;
		}

		public CacheEntry() {
			this.state = null;
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
