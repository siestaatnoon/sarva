package com.cccdlabs.sarva.data.network.retrofit;

import com.google.gson.JsonObject;
import com.cccdlabs.sarva.data.network.base.ApiError;
import com.cccdlabs.sarva.data.network.base.ApiResponse;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * Exception implementing {@link ApiError} for errors occurring for the <code>retrofit</code>
 * package API calls.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class RestException extends Exception implements ApiError {

    /**
     * The server response status code.
     */
    public int statusCode;

    /**
     * The server response body.
     */
    public byte[] responseBody;

    /**
     * The returned server response headers.
     */
    public Map<String, String> headers;

    /**
     * Flag if an authentication error (401) or a timeout error (423) occurred.
     */
    private boolean isAuthFailureError;

    /**
     * Flag ig a server error occurred (500).
     */
    private boolean isNetworkError;

    /**
     * Flag if a connection could not be established with the server.
     */
    private boolean isNoConnectionError;

    /**
     * Flag if a server timeout occurred.
     */
    private boolean isTimeoutError;

    /**
     * Constructor.
     *
     * @param message   The error message
     * @param cause     Throwable object triggering this exception
     */
    public RestException(String message, Throwable cause) {
        super(message, cause);
        setDefaults();
    }

    /**
     * Constructor.
     *
     * @param message   The error message
     */
    public RestException(String message) {
        super(message);
        setDefaults();
    }

    /**
     * Constructor.
     *
     * @param cause Throwable object triggering this exception
     */
    public RestException(Throwable cause) {
        super(cause);
        setDefaults();

        if (cause instanceof HttpException) {
            HttpException exception = (HttpException) cause;
            Response raw = exception.response().raw();
            statusCode = raw.code();
            isAuthFailureError = statusCode == RestResponse.HTTP_ERROR_UNAUTHORIZED ||
                    statusCode == RestResponse.HTTP_ERROR_LOCKOUT;
            isNetworkError = ! isAuthFailureError;

            // Set response headers
            Headers rawHeaders = raw.headers();
            Set<String> names = rawHeaders.names();
            for (String name : names) {
                String headerValue = rawHeaders.get(name);
                if (headerValue != null) {
                    headers.put(name, headerValue);
                }
            }


            // Set response body
            try {
                ResponseBody body = raw.body();
                if (body != null) {
                    String str = body.string();
                    responseBody = str.getBytes();
                }
            } catch (IOException e) {
                // Ignore exception and use defaults for values
            }
        } else if (cause instanceof SocketTimeoutException) {
            isTimeoutError = true;
        } else if (cause instanceof UnknownHostException) {
            isNoConnectionError = true;
        }
    }

    /**
     * Constructor.
     *
     * @param response {@link ApiResponse} parsed from server response
     */
    public RestException(ApiResponse response) {
        super(response.getError());
        setDefaults();

        JsonObject json = response.getJsonResult();
        String body = json == null ? "" : json.toString();
        responseBody = body.getBytes();
        statusCode = response.getStatusCode();
        headers = response.getHeaders();

        isAuthFailureError = statusCode == RestResponse.HTTP_ERROR_UNAUTHORIZED ||
                statusCode == RestResponse.HTTP_ERROR_LOCKOUT;
        isNetworkError = statusCode == RestResponse.HTTP_ERROR_SERVER;
    }

    /**
     * Returns true if a server authentication (401) or user timeout (423) occurred.
     *
     * @return True if authentication or timeout error occurred, false if not
     */
    @Override
    public boolean isAuthFailureError()  {
        return isAuthFailureError;
    }

    /**
     * Returns true if a server-side error (500) occurred.
     *
     * @return True if server-side error occurred, false if not
     */
    @Override
    public boolean isNetworkError() {
        return isNetworkError;
    }

    /**
     * Returns true if a server connection could not be established.
     *
     * @return True if server connection could not be established, false if not
     */
    @Override
    public boolean isNoConnectionError() {
        return isNoConnectionError;
    }

    /**
     * Returns true if a server user timeout occurred.
     *
     * @return True if server user timeout occurred, false if not
     */
    @Override
    public boolean isTimeoutError() {
        return isTimeoutError;
    }

    /**
     * Sets the default public members of this class.
     */
    private void setDefaults() {
        statusCode = RestResponse.HTTP_ERROR_SERVER;
        responseBody = new byte[]{};
        headers = new HashMap<>();
    }
}

