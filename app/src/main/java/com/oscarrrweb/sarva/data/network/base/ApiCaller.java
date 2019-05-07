package com.oscarrrweb.sarva.data.network.base;

import androidx.annotation.NonNull;

import com.oscarrrweb.sarva.data.entity.base.Entity;

import java.util.Map;

import io.reactivex.Single;

/**
 * Base abstraction for API network calls. All network responses are handled through an
 * RxJava {@link Single} containing an {@link ApiResponse}.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface ApiCaller {

    /**
     * Performs an http DELETE API call.
     *
     * @param entity    The {@link Entity} whose ID or UUID is obtained and passed in the API call
     * @param headers   Additional http headers to pass into the the API call
     * @return          An RxJava {@link Single} containing the {@link ApiResponse}
     */
    Single<ApiResponse> delete(@NonNull Entity entity, Map<String, String> headers);

    /**
     * Performs an http GET API call.
     *
     * @param entity    The {@link Entity} whose values are retrieved and passed in the body of
     *                  the API call
     * @param headers   Additional http headers to pass into the the API call
     * @return          An RxJava {@link Single} containing the {@link ApiResponse}
     */
    Single<ApiResponse> get(@NonNull Entity entity, Map<String, String> headers);

    /**
     * Performs an http GET API call with additional query string parameters in the request URL.
     *
     * @param entity    The {@link Entity} whose values are retrieved and passed in the API call
     * @param params    Name/value parameters sent in the request string of the API call
     * @param headers   Additional http headers to pass into the the API call
     * @return          An RxJava {@link Single} containing the {@link ApiResponse}
     */
    Single<ApiResponse> query(@NonNull Entity entity, Map<String, String> params, Map<String, String> headers);

    /**
     * Performs an http POST API call.
     *
     * @param entity    The {@link Entity} whose values are retrieved and passed in the API call
     * @param headers   Additional http headers to pass into the the API call
     * @return          An RxJava {@link Single} containing the {@link ApiResponse}
     */
    Single<ApiResponse> post(@NonNull Entity entity, Map<String, String> headers);

    /**
     * Performs an http PUT API call.
     *
     * @param entity    The {@link Entity} whose values are retrieved and passed in the API call
     * @param headers   Additional http headers to pass into the the API call
     * @return          An RxJava {@link Single} containing the {@link ApiResponse}
     */
    Single<ApiResponse> put(@NonNull Entity entity, Map<String, String> headers);
}
