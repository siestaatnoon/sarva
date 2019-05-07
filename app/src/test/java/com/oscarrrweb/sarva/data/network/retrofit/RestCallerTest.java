package com.oscarrrweb.sarva.data.network.retrofit;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.oscarrrweb.sarva.data.entity.TestEntity;
import com.oscarrrweb.sarva.data.entity.base.Entity;
import com.oscarrrweb.sarva.data.network.base.ApiResponse;
import com.oscarrrweb.sarva.presentation.di.components.DaggerTestAppComponent;
import com.oscarrrweb.sarva.presentation.di.components.TestAppComponent;
import com.oscarrrweb.sarva.presentation.di.modules.TestAppModule;
import com.oscarrrweb.sarva.presentation.di.modules.TestDataModule;
import com.oscarrrweb.sarva.utils.AssertUtils;
import com.oscarrrweb.sarva.utils.FileUtils;
import com.oscarrrweb.sarva.utils.JsonUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.test.core.app.ApplicationProvider;
import io.reactivex.observers.TestObserver;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class RestCallerTest {

    private static final String ERROR_MESSAGE = "Hey, just testing OK";
    private static final String TEST_UUID_1 = "bbbd140f-2f60-44f7-bf1d-557820b64554";
    private static final String TEST_UUID_2 = "d287d73b-22b8-4a9b-a26d-3d31677cb787";

    private RestCaller api;
    private MockWebServer mockServer;
    private TestEntity mEntity1;
    private TestEntity mEntity2;

    @Before
    public void setUp() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        mockServer = new MockWebServer();
        HttpUrl baseUrl = mockServer.url("/");
        api = new RestCaller("username", "password");
        TestAppComponent component = DaggerTestAppComponent.builder()
                .testAppModule(new TestAppModule(context))
                .testDataModule(new TestDataModule(context, baseUrl.toString(), true))
                .build();
        component.inject(api);
        api.initializeService();
        //mockServer.start();

        mEntity1 = new TestEntity();
        mEntity1.setUuid(TEST_UUID_1);
        mEntity1.setImageFile(FileUtils.getSampleImageBytesFromAndroidRes(context));
        mEntity1.setTest(true);
        mEntity1.touch();

        mEntity2 = new TestEntity();
        mEntity2.setUuid(TEST_UUID_2);
        mEntity2.setImageFile(FileUtils.getSampleImageBytesFromTestResources());
        mEntity2.setTest(false);
        mEntity2.touch();
    }

    @Test
    public void RestCaller_shouldDeleteWithSuccess() throws Exception {
        String expectedURL = "/" + mEntity1.getTableName() + "/" + RestCaller.API_QUERY_DELETE + "/" + TEST_UUID_1;

        // create a successful response and queue it
        int count = 1;
        JsonObject json = new JsonObject();
        json.add("success", new JsonPrimitive(true));
        json.add("count", new JsonPrimitive(count));
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(RestResponse.HTTP_OK)
                .setBody(json.toString());
        mockServer.enqueue(mockResponse);

        // make our call
        TestObserver<ApiResponse> testObserver = TestObserver.create();
        api.delete(mEntity1, null).subscribe(testObserver);

        //
        // Check our success response
        //

        RecordedRequest request = mockServer.takeRequest(1, TimeUnit.SECONDS);
        ApiResponse apiResponse = null;
        if (testObserver.values().size() > 0) {
            apiResponse = testObserver.values().get(0);
        }

        assertNotNull("Mock request null", request);
        assertNotNull("apiResponse null", apiResponse);
        assertEquals("DELETE urls not equal", expectedURL, request.getPath());

        RestResponse restResponse = (RestResponse) apiResponse;
        TestEntity entity = (TestEntity) restResponse.getEntity();
        assertNotNull("RestResponse.getEntity null", entity);
        assertEquals("Response code not equal", RestResponse.HTTP_OK, restResponse.getStatusCode());
        assertEquals("Count not equal", count, restResponse.getCount());
        assertTrue("Success is false", restResponse.isSuccess());

        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.dispose();
    }

    @Test
    public void RestCaller_shouldDeleteWithError() throws Exception {
        String expectedURL = "/" + mEntity1.getTableName() + "/" + RestCaller.API_QUERY_DELETE + "/" + TEST_UUID_1;

        // create a server error response and queue it
        JsonObject json = new JsonObject();
        json.add("success", new JsonPrimitive(false));
        json.add("message", new JsonPrimitive(ERROR_MESSAGE));
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(RestResponse.HTTP_ERROR_SERVER)
                .setBody(json.toString());
        mockServer.enqueue(mockResponse);

        // make our call
        TestObserver<ApiResponse> errorObserver = TestObserver.create();
        api.delete(mEntity1, null).subscribe(errorObserver);

        //
        // Check our server error response
        //

        RecordedRequest request = mockServer.takeRequest(1, TimeUnit.SECONDS);
        ApiResponse apiResponse = null;
        if (errorObserver.values().size() > 0) {
            apiResponse = errorObserver.values().get(0);
        }

        assertNotNull("Mock request null", request);
        assertNotNull("apiResponse null", apiResponse);
        assertEquals("DELETE urls not equal", expectedURL, request.getPath());

        RestResponse restResponse = (RestResponse) apiResponse;
        assertEquals("Response code not equal", RestResponse.HTTP_ERROR_SERVER, restResponse.getStatusCode());
        assertEquals("Error message not equal", ERROR_MESSAGE, restResponse.getError());
        assertFalse("Success is true", restResponse.isSuccess());

        errorObserver.assertComplete();
        errorObserver.assertNoErrors();
        errorObserver.assertValueCount(1);
        errorObserver.dispose();
    }

    @Test
    public void RestCaller_shouldGetWithSuccess() throws Exception {
        String expectedURL = "/" + mEntity1.getTableName() + "/" + RestCaller.API_QUERY_ROW + "/" + TEST_UUID_1;

        // create a successful response and queue it
        JsonObject json = new JsonObject();
        json.add("success", new JsonPrimitive(true));
        json.add("item", JsonUtils.toJsonElement(mEntity1.toJson()));
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(RestResponse.HTTP_OK)
                .setBody(json.toString());
        mockServer.enqueue(mockResponse);

        // make our call
        TestObserver<ApiResponse> testObserver = TestObserver.create();
        api.get(mEntity1, null).subscribe(testObserver);

        //
        // Check our success response
        //

        RecordedRequest request = mockServer.takeRequest(1, TimeUnit.SECONDS);
        ApiResponse apiResponse = null;
        if (testObserver.values().size() > 0) {
            apiResponse = testObserver.values().get(0);
        }

        assertNotNull("Mock request null", request);
        assertNotNull("apiResponse null", apiResponse);
        assertEquals("GET urls not equal", expectedURL, request.getPath());

        RestResponse restResponse = (RestResponse) apiResponse;
        TestEntity entity = (TestEntity) restResponse.getEntity();
        assertNotNull("RestResponse.getEntity null", entity);
        assertEquals("Response code not equal", RestResponse.HTTP_OK, restResponse.getStatusCode());
        assertTrue("Success is false", restResponse.isSuccess());
        AssertUtils.assertEntitiesEqual(mEntity1, entity);

        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.dispose();
    }

    @Test
    public void RestCaller_shouldGetWithError() throws Exception {
        String expectedURL = "/" + mEntity1.getTableName() + "/" + RestCaller.API_QUERY_ROW + "/" + TEST_UUID_1;

        // create a server error response and queue it
        JsonObject json = new JsonObject();
        json.add("success", new JsonPrimitive(false));
        json.add("message", new JsonPrimitive(ERROR_MESSAGE));
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(RestResponse.HTTP_ERROR_SERVER)
                .setBody(json.toString());
        mockServer.enqueue(mockResponse);

        // make our call
        TestObserver<ApiResponse> errorObserver = TestObserver.create();
        api.get(mEntity1, null).subscribe(errorObserver);

        //
        // Check our server error response
        //

        RecordedRequest request = mockServer.takeRequest(1, TimeUnit.SECONDS);
        ApiResponse apiResponse = null;
        if (errorObserver.values().size() > 0) {
            apiResponse = errorObserver.values().get(0);
        }

        assertNotNull("Mock request null", request);
        assertNotNull("apiResponse null", apiResponse);
        assertEquals("GET urls not equal", expectedURL, request.getPath());

        RestResponse restResponse = (RestResponse) apiResponse;
        assertEquals("Response code not equal", RestResponse.HTTP_ERROR_SERVER, restResponse.getStatusCode());
        assertEquals("Error message not equal", ERROR_MESSAGE, restResponse.getError());
        assertFalse("Success is true", restResponse.isSuccess());

        errorObserver.assertComplete();
        errorObserver.assertNoErrors();
        errorObserver.assertValueCount(1);
        errorObserver.dispose();
    }

    @Test
    public void RestCaller_shouldQueryWithSuccess() throws Exception {
        String expectedURL = "/" + mEntity1.getTableName() + "/query";

        // create a successful response and queue it
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(JsonUtils.toJsonElement(mEntity1.toJson()));
        jsonArray.add(JsonUtils.toJsonElement(mEntity2.toJson()));
        JsonObject json = new JsonObject();
        json.add("success", new JsonPrimitive(true));
        json.add("items", jsonArray);
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(RestResponse.HTTP_OK)
                .setBody(json.toString());
        mockServer.enqueue(mockResponse);

        // make our calls
        TestObserver<ApiResponse> testObserver = TestObserver.create();
        api.query(mEntity1, null, null).subscribe(testObserver);

        //
        // Check our success response
        //

        RecordedRequest request = mockServer.takeRequest(1, TimeUnit.SECONDS);
        ApiResponse apiResponse = null;
        if (testObserver.values().size() > 0) {
            apiResponse = testObserver.values().get(0);
        }

        assertNotNull("Mock request null", request);
        assertNotNull("apiResponse null", apiResponse);
        assertEquals("GET urls not equal", expectedURL, request.getPath());

        RestResponse restResponse = (RestResponse) apiResponse;
        List<Entity> entities = restResponse.getEntityCollection();
        assertNotNull("RestResponse.getEntityCollection null", entities);
        assertEquals("Response code not equal", RestResponse.HTTP_OK, restResponse.getStatusCode());
        assertTrue("Success is false", restResponse.isSuccess());
        assertEquals("Entity count not 2", 2, entities.size());
        AssertUtils.assertEntitiesEqual(mEntity1, (TestEntity) entities.get(0));
        AssertUtils.assertEntitiesEqual(mEntity2, (TestEntity) entities.get(1));

        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.dispose();
    }

    @Test
    public void RestCaller_shouldQueryWithError() throws Exception {
        String expectedURL = "/" + mEntity1.getTableName() + "/" + RestCaller.API_QUERY_ROWS;

        // create a server error response and queue it
        JsonObject json = new JsonObject();
        json.add("success", new JsonPrimitive(false));
        json.add("message", new JsonPrimitive(ERROR_MESSAGE));
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(RestResponse.HTTP_ERROR_SERVER)
                .setBody(json.toString());
        mockServer.enqueue(mockResponse);

        // make our call
        TestObserver<ApiResponse> errorObserver = TestObserver.create();
        api.query(mEntity1, null, null).subscribe(errorObserver);

        //
        // Check our server error response
        //

        RecordedRequest request = mockServer.takeRequest(1, TimeUnit.SECONDS);
        ApiResponse apiResponse = null;
        if (errorObserver.values().size() > 0) {
            apiResponse = errorObserver.values().get(0);
        }

        assertNotNull("Mock request null", request);
        assertNotNull("apiResponse null", apiResponse);
        assertEquals("GET urls not equal", expectedURL, request.getPath());

        RestResponse restResponse = (RestResponse) apiResponse;
        assertEquals("Response code not equal", RestResponse.HTTP_ERROR_SERVER, restResponse.getStatusCode());
        assertEquals("Error message not equal", ERROR_MESSAGE, restResponse.getError());
        assertFalse("Success is true", restResponse.isSuccess());

        errorObserver.assertComplete();
        errorObserver.assertNoErrors();
        errorObserver.assertValueCount(1);
        errorObserver.dispose();
    }

    @Test
    public void RestCaller_shouldPostWithSuccess() throws Exception {
        String expectedURL = "/" + mEntity1.getTableName() + "/" + RestCaller.API_QUERY_INSERT;

        // create a successful response and queue it
        JsonObject json = new JsonObject();
        json.add("success", new JsonPrimitive(true));
        json.add("uuid", new JsonPrimitive(TEST_UUID_2));
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(RestResponse.HTTP_OK)
                .setBody(json.toString());
        mockServer.enqueue(mockResponse);

        // make our call
        TestObserver<ApiResponse> testObserver = TestObserver.create();
        api.post(mEntity2, null).subscribe(testObserver);

        //
        // Check our success response
        //

        RecordedRequest request = mockServer.takeRequest(1, TimeUnit.SECONDS);
        ApiResponse apiResponse = null;
        if (testObserver.values().size() > 0) {
            apiResponse = testObserver.values().get(0);
        }

        assertNotNull("Mock request null", request);
        assertNotNull("apiResponse null", apiResponse);
        assertEquals("POST urls not equal", expectedURL, request.getPath());

        RestResponse restResponse = (RestResponse) apiResponse;
        JsonObject responseJson = restResponse.getJsonResult();
        assertNotNull("RestResponse.getJsonResult null", responseJson);
        assertEquals("Response code not equal", RestResponse.HTTP_OK, restResponse.getStatusCode());
        assertEquals("UUIDs not equal", TEST_UUID_2, responseJson.get("uuid").getAsString());
        assertTrue("Success is false", restResponse.isSuccess());

        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.dispose();
    }

    @Test
    public void RestCaller_shouldPostWithError() throws Exception {
        String expectedURL = "/" + mEntity1.getTableName() + "/" + RestCaller.API_QUERY_INSERT;

        // create a server error response and queue it
        JsonObject json = new JsonObject();
        json.add("success", new JsonPrimitive(false));
        json.add("message", new JsonPrimitive(ERROR_MESSAGE));
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(RestResponse.HTTP_ERROR_SERVER)
                .setBody(json.toString());
        mockServer.enqueue(mockResponse);

        // make our call
        TestObserver<ApiResponse> errorObserver = TestObserver.create();
        api.post(mEntity1, null).subscribe(errorObserver);

        //
        // Check our server error response
        //

        RecordedRequest request = mockServer.takeRequest(1, TimeUnit.SECONDS);
        ApiResponse apiResponse = null;
        if (errorObserver.values().size() > 0) {
            apiResponse = errorObserver.values().get(0);
        }

        assertNotNull("Mock request null", request);
        assertNotNull("apiResponse null", apiResponse);
        assertEquals("POST urls not equal", expectedURL, request.getPath());

        RestResponse restResponse = (RestResponse) apiResponse;
        assertEquals("Response code not equal", RestResponse.HTTP_ERROR_SERVER, restResponse.getStatusCode());
        assertEquals("Error message not equal", ERROR_MESSAGE, restResponse.getError());
        assertFalse("Success is true", restResponse.isSuccess());

        errorObserver.assertComplete();
        errorObserver.assertNoErrors();
        errorObserver.assertValueCount(1);
        errorObserver.dispose();
    }

    @Test
    public void RestCaller_shouldPutWithSuccess() throws Exception {
        String expectedURL = "/" + mEntity1.getTableName() + "/" + RestCaller.API_QUERY_UPDATE;

        // create a successful response and queue it
        JsonObject json = new JsonObject();
        json.add("success", new JsonPrimitive(true));
        json.add("uuid", new JsonPrimitive(TEST_UUID_2));
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(RestResponse.HTTP_OK)
                .setBody(json.toString());
        mockServer.enqueue(mockResponse);

        // make our call
        TestObserver<ApiResponse> testObserver = TestObserver.create();
        api.put(mEntity2, null).subscribe(testObserver);

        //
        // Check our success response
        //

        RecordedRequest request = mockServer.takeRequest(1, TimeUnit.SECONDS);
        ApiResponse apiResponse = null;
        if (testObserver.values().size() > 0) {
            apiResponse = testObserver.values().get(0);
        }

        assertNotNull("Mock request null", request);
        assertNotNull("apiResponse null", apiResponse);
        assertEquals("PUT urls not equal", expectedURL, request.getPath());

        RestResponse restResponse = (RestResponse) apiResponse;
        JsonObject responseJson = restResponse.getJsonResult();
        assertNotNull("RestResponse.getJsonResult null", responseJson);
        assertEquals("Response code not equal", RestResponse.HTTP_OK, restResponse.getStatusCode());
        assertEquals("UUIDs not equal", TEST_UUID_2, responseJson.get("uuid").getAsString());
        assertTrue("Success is false", restResponse.isSuccess());

        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.dispose();
    }

    @Test
    public void RestCaller_shouldPutWithError() throws Exception {
        String expectedURL = "/" + mEntity1.getTableName() + "/" + RestCaller.API_QUERY_UPDATE;

        // create a server error response and queue it
        JsonObject json = new JsonObject();
        json.add("success", new JsonPrimitive(false));
        json.add("message", new JsonPrimitive(ERROR_MESSAGE));
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(RestResponse.HTTP_ERROR_SERVER)
                .setBody(json.toString());
        mockServer.enqueue(mockResponse);

        // make our call
        TestObserver<ApiResponse> errorObserver = TestObserver.create();
        api.put(mEntity1, null).subscribe(errorObserver);

        //
        // Check our server error response
        //

        RecordedRequest request = mockServer.takeRequest(1, TimeUnit.SECONDS);
        ApiResponse apiResponse = null;
        if (errorObserver.values().size() > 0) {
            apiResponse = errorObserver.values().get(0);
        }

        assertNotNull("Mock request null", request);
        assertNotNull("apiResponse null", apiResponse);
        assertEquals("PUT urls not equal", expectedURL, request.getPath());

        RestResponse restResponse = (RestResponse) apiResponse;
        assertEquals("Response code not equal", RestResponse.HTTP_ERROR_SERVER, restResponse.getStatusCode());
        assertEquals("Error message not equal", ERROR_MESSAGE, restResponse.getError());
        assertFalse("Success is true", restResponse.isSuccess());

        errorObserver.assertComplete();
        errorObserver.assertNoErrors();
        errorObserver.assertValueCount(1);
        errorObserver.dispose();
    }

    @After
    public void tearDown() throws Exception {
        mockServer.shutdown();
        api = null;
        mEntity1 = null;
        mEntity2 = null;
    }
}
