package com.cccdlabs.sarva.presentation.exception;

import android.content.Context;

import com.cccdlabs.sarva.R;
import com.cccdlabs.sarva.data.network.exception.NetworkConnectionException;
import com.cccdlabs.sarva.data.network.retrofit.RestException;
import com.cccdlabs.sarva.data.network.retrofit.RestResponse;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryDeleteException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryInsertException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryQueryException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryUpdateException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import androidx.test.core.app.ApplicationProvider;
import io.reactivex.annotations.Nullable;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.HttpException;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ErrorMessageFactoryTest {

    private static final String DUMMY_PARAM = "Dummy";
    private Context context;
    private ResponseBody stubResponseBody = new ResponseBody() {
        @Nullable
        @Override
        public MediaType contentType() {
            return null;
        }

        @Override
        public long contentLength() {
            return 0;
        }

        @Override
        public BufferedSource source() {
            return null;
        }
    };

    @Before
    public void setUp() throws Exception {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void shouldReturnNetworkConnectionExceptionMessage() throws Exception {
        NetworkConnectionException exception = new NetworkConnectionException();
        String message = ErrorMessageFactory.create(context, exception);
        assertEquals("Exception messages not equal", message, context.getString(R.string.error_network_connection));
    }

    @Test
    public void shouldReturnRepositoryInsertExceptionMessage() throws Exception {
        RepositoryInsertException exception = new RepositoryInsertException();
        String message = ErrorMessageFactory.create(context, exception);
        assertEquals("Exception messages not equal", message, context.getString(R.string.error_respository_general));

        message = ErrorMessageFactory.create(context, exception, DUMMY_PARAM);
        String toVerify = String.format(context.getString(R.string.error_respository_insert), DUMMY_PARAM);
        assertEquals("Exception messages with param not equal", message, toVerify);
    }

    @Test
    public void shouldReturnRepositoryUpdateExceptionMessage() throws Exception {
        RepositoryUpdateException exception = new RepositoryUpdateException();
        String message = ErrorMessageFactory.create(context, exception);
        assertEquals("Exception messages not equal", message, context.getString(R.string.error_respository_general));

        message = ErrorMessageFactory.create(context, exception, DUMMY_PARAM);
        String toVerify = String.format(context.getString(R.string.error_respository_update), DUMMY_PARAM);
        assertEquals("Exception messages with param not equal", message, toVerify);
    }

    @Test
    public void shouldReturnRepositoryDeleteExceptionMessage() throws Exception {
        RepositoryDeleteException exception = new RepositoryDeleteException();
        String message = ErrorMessageFactory.create(context, exception);
        assertEquals("Exception messages not equal", message, context.getString(R.string.error_respository_general));

        message = ErrorMessageFactory.create(context, exception, DUMMY_PARAM);
        String toVerify = String.format(context.getString(R.string.error_respository_delete), DUMMY_PARAM);
        assertEquals("Exception messages with param not equal", message, toVerify);
    }

    @Test
    public void shouldReturnRepositoryQueryExceptionMessage() throws Exception {
        RepositoryQueryException exception = new RepositoryQueryException();
        String message = ErrorMessageFactory.create(context, exception);
        assertEquals("Exception messages not equal", message, context.getString(R.string.error_respository_general));

        message = ErrorMessageFactory.create(context, exception, DUMMY_PARAM);
        String toVerify = String.format(context.getString(R.string.error_respository_query), DUMMY_PARAM);
        assertEquals("Exception messages with param not equal", message, toVerify);
    }

    @Test
    public void shouldReturnApiErrorMessage() throws Exception {
        Response<RestResponse> response = Response.error(RestResponse.HTTP_ERROR_UNAUTHORIZED, stubResponseBody);
        HttpException httpException = new HttpException(response);
        RestException restException = new RestException(httpException);
        String message = ErrorMessageFactory.create(context, restException);
        assertEquals("Exception [HTTP_ERROR_UNAUTHORIZED] messages not equal", message, context.getString(R.string.error_authentication));

        response = Response.error(RestResponse.HTTP_ERROR_SERVER, stubResponseBody);
        httpException = new HttpException(response);
        restException = new RestException(httpException);
        message = ErrorMessageFactory.create(context, restException);
        assertEquals("Exception [HTTP_ERROR_SERVER] messages not equal", message, context.getString(R.string.error_network_server));

        SocketTimeoutException timeoutException = new SocketTimeoutException();
        restException = new RestException(timeoutException);
        message = ErrorMessageFactory.create(context, restException);
        assertEquals("Exception [timeout] messages not equal", message, context.getString(R.string.error_network_timeout));

        UnknownHostException unknownHostException = new UnknownHostException();
        restException = new RestException(unknownHostException);
        message = ErrorMessageFactory.create(context, restException);
        assertEquals("Exception [unknown host] messages not equal", message, context.getString(R.string.error_network_connection));
    }

    @Test
    public void shouldReturnGenericExceptionMessage() throws Exception {
        Exception exception = new Exception();
        String message = ErrorMessageFactory.create(context, exception);
        assertEquals("Exception messages not equal", message, context.getString(R.string.error_unknown));
    }

    @After
    public void tearDown() throws Exception {
        context = null;
    }
}
