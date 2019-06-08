package com.cccdlabs.sarva.data.p2p.nearby;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.cccdlabs.sarva.data.p2p.base.MockMessagesClient;
import com.cccdlabs.sarva.data.p2p.nearby.exception.PermissionException;
import com.cccdlabs.sarva.data.p2p.nearby.exception.PublishExpiredException;
import com.cccdlabs.sarva.data.p2p.nearby.utils.NearbyUtils;
import com.cccdlabs.sarva.data.settings.GeneralSettingsManager;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessagesClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class NearbyPartnerBroadcastTest {

    private static final String OBJECT_NAME = "NearbyPartnerBroadcast";
    private static final String TEST_UUID = "9d29e5e4-3d33-4f90-9453-5b3e8af963ed";
    private static final String TEST_USERNAME = "Johnny";

    private MockMessagesClient mClient;
    private NearbyPartnerBroadcast mNearby;
    private GeneralSettingsManager mSettings;

    class MockNearbyPartnerBroadcast extends NearbyPartnerBroadcast {

        MockNearbyPartnerBroadcast(Context context) {
            super(context);
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

        // Since we won't mock the settings, set the UUID and username
        mSettings = new GeneralSettingsManager(context);
        mSettings.setUuid(TEST_UUID);
        mSettings.setUsername(TEST_USERNAME);

        mClient = new MockMessagesClient(context, true);
        mNearby = new MockNearbyPartnerBroadcast(context);
    }

    @Test
    public void testDoesNotSubscribe() throws Throwable {
        assertFalse("Nearby client not subscribing", mClient.isSubscribing());
        assertFalse(OBJECT_NAME + " not subscribing", mNearby.isSubscribing());
    }

    @Test
    public void testIsPublishingAndSendingMessage() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mNearby.getPartnerEmitter().test(); // subscribing triggers publishing
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
    public void testPauseStopsPubSubAndResumeStartsPubSubAgain() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mNearby.getPartnerEmitter().test();

        // First make sure is publishing BUT NOT subscribing
        assertTrue("Nearby client is not publishing before pauseEmitter()", mClient.isPublishing());
        assertTrue(OBJECT_NAME + " is not publishing before pauseEmitter()", mNearby.isPublishing());
        assertFalse("Nearby client is subscribing before pauseEmitter()", mClient.isSubscribing());
        assertFalse(OBJECT_NAME + " is subscribing before pauseEmitter()", mNearby.isSubscribing());

        mNearby.pauseEmitter();

        assertFalse("Nearby client is publishing after pauseEmitter()", mClient.isPublishing());
        assertFalse(OBJECT_NAME + " is publishing after pauseEmitter()", mNearby.isPublishing());
        assertFalse("Nearby client is subscribing after pauseEmitter()", mClient.isSubscribing());
        assertFalse(OBJECT_NAME + " is subscribing after pauseEmitter()", mNearby.isSubscribing());

        mNearby.resumeEmitter();

        // Make sure is publishing again BUT STILL NOT subscribing
        assertTrue("Nearby client is not publishing after resumeEmitter()", mClient.isPublishing());
        assertTrue(OBJECT_NAME + " is not publishing after resumeEmitter()", mNearby.isPublishing());
        assertFalse("Nearby client is subscribing after resumeEmitter()", mClient.isSubscribing());
        assertFalse(OBJECT_NAME + " is subscribing after resumeEmitter()", mNearby.isSubscribing());

        subscriber.assertNoErrors();
        subscriber.dispose();
    }

    @Test
    public void testOnPermissionChangedFalseStopsPublishing() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mNearby.getPartnerEmitter().test();
        mClient.mockStatusCallbackOnPermissionChanged(false);

        subscriber.assertError(PermissionException.class);
        assertFalse("Nearby client is publishing", mClient.isPublishing());
        assertFalse(OBJECT_NAME + " is publishing", mNearby.isPublishing());

        subscriber.dispose();
    }

    @Test
    public void testOnPermissionChangedTrueDoesNothing() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mNearby.getPartnerEmitter().test();
        mClient.mockStatusCallbackOnPermissionChanged(true);

        subscriber.assertNoErrors();
        assertTrue("Nearby client is not publishing", mClient.isPublishing());
        assertTrue(OBJECT_NAME + " is not publishing", mNearby.isPublishing());

        subscriber.dispose();
    }

    @Test
    public void testOnPublishExpiredStopsPublishing() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mNearby.getPartnerEmitter().test();
        mClient.mockPublishExpired();

        subscriber.assertError(PublishExpiredException.class);
        assertFalse("Nearby client is publishing", mClient.isPublishing());
        assertFalse(OBJECT_NAME + " is publishing", mNearby.isPublishing());

        subscriber.dispose();
    }

    @Test
    public void testSubscriberCancelStopsPublishing() throws Throwable {
        TestSubscriber<PartnerResult> subscriber = mNearby.getPartnerEmitter().test();
        subscriber.cancel();

        assertTrue("Subscriber not canceled", subscriber.isCancelled());
        assertFalse("Nearby client is publishing", mClient.isPublishing());
        assertFalse(OBJECT_NAME + " is publishing", mNearby.isPublishing());

        subscriber.dispose();
    }

    @After
    public void tearDown() throws Exception {
        mClient.close();
        mClient = null;
        mNearby = null;
        mSettings = null;
    }
}
