package com.cccdlabs.sarva.data.p2p.nearby;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.cccdlabs.sarva.data.p2p.base.MockMessagesClient;
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class NearbyPartnerTransmitterTest {

    private static final String OBJECT_NAME = "NearbyPartnerTransmitter";
    private static final String TEST_UUID = "9d29e5e4-3d33-4f90-9453-5b3e8af963ed";
    private static final String TEST_USERNAME = "Johnny";
    private static final int TEST_MESSAGE_COUNT = 5;

    private MockMessagesClient mClient;
    private NearbyPartnerTransmitter mNearby;
    private PartnerRepository mRepository;
    private GeneralSettingsManager mSettings;

    class MockNearbyPartnerTransmitter extends NearbyPartnerTransmitter {

        MockNearbyPartnerTransmitter(Context context, PartnerRepository repository) {
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
        mNearby = new MockNearbyPartnerTransmitter(context, mRepository);
    }

    @Test
    public void testIsNotPublishingAfterInitializing() throws Throwable {
        Message message = mClient.capturePublishMessage();

        assertNull("Message not null", message);
        assertFalse("Nearby client publishing", mClient.isPublishing());
        assertFalse(OBJECT_NAME + " publishing", mNearby.isPublishing());
    }

    @Test
    public void testIsPublishingAfterFirstOnFound() throws Throwable {
        List<Message> list = TestData.generateMessages(PartnerMessage.Mode.SEARCH);
        final List<Message> messages = list.subList(0, 1); // only test first onFound() call
        final List<Partner> partners = TestUtils.seedAndConvertToPartners(
                mRepository,
                messages,
                null,
                null,
                true // DB items are set to active prior to onFound() call
        );
        final int size = partners.size();

        TestSubscriber<PartnerResult> subscriberSpy = TestUtils.getTestPartnerSubscriber(partners);
        subscriberSpy = spy(subscriberSpy);
        mNearby.getPartnerEmitter().subscribe(subscriberSpy);

        mClient.mockMessageOnFound(messages);
        Message message = mClient.capturePublishMessage();
        PartnerMessage partnerMessage = NearbyUtils.toPartnerMessage(message);

        // need to test just one message received
        assertEquals("Number of messages emitted not one", 1, size);
        verify(subscriberSpy, times(size)).onNext(any(PartnerResult.class));

        assertTrue("Nearby client not publishing", mClient.isPublishing());
        assertTrue(OBJECT_NAME + " not publishing", mNearby.isPublishing());
        assertEquals("UUID not equal", TEST_UUID, partnerMessage.getUuid());
        assertEquals("Username not equal", TEST_USERNAME, partnerMessage.getUsername());
        assertEquals("Device name not equal", NearbyUtils.getDeviceName(), partnerMessage.getDeviceName());
        assertEquals("Mode not equal", PartnerMessage.Mode.PING, partnerMessage.getMode());

        subscriberSpy.onComplete();
        subscriberSpy.dispose();
    }

    @Test
    public void testIsSubscribingWithOnFound() throws Throwable {
        final List<Message> messages = TestData.generateMessages(PartnerMessage.Mode.SEARCH);
        final List<Partner> partners = TestUtils.seedAndConvertToPartners(
                mRepository,
                messages,
                null,
                null,
                true // DB items are set to active prior to onFound() call
        );
        final int size = partners.size();

        TestSubscriber<PartnerResult> subscriberSpy = TestUtils.getTestPartnerSubscriber(partners);
        subscriberSpy = spy(subscriberSpy);
        mNearby.getPartnerEmitter().subscribe(subscriberSpy);

        assertTrue("Nearby client not subscribing", mClient.isSubscribing());
        assertTrue(OBJECT_NAME + " not subscribing", mNearby.isSubscribing());

        mClient.mockMessageOnFound(messages);
        List<Partner> items = mRepository.getAll();

        // need to test at least TEST_MESSAGE_COUNT (5) messages received
        assertTrue("Number of messages emitted less than " + TEST_MESSAGE_COUNT, size >= TEST_MESSAGE_COUNT);
        assertEquals("Database count not message count", size, items.size());
        verify(subscriberSpy, times(size)).onNext(any(PartnerResult.class));

        subscriberSpy.onComplete();
        subscriberSpy.dispose();
    }

    @Test
    public void testIsSubscribingWithOnDistanceChanged() throws Throwable {
        final List<Message> messages = TestData.generateMessages(PartnerMessage.Mode.SEARCH);
        final List<Distance> distances = TestData.generateDistances();
        final List<Partner> partners = TestUtils.seedAndConvertToPartners(
                mRepository,
                messages,
                distances,
                null,
                true // DB items are set to active prior to onFound() call
        );
        final int size = partners.size();

        TestSubscriber<PartnerResult> subscriberSpy = TestUtils.getTestPartnerSubscriber(partners);
        subscriberSpy = spy(subscriberSpy);
        mNearby.getPartnerEmitter().subscribe(subscriberSpy);

        assertTrue("Nearby client not subscribing", mClient.isSubscribing());
        assertTrue(OBJECT_NAME + " not subscribing", mNearby.isSubscribing());

        mClient.mockMessageOnDistanceChanged(messages, distances);
        List<Partner> items = mRepository.getAll();

        // need to test at least TEST_MESSAGE_COUNT (5) messages received
        assertTrue("Number of messages emitted less than " + TEST_MESSAGE_COUNT, size >= TEST_MESSAGE_COUNT);
        assertEquals("Database count not message count", size, items.size());
        verify(subscriberSpy, times(size)).onNext(any(PartnerResult.class));

        subscriberSpy.onComplete();
        subscriberSpy.dispose();
    }

    @Test
    public void testIsSubscribingWithOnBleSignalChanged() throws Throwable {
        final List<Message> messages = TestData.generateMessages(PartnerMessage.Mode.SEARCH);
        final List<BleSignal> signals = TestData.generateBleSignals();
        final List<Partner> partners = TestUtils.seedAndConvertToPartners(
                mRepository,
                messages,
                null,
                signals,
                true // DB items are set to active prior to onFound() call
        );
        final int size = partners.size();

        TestSubscriber<PartnerResult> subscriberSpy = TestUtils.getTestPartnerSubscriber(partners);
        subscriberSpy = spy(subscriberSpy);
        mNearby.getPartnerEmitter().subscribe(subscriberSpy);

        assertTrue("Nearby client not subscribing", mClient.isSubscribing());
        assertTrue(OBJECT_NAME + " not subscribing", mNearby.isSubscribing());

        mClient.mockMessageOnBleSignalChanged(messages, signals);
        List<Partner> items = mRepository.getAll();

        // need to test at least TEST_MESSAGE_COUNT (5) messages received
        assertTrue("Number of messages emitted less than " + TEST_MESSAGE_COUNT, size >= TEST_MESSAGE_COUNT);
        assertEquals("Database count not message count", size, items.size());
        verify(subscriberSpy, times(size)).onNext(any(PartnerResult.class));

        subscriberSpy.onComplete();
        subscriberSpy.dispose();
    }

    @Test
    public void testSubscribesWithOnLostAndStopsPublishingOnLastCall() throws Throwable {
        final List<Message> messages = TestData.generateMessages(PartnerMessage.Mode.SEARCH);
        final List<Partner> partners = TestUtils.seedAndConvertToPartners(
                mRepository,
                messages,
                null,
                null,
                true // DB items are set to active prior to onLost() call
        );
        final int size = partners.size();

        // need to set isActive=false for each Partner
        // since that is the result after onLost() call
        for (Partner partner : partners) {
            partner.setActive(false);
        }

        TestSubscriber<PartnerResult> subscriberSpy = TestUtils.getTestPartnerSubscriber(partners);
        subscriberSpy = spy(subscriberSpy);
        mNearby.getPartnerEmitter().subscribe(subscriberSpy);

        assertTrue("Nearby client not subscribing", mClient.isSubscribing());
        assertTrue(OBJECT_NAME + " not subscribing", mNearby.isSubscribing());

        mClient.mockMessageOnLost(messages);
        Message message = mClient.capturePublishMessage();
        List<Partner> items = mRepository.getAll();

        // need to test at least TEST_MESSAGE_COUNT (5) messages received
        assertTrue("Number of messages emitted less than " + TEST_MESSAGE_COUNT, size >= TEST_MESSAGE_COUNT);
        assertEquals("Database count not message count", size, items.size());

        // assert not publishing after last onLost() call
        assertNull("Message not null", message);
        assertFalse("Nearby client publishing", mClient.isPublishing());
        assertFalse(OBJECT_NAME + " publishing", mNearby.isPublishing());

        verify(subscriberSpy, times(size)).onNext(any(PartnerResult.class));

        subscriberSpy.onComplete();
        subscriberSpy.dispose();
    }

    @Test
    public void testOnPublishExpiredStopsPubSub() throws Throwable {
        // First need to trigger onFound() to initialize publishing
        List<Message> list = TestData.generateMessages(PartnerMessage.Mode.SEARCH);
        final List<Message> messages = list.subList(0, 1); // only test first onFound() call
        final List<Partner> partners = TestUtils.seedAndConvertToPartners(
                mRepository,
                messages,
                null,
                null,
                true // DB items are set to active prior to onFound() call
        );
        final int size = partners.size();

        TestSubscriber<PartnerResult> subscriberSpy = TestUtils.getTestPartnerSubscriber(partners);
        subscriberSpy = spy(subscriberSpy);
        mNearby.getPartnerEmitter().subscribe(subscriberSpy);
        mClient.mockMessageOnFound(messages);
        mClient.mockPublishExpired();

        // need to test just one message received
        assertEquals("Number of messages emitted not one", 1, size);
        verify(subscriberSpy, times(size)).onNext(any(PartnerResult.class));
        verify(subscriberSpy, times(1)).onError(any(PublishExpiredException.class));
        assertFalse("Nearby client is publishing", mClient.isPublishing());
        assertFalse("Nearby client is subscribing", mClient.isSubscribing());
        assertFalse(OBJECT_NAME + " is publishing", mNearby.isPublishing());
        assertFalse(OBJECT_NAME + " is subscribing", mNearby.isSubscribing());

        subscriberSpy.onComplete();
        subscriberSpy.dispose();
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
                fail("Exception should be passed into onNext(), " + throwable.getMessage());
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
                fail("Exception should be passed into onNext(), " + throwable.getMessage());
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
                fail("Exception should be passed into onError()");
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
