package com.cccdlabs.sarva.data.p2p.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.cccdlabs.sarva.data.p2p.base.MockBleSignal;
import com.cccdlabs.sarva.data.p2p.base.MockDistance;
import com.cccdlabs.sarva.data.p2p.nearby.utils.NearbyUtils;
import com.cccdlabs.sarva.data.utils.DateUtils;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public final class TestData {

    private static final int MAX_ACCURACY = 15;
    private static final int MAX_DISTANCE = 160;
    private static final int MAX_RSSI = 128;
    private static final int MAX_TX_POWER = 60;

    private static final String[] UUIDS = new String[]{
            "6198c443-7e68-4c8e-8052-8e8736111d5f",
            "37a5977a-4d54-43b9-9001-045271eeb36b",
            "481ffa66-a87d-4950-aae9-6643322d4081",
            "fc7d2829-8a03-4a6f-af58-b876bb7b0729",
            "4c3c09b9-e6e8-4f00-bf59-44ef79bff577"
    };

    private static final String[] USERNAMES = new String[]{
            "Johnny",
            "Ripper",
            "Ana",
            "Criado",
            "Pepino"
    };

    private static final String[] DEVICES = new String[]{
            "HTC One",
            "LG Bello",
            "Samsung J3",
            "Google Pixel 3A",
            "Realme 3 Pro"
    };

    public static List<Message> generateMessages(@NonNull PartnerMessage.Mode mode) {
        shuffle(UUIDS);
        shuffle(USERNAMES);
        shuffle(DEVICES);

        List<Message> messages = new ArrayList<>(UUIDS.length);
        long time = DateUtils.currentTimestamp().getTime();

        for (int i=0; i < UUIDS.length; i++) {
            PartnerMessage message = new PartnerMessage();
            message.setUuid(UUIDS[i]);
            message.setUsername(USERNAMES[i]);
            message.setDeviceName(DEVICES[i]);
            message.setMode(mode);
            message.setTime(new Date(time + (i * 1000)));
            messages.add(NearbyUtils.toNearbyMessage(message));
        }

        return messages;
    }

    public static List<Distance> generateDistances() {
        int length = UUIDS.length;
        List<Distance> distances = new ArrayList<>(length);
        Random random = new Random();

        // we'll assume accuracy to be constant
        int accuracy = random.nextInt(MAX_ACCURACY);

        for (int i=0; i < length; i++) {
            double meters = MAX_DISTANCE * random.nextDouble();
            MockDistance distance = new MockDistance(meters, accuracy);
            distances.add(distance);
        }

        return distances;
    }

    public static List<BleSignal> generateBleSignals() {
        int length = UUIDS.length;
        List<BleSignal> signals = new ArrayList<>(length);
        Random random = new Random();

        // we'll assume TX power to be constant
        int txPower = -(random.nextInt(MAX_TX_POWER));

        for (int i=0; i < length; i++) {
            int rssi = -(random.nextInt(MAX_RSSI));
            MockBleSignal signal = new MockBleSignal(rssi, txPower);
            signals.add(signal);
        }

        return signals;
    }

    private static void shuffle(String[] arr) {
        if (arr == null) {
            return;
        }

        int index;
        String tmp;
        Random random = new Random();
        for (int i=arr.length-1; i > 0; i--) {
            index = random.nextInt(i + 1);
            tmp = arr[index];
            arr[index] = arr[i];
            arr[i] = tmp;
        }
    }

    public static void main(String[] args) {
        List<Message> items = TestData.generateMessages(PartnerMessage.Mode.CHECK);
        System.out.println(items);
        items = TestData.generateMessages(PartnerMessage.Mode.CHECK);
        System.out.println(items);
    }
}
