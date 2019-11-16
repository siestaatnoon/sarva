package com.cccdlabs.sarva.data.p2p.nearby;

import android.app.Activity;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.cccdlabs.sarva.data.p2p.base.MockMessagesClient;
import com.cccdlabs.sarva.data.p2p.nearby.client.MockPartnerNearbyMessagesClient;
import com.cccdlabs.sarva.data.p2p.nearby.client.PartnerNearbyMessagesClient;
import com.cccdlabs.sarva.data.p2p.nearby.exception.PermissionException;
import com.cccdlabs.sarva.data.p2p.nearby.utils.NearbyUtils;
import com.cccdlabs.sarva.data.settings.GeneralSettingsManager;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryQueryException;
import com.cccdlabs.sarva.presentation.di.components.DaggerTestDataComponent;
import com.cccdlabs.sarva.presentation.di.components.TestDataComponent;
import com.cccdlabs.sarva.presentation.di.modules.TestDataModule;
import com.google.android.gms.nearby.messages.Message;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class PartnerBroadcastEmitterTest {

    private static final String TEST_UUID = "9d29e5e4-3d33-4f90-9453-5b3e8af963ed";
    private static final String TEST_USERNAME = "Johnny";

    private MockMessagesClient mClient;
    private PartnerBroadcastEmitter mEmitter;
    private GeneralSettingsManager mSettings;
    private Exception mException = new RepositoryQueryException("Unit test error");
    private DefaultErrorHandler mDefaultErrorHandler;

    class DefaultErrorHandler implements Consumer<Throwable> {

        private boolean hasPassedThrough = false;

        @Override
        public void accept(Throwable throwable) throws Exception {
            assertTrue(
                    "Throwable not of UndeliverableException",
                    throwable instanceof UndeliverableException
            );
            UndeliverableException undeliverable = (UndeliverableException) throwable;
            Throwable t1 = undeliverable.getCause();
            assertTrue(
                    "Throwable not of RuntimeException",
                    t1 instanceof RuntimeException
            );
            Throwable t2 = t1.getCause();
            assertTrue(
                    "Throwable not of RepositoryQueryException",
                    t2 instanceof RepositoryQueryException
            );
            hasPassedThrough = true;
        }

        public boolean hasPassedThrough() {
            return hasPassedThrough;
        }
    };

    class MockPartnerBroadcastEmitter extends PartnerBroadcastEmitter {

        private Context context;
        private MockMessagesClient mockMessagesClient;

        MockPartnerBroadcastEmitter(Context context, MockMessagesClient mockMessagesClient) {
            super(mock(Activity.class));
            this.context = context;
            this.mockMessagesClient = mockMessagesClient;
        }

        @Override
        protected PartnerNearbyMessagesClient getClient() {
            mClient = MockPartnerNearbyMessagesClient.get(
                    context,
                    mockMessagesClient,
                    true,
                    false,
                    PartnerMessage.Mode.CHECK
            );
            return mClient;
        }
    }

    @Before
    public void setup() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        TestDataComponent dataComponent = DaggerTestDataComponent.builder()
                .testDataModule(new TestDataModule(context, true))
                .build();

        // Since we won't mock the settings, set the UUID and username
        mSettings = new GeneralSettingsManager(context);
        mSettings.setUuid(TEST_UUID);
        mSettings.setUsername(TEST_USERNAME);

        mClient = new MockMessagesClient(context, true);
        mEmitter = new MockPartnerBroadcastEmitter(context, mClient);
        mDefaultErrorHandler = new DefaultErrorHandler();
        RxJavaPlugins.setErrorHandler(mDefaultErrorHandler);
    }

    @Test
    public void testIsPublishingAndSendingMessage() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mEmitter.getPartnerFlowable().test();
        Message message = mClient.capturePublishMessage();
        PartnerMessage partnerMessage = NearbyUtils.toPartnerMessage(message);

        assertTrue("Message client not publishing", mClient.isPublishing());
        assertEquals("UUID not equal", TEST_UUID, partnerMessage.getUuid());
        assertEquals("Username not equal", TEST_USERNAME, partnerMessage.getUsername());
        assertEquals("Device name not equal", NearbyUtils.getDeviceName(), partnerMessage.getDeviceName());
        assertEquals("Mode not equal", PartnerMessage.Mode.CHECK, partnerMessage.getMode());

        subscriber.dispose();
    }

    @Test
    public void testIsNotSubscribing() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mEmitter.getPartnerFlowable().test();
        assertTrue("Message client not publishing", mClient.isPublishing());
        assertFalse("Message client is subscribing", mClient.isSubscribing());

        subscriber.dispose();
    }

    @Test
    public void testOnPermissionChangedFalseStopsPubSub() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mEmitter.getPartnerFlowable().test();
        mClient.mockStatusCallbackOnPermissionChanged(false);

        subscriber.assertError(PermissionException.class);
        assertFalse("Message client is publishing", mEmitter.isPublishing());
        assertFalse("Message client is subscribing", mEmitter.isSubscribing());

        subscriber.dispose();
    }

    @Test
    public void testOnPermissionChangedTrueDoesNothing() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mEmitter.getPartnerFlowable().test();
        mClient.mockStatusCallbackOnPermissionChanged(true);

        subscriber.assertNoErrors();
        assertTrue("Message client is not publishing", mEmitter.isPublishing());
        assertFalse("Message client is subscribing", mEmitter.isSubscribing());

        subscriber.dispose();
    }

    @Test
    public void testOnPublishExpiredEmitsPartnerResult() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = new TestSubscriber<PartnerResult>() {
            private int count = 0;

            @Override
            public void onNext(PartnerResult partnerResult) {
                assertNotNull("PartnerResult null", partnerResult);
                assertNotNull("PartnerResult.Status is null", partnerResult.getStatus());
                count++;
            }

            @Override
            public void onError(Throwable throwable) {
                throw fail("Exception should be passed into onNext(), " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                assertEquals("Emission count not 1", 1, count);
            }
        };

        mEmitter.getPartnerFlowable().subscribe(subscriber);
        mClient.mockPublishExpired();

        // client should not be publishing
        assertFalse("Message client is publishing", mEmitter.isPublishing());

        // ... and not subscribing
        assertFalse("Message client is subscribing", mEmitter.isSubscribing());

        subscriber.dispose();
    }

    @Test
    public void testPublishHandlesOnFailureListener() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = new TestSubscriber<PartnerResult>() {

            private boolean hasError = false;

            @Override
            public void onNext(PartnerResult partnerResult) {
                // onSuccess subscribe status may pass through here so verify it is that
                assertNotNull("PartnerResult null", partnerResult);
                assertNotNull("PartnerResult.Status is null", partnerResult.getStatus());
            }

            @Override
            public void onError(Throwable throwable) {
                assertTrue(
                        "Throwable not of RepositoryQueryException",
                        throwable instanceof RepositoryQueryException
                );
                hasError = true;
            }

            @Override
            public void onComplete() {
                assertTrue("Error has not been passed to onError()", hasError);
            }
        };
        TestSubscriber<PartnerResult> subscriberSpy = spy(subscriber);
        mClient.mockPublishFailure(mException);
        mEmitter.getPartnerFlowable().subscribe(subscriberSpy);

        verify(subscriberSpy, times(1)).onError(mException);
        assertFalse("Message client is publishing", mClient.isPublishing());
        assertFalse("Message client is subscribing", mClient.isSubscribing());

        subscriberSpy.dispose();
    }

    @Test
    public void testUnpublishHandlesOnFailureListener() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = new TestSubscriber<PartnerResult>() {

            private int count = 0;

            @Override
            public void onNext(PartnerResult partnerResult) {
                assertNotNull("PartnerResult null", partnerResult);
                assertNotNull("PartnerResult.Status is null", partnerResult.getStatus());
                count++;
            }

            @Override
            public void onError(Throwable throwable) {
                throw fail("Exception should be passed into onNext(), " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                assertEquals("Emission count not 1", 1, count);
            }
        };

        TestSubscriber<PartnerResult> subscriberSpy = spy(subscriber);
        mClient.mockUnpublishFailure(mException);
        mEmitter.getPartnerFlowable().subscribe(subscriberSpy);
        subscriberSpy.onComplete();
        subscriberSpy.dispose();

        verify(subscriberSpy, times(1)).onNext(any(PartnerResult.class));

        // Exception should be passed to RxJava default error handler
        // since the onComplete() call triggers cancel() on the subscriber
        // which then triggers unpublish() in the client and since the
        // subcriber is canceled, this is the only route to go
        assertTrue("Exception not passed to RxJava default error handler", mDefaultErrorHandler.hasPassedThrough());
    }

    @After
    public void tearDown() throws Exception {
        PartnerNearbyMessagesClient.destroy();
        mClient.close();
        mClient = null;
        mEmitter = null;
        mSettings = null;
    }
}
