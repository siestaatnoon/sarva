package com.oscarrrweb.sarva.data.p2p.base;

import com.google.android.gms.nearby.messages.BleSignal;

public class MockBleSignal implements BleSignal {

    private int rssi;
    private int txPower;

    public MockBleSignal(int rssi, int txPower) {
        this.rssi = rssi;
        this.txPower = txPower;
    }

    @Override
    public int getRssi() {
        return rssi;
    }

    @Override
    public int getTxPower() {
        return txPower;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[").append("rssi: ").append(rssi).append(", ");
        buffer.append("txPower: ").append(txPower).append("]");
        return buffer.toString();
    }
}
