package com.cccdlabs.sarva.data.p2p.nearby.client;

import android.content.Context;

import com.cccdlabs.sarva.data.p2p.base.MockMessagesClient;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.google.android.gms.nearby.messages.StatusCallback;

public class MockPartnerNearbyMessagesClient {

    public static PartnerNearbyMessagesClient get(Context context, MockMessagesClient mockMessagesClient,
            boolean hasPublish, boolean hasSubscribe, PartnerMessage.Mode publishMode) {
        if (mockMessagesClient == null) {
            mockMessagesClient = new MockMessagesClient(context, true);
        }

        PartnerNearbyMessagesClient client = new PartnerNearbyMessagesClient.Builder(context)
                .hasPublish(hasPublish)
                .hasSubscribe(hasSubscribe)
                .setPublishMode(publishMode)
                .setMessagesClient(mockMessagesClient)
                .setDebug(true)
                .build();

        StatusCallback statusCallback = client.new PartnerStatusCallback();
        mockMessagesClient.registerStatusCallback(statusCallback);
        return client;
    }

    public static PartnerNearbyMessagesClient get(Context context, boolean hasPublish,
            boolean hasSubscribe, PartnerMessage.Mode publishMode) {
        return get(context, null, hasPublish, hasSubscribe, publishMode);
    }
}
