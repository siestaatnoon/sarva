package com.cccdlabs.sarva.data.p2p.nearby;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.cccdlabs.sarva.data.p2p.nearby.base.AbstractNearbyPartnerEmitter;
import com.cccdlabs.sarva.data.p2p.nearby.utils.NearbyUtils;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;

import io.reactivex.FlowableEmitter;

public class NearbyPartnerSearch extends AbstractNearbyPartnerEmitter {

    class PartnerSearchMessageListener extends PartnerMessageListener {

        PartnerSearchMessageListener(FlowableEmitter<PartnerResult> emitter) {
            super(emitter, PartnerMessage.Mode.PING, true);
        }

        @Override
        public void onFound(Message message) {
            if (checkInvalidState(message)) {
                // error already emitted in
                // Observable so exit
                return;
            }

            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setActive(true); // Set the partner to true since within range
            emitter.onNext(new PartnerResult(partner));
        }

        @Override
        public void onLost(Message message) {
            if (checkInvalidState(message)) {
                // error already emitted in
                // Observable so exit
                return;
            }

            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setActive(false); // Set the partner to false since out of range
            emitter.onNext(new PartnerResult(partner));
        }

        @Override
        public void onDistanceChanged(Message message, Distance distance) {
            if (checkInvalidState(message)) {
                // error already emitted in
                // Observable so exit
                return;
            }

            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setDistance(distance.getMeters());
            partner.setAccuracy(distance.getAccuracy());
            partner.setActive(true); // Set the partner to true since within range
            emitter.onNext(new PartnerResult(partner));
        }

        @Override
        public void onBleSignalChanged(Message message, BleSignal bleSignal) {
            if (checkInvalidState(message)) {
                // error already emitted in
                // Observable so exit
                return;
            }

            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setRssi(bleSignal.getRssi());
            partner.setTxPower(bleSignal.getTxPower());
            partner.setActive(true); // Set the partner to true since within range
            emitter.onNext(new PartnerResult(partner));
        }
    }


    public NearbyPartnerSearch(@NonNull Activity activity, @NonNull PartnerRepository repository) {
        super(activity, repository);
        publish();
    }

    public NearbyPartnerSearch(@NonNull Context context, @NonNull PartnerRepository repository) {
        super(context, repository);
        publish();
    }

    @Override
    protected MessageListener getMessageListener(FlowableEmitter<PartnerResult> emitter) {
        return messageListener == null
            ? new PartnerSearchMessageListener(emitter)
            : messageListener;
    }

    @Override
    protected PartnerMessage.Mode getPublishMode() {
        return PartnerMessage.Mode.SEARCH;
    }
}
