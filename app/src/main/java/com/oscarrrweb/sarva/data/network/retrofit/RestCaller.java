package com.oscarrrweb.sarva.data.network.retrofit;

import androidx.annotation.NonNull;

import android.content.Context;
import android.util.ArrayMap;
import android.util.MalformedJsonException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oscarrrweb.sarva.data.Constants;
import com.oscarrrweb.sarva.data.entity.base.Entity;
import com.oscarrrweb.sarva.data.network.base.ApiCaller;
import com.oscarrrweb.sarva.data.network.base.ApiResponse;
import com.oscarrrweb.sarva.data.network.exception.NetworkConnectionException;
import com.oscarrrweb.sarva.data.network.utils.NetworkUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Function;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Implementation of the {@link ApiCaller} interface utilizing a
 * <a href="https://square.github.io/retrofit/" target="_top">Retrofit</a> client and
 * <a href="https://github.com/ReactiveX/RxJava" target="_top">RxJava</a> {@link Single}s
 * to handle a server response by {@link io.reactivex.Observer}s.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class RestCaller implements ApiCaller {

    /**
     * API client
     */
    @Inject RestClient mClient;

    /**
     * Android application context
     */
    @Inject Context mContext;

    /**
     * Constant for content-type header value in API request.
     */
    protected static final String CONTENT_TYPE_POST = "Content-Type: text/json; charset=" + Constants.CHAR_ENCODING;

    /**
     * API endpoint to SELECT a single database row.
     */
    protected static final String API_QUERY_ROW = "row";

    /**
     * API endpoint to SELECT multiple database rows from a query string.
     */
    protected static final String API_QUERY_ROWS = "query";

    /**
     * API endpoint to DELETE one or more database rows.
     */
    protected static final String API_QUERY_DELETE = "delete";

    /**
     * API endpoint to perform an INSERT of data.
     */
    protected static final String API_QUERY_INSERT = "insert";

    /**
     * API endpoint to perform an UPDATE of data.
     */
    protected static final String API_QUERY_UPDATE = "update";

    /**
     * Request headers for API call.
     */
    protected Map<String, String> mHeaders;

    /**
     * API key used for API call authentication.
     */
    protected String mApiKey;

    /**
     * Auth token used for API call authentication.
     */
    protected String mAuthToken;

    /**
     * Parameters to add to a query string in a GET API call.
     */
    protected Map<String, String> mQueryParams;

    /**
     * Retrofit interface implementation of API endpoints and corresponding method calls.
     */
    protected RestService mService;

    /**
     * Constructor
     *
     * @param authToken The auth token used for API call authentication
     * @param apiKey    The API key used for API call authentication
     */
    public RestCaller(String authToken, String apiKey) {
        //RestClient client = RestClient.getInstance();
        // mService = mClient.getService(RestService.class);
        mApiKey = apiKey;
        mAuthToken = authToken;
        mHeaders = new HashMap<>();
        mHeaders.put("Content-Type", "application/json; charset=" + Constants.CHAR_ENCODING);
    }

    /**
     * Initializes the Retrofit API service class.
     *
     * @throws IllegalStateException if RestClient not dependency injected in instance of this class
     */
    public void initializeService() {
        if (mClient == null) {
            throw new IllegalStateException("RestClient not dependency injected in instance");
        }

        mService = mClient.getService(RestService.class);
    }

    /**
     * Performs an http DELETE API call.
     *
     * @param entity    The {@link Entity} whose ID or UUID is obtained and passed in the API call
     * @param headers   Additional http headers to pass into the the API call
     * @return          An RxJava {@link Single} containing the {@link ApiResponse}
     */
    @Override
    public Single<ApiResponse> delete(@NonNull Entity entity, Map<String, String> headers) {
        checkServiceState();
        return setObservableRequest(API_QUERY_DELETE, entity, headers);
    }

    /**
     * Performs an http GET API call.
     *
     * @param entity    The {@link Entity} whose values are retrieved and passed in the body of
     *                  the API call
     * @param headers   Additional http headers to pass into the the API call
     * @return          An RxJava {@link Single} containing the {@link ApiResponse}
     */
    @Override
    public Single<ApiResponse> get(@NonNull Entity entity, Map<String, String> headers) {
        checkServiceState();
        return setObservableRequest(API_QUERY_ROW, entity, headers);
    }

    /**
     * Performs an http GET API call with additional query string parameters in the request URL.
     *
     * @param entity    The {@link Entity} whose values are retrieved and passed in the API call
     * @param params    Name/value parameters sent in the request string of the API call
     * @param headers   Additional http headers to pass into the the API call
     * @return          An RxJava {@link Single} containing the {@link ApiResponse}
     */
    @Override
    public Single<ApiResponse> query(@NonNull Entity entity, Map<String, String> params, Map<String, String> headers) {
        checkServiceState();
        mQueryParams = params == null ? new ArrayMap<String, String>(0) : params;
        return setObservableRequest(API_QUERY_ROWS, entity, headers);
    }

    /**
     * Performs an http POST API call.
     *
     * @param entity    The {@link Entity} whose values are retrieved and passed in the API call
     * @param headers   Additional http headers to pass into the the API call
     * @return          An RxJava {@link Single} containing the {@link ApiResponse}
     */
    @Override
    public Single<ApiResponse> post(@NonNull Entity entity, Map<String, String> headers) {
        checkServiceState();
        return setObservableRequest(API_QUERY_INSERT, entity, headers);
    }

    /**
     * Performs an http PUT API call.
     *
     * @param entity    The {@link Entity} whose values are retrieved and passed in the API call
     * @param headers   Additional http headers to pass into the the API call
     * @return          An RxJava {@link Single} containing the {@link ApiResponse}
     */
    @Override
    public Single<ApiResponse> put(@NonNull Entity entity, Map<String, String> headers) {
        checkServiceState();
        return setObservableRequest(API_QUERY_UPDATE, entity, headers);
    }

    /**
     * Checks if the Retrofit API service has been initialized and returns true if so or
     * throws an {@link IllegalStateException} if not.
     *
     * @return True if service initialized
     * @throws IllegalStateException if service not initialized
     */
    protected boolean checkServiceState() {
        if (mClient == null) {
            throw new IllegalStateException("initializeService() must be called prior to API calls");
        }

        return true;
    }

    /**
     * Sets the request headers of the API call. In addition, special headers for authentication
     * containing the auth token and a hash for the request body are added as well.
     *
     * @param requestBody   The request body as a byte array
     * @param addHeaders    Additional request headers to add to the API call
     */
    protected void setHeaders(byte[] requestBody, Map<String, String> addHeaders) {
        if (mApiKey != null && mAuthToken != null) {
            String tokenHash = NetworkUtils.getRequestHash(mAuthToken, requestBody);
            if (tokenHash == null) {
                tokenHash = "";
            }

            mHeaders.put(Constants.HEADER_TOKEN, mApiKey);
            mHeaders.put(Constants.HEADER_TOKEN_HASH, tokenHash);
        }

        if (addHeaders != null) {
            mHeaders.putAll(addHeaders);
        }
    }

    /**
     * Sets the Retrofit service API call set in a {@link Single} observable.
     *
     * @param method        The endpoint for API call
     * @param entity        {@link Entity} whose values are used in request body of API call
     *                      or UUID used to retrieve a remote entity
     * @param addHeaders    Additional request headers to add to the API call
     * @return              The Single observable
     */
    protected Single<ApiResponse> setObservableRequest(String method, final Entity entity,
            final Map<String, String> addHeaders) {
        if (!NetworkUtils.hasInternetConnection(mContext)) {
            return Single.create(new SingleOnSubscribe<ApiResponse>() {
                    @Override
                    public void subscribe(SingleEmitter<ApiResponse> emitter) throws Exception {
                        emitter.onError(new NetworkConnectionException("No internet connection is available"));
                    }
                });
        }

        Single<Response<String>> apiCall = null;
        String tableName = entity.getTableName();
        String body = method.equals(API_QUERY_ROW) || method.equals(API_QUERY_ROWS) || method.equals(API_QUERY_DELETE)
                ? "" :
                entity.toJson(false);
        byte[] bodyBytes = body.getBytes();
        setHeaders(bodyBytes, addHeaders);

        switch(method) {
            case API_QUERY_ROW:
                apiCall = mService.row(tableName, mHeaders, entity.getUuid());
                break;
            case API_QUERY_ROWS:
                apiCall = mService.query(tableName, mHeaders, mQueryParams);
                break;
            case API_QUERY_DELETE:
                apiCall = mService.delete(tableName, mHeaders, entity.getUuid());
                break;
            case API_QUERY_INSERT:
            case API_QUERY_UPDATE:
                RequestBody requestBody = RequestBody.create(MediaType.parse(CONTENT_TYPE_POST), body);
                apiCall = method.equals(API_QUERY_INSERT)
                        ? mService.insert(tableName, mHeaders, requestBody)
                        : mService.update(tableName, mHeaders, requestBody);
                break;
            default:
                Timber.e("Service [" + method + "] not supported");
                return null;
        }

        return getResponseSingle(apiCall, entity);
    }

    /**
     * Converts a Retrofit response to an {@link ApiResponse} used in this application.
     *
     * @param apiCall   The {@link Single} observable with the Retrofit server response
     * @param entity    The {@link Entity} whose class will be typed for the ApiResponse
     * @return          The Single observable with the ApiResponse
     */
    protected Single<ApiResponse> getResponseSingle(final Single<Response<String>> apiCall,
            final Entity entity) {
        if (apiCall == null || entity == null) {
            return null;
        }

        return apiCall.flatMap(new Function<Response<String>, Single<ApiResponse>>() {
                    @Override
                    public Single<ApiResponse> apply(Response<String> stringResponse) throws Exception {
                        ApiResponse apiResponse = convertFromReactResponse(stringResponse, entity);
                        return Single.just(apiResponse);
                    }
                });
    }

    /**
     * Converts the Retrofit server response to an {@link ApiResponse}.
     *
     * @param response  The Retrofit response object
     * @param entity    The {@link Entity} whose class will be typed for the ApiResponse
     * @return          The ApiResponse object
     */
    private ApiResponse convertFromReactResponse(Response<String> response, Entity entity) {
        if (response == null || entity == null) {
            return null;
        }

        boolean isSuccess = response.isSuccessful();

        String body = response.body();
        if (isSuccess) {
            body = response.body();
        } else {
            ResponseBody errorBody = response.errorBody();
            if (errorBody != null) {
                try {
                    body = errorBody.string();
                } catch (IOException e) {
                    // Ignore exception, error will catch in ApiResponse instantiation
                }
            }
        }

        JsonObject json = null;
        try {
            json = getJsonObject(body);
        } catch (MalformedJsonException e) {
            Timber.e(e);
        }

        Headers rawHeaders = response.raw().headers();
        Map<String, String> headers = new HashMap<>();
        Set<String> names = rawHeaders.names();
        for (String name : names) {
            String value = rawHeaders.get(name);
            headers.put(name, value == null ? "" : value);
        }

        return new RestResponse(
                response.code(),
                json,
                headers,
                entity.getClass()
        );
    }

    /**
     * Converts a JSON string to {@link JsonObject}.
     *
     * @param jsonStr   The JSON string
     * @return          The JsonObject
     * @throws          MalformedJsonException if string parameter not parsable JSON
     */
    private JsonObject getJsonObject(String jsonStr) throws MalformedJsonException {
        if (jsonStr == null) {
            return null;
        }

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonStr);
        if (element.isJsonArray()) {
            // method only returns data objects, not arrays of any kind
            return null;
        }

        return element.getAsJsonObject();
    }
}
