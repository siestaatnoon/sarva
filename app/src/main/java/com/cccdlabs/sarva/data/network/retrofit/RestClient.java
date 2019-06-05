package com.cccdlabs.sarva.data.network.retrofit;

import com.cccdlabs.sarva.data.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * API client to connect to server resources utilizing a {@link Retrofit} client.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class RestClient {

    /**
     * Default seconds to timeout from server.
     */
    private static final int TIMEOUT = 30; // seconds

    /**
     * API base URL.
     */
    private static String sBaseUrl;

    /**
     * {@link Retrofit} client.
     */
    private static Retrofit sRetrofit;

    /**
     * Instance of this class used for singleton.
     */
    private static RestClient sInstance;

    /**
     * Constructor.
     *
     * @param baseUrl The API base url
     */
    private RestClient(String baseUrl) {
        if (baseUrl == null) {
            sBaseUrl = Constants.API_URL;
        } else {
            sBaseUrl = baseUrl;
        }

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(sBaseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client);

        sRetrofit = builder.build();
    }

    /**
     * Returns the API base URL.
     *
     * @return The base URL
     */
    public static String getBaseUrl() {
        return sBaseUrl;
    }

    /**
     * Returns a singleton instance of this class with a given API url and option to refresh
     * instance of singleton.
     *
     * @param baseUrl           The base URL for API calls
     * @param refreshInstance   True to refresh singleton instance
     * @return                  The RestClient instance
     */
    public static RestClient getInstance(String baseUrl, boolean refreshInstance) {
        if (sInstance == null || refreshInstance) {
            sInstance = new RestClient(baseUrl);
        }

        return sInstance;
    }

    /**
     * Returns a singleton instance of this class with a given API url.
     *
     * @param baseUrl   The base URL for API calls
     * @return          The RestClient instance
     */
    public static RestClient getInstance(String baseUrl) {
        return getInstance(baseUrl, false);
    }

    /**
     * Returns a singleton instance of this class using the default API url.
     *
     * @return The RestClient instance
     */
    public static RestClient getInstance() {
        return getInstance(null, false);
    }

    /**
     * Returns the {@link Retrofit} service implementation for API calls.
     *
     * @param service   The Class of the  service interface mapping API endpoints to handler methods
     * @param <T>       Class type of the service class
     * @return          Implementation of the service class
     */
    public <T> T getService(Class<T> service) {
        return sRetrofit.create(service);
    }
}
