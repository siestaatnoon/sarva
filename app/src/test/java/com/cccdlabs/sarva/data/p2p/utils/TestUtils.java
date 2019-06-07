package com.cccdlabs.sarva.data.p2p.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.cccdlabs.sarva.data.p2p.nearby.utils.NearbyUtils;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryException;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Utility class for Nearby class testing.
 */
public final class TestUtils {

    static class TestAssertPartnerResultSubscriber extends TestSubscriber<PartnerResult> {

        final private List<Partner> partners;
        final private int expectedCount;
        private int count = 0;

        public TestAssertPartnerResultSubscriber(List<Partner> partners) {
            this.partners = partners;
            expectedCount = partners.size();
        }

        @Override
        public void onNext(PartnerResult partnerResult) {
            String objName = "PartnerResult[" + count + "]";
            Partner expected = partners.get(count);
            Partner result = partnerResult.getPartner();
            TestUtils.assertEqualPartners(objName, expected, result);
            count++;
        }

        @Override
        public void onComplete() {
            assertEquals("Emission count not " + expectedCount, expectedCount, count);
        }

        @Override
        public void onError(Throwable throwable) {
            fail("Unexpected exception thrown: " + throwable.toString());
        }
    }

    public static void assertEqualPartners(String objectName, Partner expected, Partner result) {
        assertEqualPartners(objectName, expected, result, true);
    }

    public static void assertEqualPartners(String objectName, Partner expected, Partner result, boolean onlyNonNull) {
        if (objectName == null || objectName.equals("")) {
            objectName = "Partner";
        }

        if (onlyNonNull) {
            assertNotNull(objectName + "[expected] null", expected);
            assertNotNull(objectName + "[result] null", result);
        } else if (expected == null) {
            assertNull(objectName + "[result] not null", result);
        } else if (result == null) {
            fail(objectName + "[result] null");
        }

        double delta = 0.001;
        assertEquals(objectName + " UUID not equal", expected.getUuid(), result.getUuid());
        assertEquals(objectName + " username not equal", expected.getUsername(), result.getUsername());
        assertEquals(objectName + " device name not equal", expected.getDeviceName(), result.getDeviceName());
        assertEquals(objectName + " accuracy not equal", expected.getAccuracy(), result.getAccuracy());
        assertEquals(objectName + " distance not equal", expected.getDistance(), result.getDistance(), delta);
        assertEquals(objectName + " RSSI not equal", expected.getRssi(), result.getRssi());
        assertEquals(objectName + " TX power not equal", expected.getTxPower(), result.getTxPower());
        assertEquals(objectName + " isActive not equal", expected.isActive(), result.isActive());
    }

    public static TestSubscriber<PartnerResult> getTestAssertPartnerResultSubscriber(List<Partner> partners) {
        if (partners == null) {
            throw new IllegalArgumentException("partners parameter null");
        }

        return new TestAssertPartnerResultSubscriber(partners);
    }

    public static List<Partner> seedAndConvertToPartners(@NonNull PartnerRepository repository, List<Message> messages,
            List<Distance> distances, List<BleSignal> signals, boolean isPartnerActive) throws RepositoryException {
        List<Partner> partners = new ArrayList<>();
        if (messages == null) {
            return null;
        } else if (messages.size() == 0) {
            return partners;
        }

        if (distances == null) {
            distances = new ArrayList<>();
        }
        if (signals == null) {
            signals = new ArrayList<>();
        }

        int distanceCount = distances.size();
        int signalCount = signals.size();
        for (int i=0; i<messages.size(); i++) {
            Message message = messages.get(i);
            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setActive(isPartnerActive);
            int id = repository.insert(partner);
            partner.setId(id);

            int index;
            if (distanceCount > 0) {
                index = i < distanceCount ? i : distanceCount - 1;
                Distance distance = distances.get(index);
                partner.setDistance(distance.getMeters());
                partner.setAccuracy(distance.getAccuracy());
            }

            if (signalCount > 0) {
                index = i < signalCount ? i : signalCount - 1;
                BleSignal signal = signals.get(index);
                partner.setRssi(signal.getRssi());
                partner.setTxPower(signal.getTxPower());
            }

            partners.add(partner);
        }

        return partners;
    }
}
