package com.cccdlabs.sarva.data.p2p.nearby;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.cccdlabs.sarva.data.p2p.base.MockMessagesClient;
import com.cccdlabs.sarva.data.p2p.nearby.exception.PermissionException;
import com.cccdlabs.sarva.data.p2p.nearby.exception.PublishExpiredException;
import com.cccdlabs.sarva.data.p2p.nearby.exception.SubscribeExpiredException;
import com.cccdlabs.sarva.data.p2p.nearby.utils.NearbyUtils;
import com.cccdlabs.sarva.data.p2p.utils.TestData;
import com.cccdlabs.sarva.data.p2p.utils.TestUtils;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.data.settings.GeneralSettingsManager;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.domain.p2p.exception.InvalidPartnerException;
import com.cccdlabs.sarva.domain.p2p.exception.PartnerException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryQueryException;
import com.cccdlabs.sarva.presentation.di.components.DaggerTestDataComponent;
import com.cccdlabs.sarva.presentation.di.components.TestDataComponent;
import com.cccdlabs.sarva.presentation.di.modules.TestDataModule;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessagesClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class NearbyPartnerFindTest {

    private static final String OBJECT_NAME = "NearbyPartnerFind";
    private static final String TEST_UUID = "9d29e5e4-3d33-4f90-9453-5b3e8af963ed";
    private static final String TEST_USERNAME = "Johnny";
    private static final int TEST_MESSAGE_COUNT = 5;

    private MockMessagesClient mClient;
    private NearbyPartnerFind mNearby;
    private PartnerRepository mRepository;
    private GeneralSettingsManager mSettings;

    class MockNearbyPartnerFind extends NearbyPartnerFind {

        MockNearbyPartnerFind(Context context, PartnerRepository repository) {
            super(context, repository);
            this.context = context;
        }

        @Override
        protected MessagesClient getMessagesClient() {
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

        mRepository = dataComponent.partnerRepository();
        dataComponent.inject(mRepository);
        mClient = new MockMessagesClient(context, true);
        mNearby = new MockNearbyPartnerFind(context, mRepository);
    }

    @Test
    public void testIsPublishingAndSendingMessage() throws Throwable {
        Message message = mClient.capturePublishMessage();
        PartnerMessage partnerMessage = NearbyUtils.toPartnerMessage(message);

        assertTrue("Nearby client not publishing", mClient.isPublishing());
        assertTrue(OBJECT_NAME + " not publishing", mNearby.isPublishing());
        assertEquals("UUID not equal", TEST_UUID, partnerMessage.getUuid());
        assertEquals("Username not equal", TEST_USERNAME, partnerMessage.getUsername());
        assertEquals("Device name not equal", NearbyUtils.getDeviceName(), partnerMessage.getDeviceName());
        assertEquals("Mode not equal", PartnerMessage.Mode.PAIR, partnerMessage.getMode());
    }

    @Test
    public void testIsSubscribingWithOnFound() throws Throwable {
        final List<Message> messages = TestData.generateMessages(PartnerMessage.Mode.PAIR);
        final List<Partner> partners = TestUtils.seedAndConvertToPartners(
                mRepository,
                messages,
                null,
                null,
                false // DB items are set to inactive after onFound() call
        );
        final int size = partners.size();

        // need to set isEmitting=true for each Partner
        // since that is the result after onFound() call
        for (Partner partner : partners) {
            partner.setEmitting(true);
        }

        TestSubscriber<PartnerResult> subscriberSpy = TestUtils.getTestAssertPartnerResultSubscriber(partners);
        subscriberSpy = spy(subscriberSpy);
        mNearby.getPartnerEmitter().subscribe(subscriberSpy);

        assertTrue("Nearby client not subscribing", mClient.isSubscribing());
        assertTrue(OBJECT_NAME + " not subscribing", mNearby.isSubscribing());

        mClient.mockMessageOnFound(messages);

        // need to test at least TEST_MESSAGE_COUNT (5) messages received
        assertTrue("Number of messages emitted less than " + TEST_MESSAGE_COUNT, size >= TEST_MESSAGE_COUNT);
        verify(subscriberSpy, times(size)).onNext(any(PartnerResult.class));

        List<Partner> items = mRepository.getAll();
        assertEquals("Database count not message count", size, items.size());

        subscriberSpy.onComplete();
        subscriberSpy.dispose();
    }

    @Test
    public void testPauseStopsPubSubAndResumeStartsPubSubAgain() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mNearby.getPartnerEmitter().test();

        // First make sure is publishing and subscribing
        assertTrue("Nearby client is not publishing before pauseEmitter()", mClient.isPublishing());
        assertTrue("Nearby client is not subscribing before pauseEmitter()", mClient.isSubscribing());
        assertTrue(OBJECT_NAME + " is not publishing before pauseEmitter()", mNearby.isPublishing());
        assertTrue(OBJECT_NAME + " is not subscribing before pauseEmitter()", mNearby.isSubscribing());

        mNearby.pauseEmitter();

        assertFalse("Nearby client is publishing after pauseEmitter()", mClient.isPublishing());
        assertFalse("Nearby client is subscribing after pauseEmitter()", mClient.isSubscribing());
        assertFalse(OBJECT_NAME + " is publishing after pauseEmitter()", mNearby.isPublishing());
        assertFalse(OBJECT_NAME + " is subscribing after pauseEmitter()", mNearby.isSubscribing());

        mNearby.resumeEmitter();

        assertTrue("Nearby client is not publishing after resumeEmitter()", mClient.isPublishing());
        assertTrue("Nearby client is not subscribing after resumeEmitter()", mClient.isSubscribing());
        assertTrue(OBJECT_NAME + " is not publishing after resumeEmitter()", mNearby.isPublishing());
        assertTrue(OBJECT_NAME + " is not subscribing after resumeEmitter()", mNearby.isSubscribing());

        subscriber.assertNoErrors();
        subscriber.dispose();
    }

    @Test
    public void testOnPermissionChangedFalseStopsPubSub() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mNearby.getPartnerEmitter().test();
        mClient.mockStatusCallbackOnPermissionChanged(false);

        subscriber.assertError(PermissionException.class);
        assertFalse("Nearby client is publishing", mClient.isPublishing());
        assertFalse("Nearby client is subscribing", mClient.isSubscribing());
        assertFalse(OBJECT_NAME + " is publishing", mNearby.isPublishing());
        assertFalse(OBJECT_NAME + " is subscribing", mNearby.isSubscribing());

        subscriber.dispose();
    }

    @Test
    public void testOnPermissionChangedTrueDoesNothing() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mNearby.getPartnerEmitter().test();
        mClient.mockStatusCallbackOnPermissionChanged(true);

        subscriber.assertNoErrors();
        assertTrue("Nearby client is not publishing", mClient.isPublishing());
        assertTrue("Nearby client is not subscribing", mClient.isSubscribing());
        assertTrue(OBJECT_NAME + " is not publishing", mNearby.isPublishing());
        assertTrue(OBJECT_NAME + " is not subscribing", mNearby.isSubscribing());

        subscriber.dispose();
    }

    @Test
    public void testOnPublishExpiredStopsPubSub() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mNearby.getPartnerEmitter().test();
        mClient.mockPublishExpired();

        subscriber.assertError(PublishExpiredException.class);
        assertFalse("Nearby client is publishing", mClient.isPublishing());
        assertFalse("Nearby client is subscribing", mClient.isSubscribing());
        assertFalse(OBJECT_NAME + " is publishing", mNearby.isPublishing());
        assertFalse(OBJECT_NAME + " is subscribing", mNearby.isSubscribing());

        subscriber.dispose();
    }

    @Test
    public void testOnSubscribeExpiredStopsPubSub() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mNearby.getPartnerEmitter().test();
        mClient.mockSubscribeExpired();

        subscriber.assertError(SubscribeExpiredException.class);
        assertFalse("Nearby client is publishing", mClient.isPublishing());
        assertFalse("Nearby client is subscribing", mClient.isSubscribing());
        assertFalse(OBJECT_NAME + " is publishing", mNearby.isPublishing());
        assertFalse(OBJECT_NAME + " is subscribing", mNearby.isSubscribing());

        subscriber.dispose();
    }

    @Test
    public void testSubscriberCancelStopsPubSub() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mNearby.getPartnerEmitter().test();
        subscriber.cancel();

        assertTrue("Subscriber not canceled", subscriber.isCancelled());
        assertFalse("Nearby client is publishing", mClient.isPublishing());
        assertFalse("Nearby client is subscribing", mClient.isSubscribing());
        assertFalse(OBJECT_NAME + " is publishing", mNearby.isPublishing());
        assertFalse(OBJECT_NAME + " is subscribing", mNearby.isSubscribing());

        subscriber.dispose();
    }

    @Test
    public void testThrownRepositoryExceptionEmitsPartnerResult() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = new TestSubscriber<PartnerResult>() {
            private int count = 0;

            @Override
            public void onNext(PartnerResult partnerResult) {
                assertNotNull("PartnerResult null", partnerResult);
                assertTrue("PartnerResult hasError() false", partnerResult.hasError());
                assertTrue(
                        "PartnerResult exception not instance of RepositoryException",
                        partnerResult.getException() instanceof RepositoryException
                );
                count++;
            }

            @Override
            public void onError(Throwable throwable) {
                throw fail("Exception should be passed into onNext(), " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                assertEquals("Emission count not one", 1, count);
            }
        };

        mNearby.getPartnerEmitter().subscribe(subscriber);
        List<Message> messages = new ArrayList<>(1);
        messages.add(new Message(new byte[]{}));
        mClient.mockMessageCallbackException(new RepositoryQueryException("Test error"), 0);
        mClient.mockMessageOnFound(messages);

        subscriber.assertSubscribed();
        subscriber.onComplete();
        subscriber.dispose();
    }

    @Test
    public void testThrownPartnerExceptionEmitsPartnerResult() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = new TestSubscriber<PartnerResult>() {
            private int count = 0;

            @Override
            public void onNext(PartnerResult partnerResult) {
                assertNotNull("PartnerResult null", partnerResult);
                assertTrue("PartnerResult hasError() false", partnerResult.hasError());
                assertTrue(
                        "PartnerResult exception not instance of PartnerException",
                        partnerResult.getException() instanceof PartnerException
                );
                count++;
            }

            @Override
            public void onError(Throwable throwable) {
                throw fail("Exception should be passed into onNext(), " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                assertEquals("Emission count not one", 1, count);
            }
        };

        mNearby.getPartnerEmitter().subscribe(subscriber);
        List<Message> messages = new ArrayList<>(1);
        messages.add(new Message(new byte[]{}));
        mClient.mockMessageCallbackException(new InvalidPartnerException("Test error"), 0);
        mClient.mockMessageOnFound(messages);

        subscriber.assertSubscribed();
        subscriber.onComplete();
        subscriber.dispose();
    }

    @Test
    public void testThrownExceptionEmitsFromOnError() throws Throwable {
        final String errorMessage = "TEST ERROR";
        TestSubscriber<PartnerResult> subscriber = new TestSubscriber<PartnerResult>() {
            private int count = 0;

            @Override
            public void onNext(PartnerResult partnerResult) {
                throw fail("Exception should be passed into onError()");
            }

            @Override
            public void onError(Throwable throwable) {
                assertNotNull("Throwable null", throwable);
                assertTrue("Throwable exception not instance of Exception", throwable instanceof Exception);
                assertEquals("Error message not [" + errorMessage + "]", errorMessage, throwable.getMessage());
            }
        };

        mNearby.getPartnerEmitter().subscribe(subscriber);
        List<Message> messages = new ArrayList<>(1);
        messages.add(new Message(new byte[]{}));
        mClient.mockMessageCallbackException(new Exception(errorMessage), 0);
        mClient.mockMessageOnFound(messages);

        subscriber.assertSubscribed();
        assertFalse("Nearby client is publishing", mClient.isPublishing());
        assertFalse("Nearby client is subscribing", mClient.isSubscribing());
        assertFalse(OBJECT_NAME + " is publishing", mNearby.isPublishing());
        assertFalse(OBJECT_NAME + " is subscribing", mNearby.isSubscribing());

        subscriber.dispose();
    }

    @After
    public void tearDown() throws Exception {
        mClient.close();
        mClient = null;
        mNearby = null;
        mSettings = null;
        mRepository = null;
    }
}
