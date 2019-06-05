package com.cccdlabs.sarva.data.p2p.base;

import com.google.android.gms.nearby.messages.Distance;

import androidx.annotation.NonNull;

public class MockDistance implements Distance {

    private double meters;
    private int accuracy;

    public MockDistance(double meters, int accuracy) {
        this.meters = meters;
        this.accuracy = accuracy;
    }

    @Override
    public int getAccuracy() {
        return accuracy;
    }

    @Override
    public double getMeters() {
        return meters;
    }

    @Override
    public int compareTo(@NonNull Distance distance) {
        return (int) Math.round(meters - distance.getMeters());
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[").append("meters: ").append(meters).append(", ");
        buffer.append("accuracy: ").append(accuracy).append("]");
        return buffer.toString();
    }
}
