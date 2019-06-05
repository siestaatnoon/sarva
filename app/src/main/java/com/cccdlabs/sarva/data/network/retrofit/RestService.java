package com.cccdlabs.sarva.data.network.retrofit;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * <a href="https://square.github.io/retrofit/" target="_top">Retrofit</a> service abstraction
 * for API endpoints used in this application.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface RestService {

    /**
     * API GET call for retrieving a single entity.
     *
     * @param tableName     The table name or name of entity to retrieve
     * @param headers       Request headers to add to the API call
     * @param uuid          UUID of entity to be retrieved
     * @return              An RxJava {@link Single} to emit the Retrofit {@link Response} from the
     *                      server.
     * @see                 <a href="http://reactivex.io/documentation/single.html" target="_top">Single</a>
     */
    @GET("/{table_name}/row/{uuid}")
    Single<Response<String>> row(@Path("table_name") String tableName, @HeaderMap Map<String, String> headers,
                                 @Path("uuid") String uuid);
    /**
     * API GET call for retrieving multiple entities based on query parameters.
     *
     * @param tableName     The table name or name of entity to retrieve
     * @param headers       Request headers to add to the API call
     * @param params        The query parameters
     * @return              An RxJava {@link Single} to emit the Retrofit {@link Response} from the
     *                      server.
     * @see                 <a href="http://reactivex.io/documentation/single.html" target="_top">Single</a>
     */
    @GET("/{table_name}/query")
    Single<Response<String>> query(@Path("table_name") String tableName, @HeaderMap Map<String, String> headers,
                                   @QueryMap Map<String, String> params);
    /**
     * API DELETE call for deleting a single entity.
     *
     * @param tableName     The table name or name of entity to delete
     * @param headers       Request headers to add to the API call
     * @param uuid          UUID of entity to be deleted
     * @return              An RxJava {@link Single} to emit the Retrofit {@link Response} from the
     *                      server.
     * @see                 <a href="http://reactivex.io/documentation/single.html" target="_top">Single</a>
     */
    @DELETE("/{table_name}/delete/{uuid}")
    Single<Response<String>> delete(@Path("table_name") String tableName, @HeaderMap Map<String, String> headers,
                                    @Path("uuid") String uuid);
    /**
     * API POST call for inserting a single entity.
     *
     * @param tableName     The table name or name of entity to insert
     * @param headers       Request headers to add to the API call
     * @param body          The request body containing the values to insert
     * @return              An RxJava {@link Single} to emit the Retrofit {@link Response} from the
     *                      server.
     * @see                 <a href="http://reactivex.io/documentation/single.html" target="_top">Single</a>
     */
    @POST("/{table_name}/insert")
    Single<Response<String>> insert(@Path("table_name") String tableName, @HeaderMap Map<String, String> headers,
                                    @Body RequestBody body);
    /**
     * API PUT call for updating a single entity.
     *
     * @param tableName     The table name or name of entity to update
     * @param headers       Request headers to add to the API call
     * @param body          The request body containing the values to update
     * @return              An RxJava {@link Single} to emit the Retrofit {@link Response} from the
     *                      server.
     * @see                 <a href="http://reactivex.io/documentation/single.html" target="_top">Single</a>
     */
    @PUT("/{table_name}/update")
    Single<Response<String>> update(@Path("table_name") String tableName, @HeaderMap Map<String, String> headers,
                                    @Body RequestBody body);
}
