package com.oscarrrweb.sarva.data.p2p.nearby;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.oscarrrweb.sarva.data.p2p.nearby.base.AbstractNearbyPartnerEmitter;
import com.oscarrrweb.sarva.data.p2p.nearby.utils.NearbyUtils;
import com.oscarrrweb.sarva.data.repository.partners.PartnerRepository;
import com.oscarrrweb.sarva.domain.model.partners.Partner;
import com.oscarrrweb.sarva.domain.model.partners.PartnerMessage;
import com.oscarrrweb.sarva.domain.model.partners.PartnerResult;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.FlowableEmitter;

public class NearbyPartnerTransmitter extends AbstractNearbyPartnerEmitter {

    private List<String> mPartners;

    class PartnerTransceiverMessageListener extends PartnerMessageListener {

        PartnerTransceiverMessageListener(FlowableEmitter<PartnerResult> emitter) {
            super(emitter, PartnerMessage.Mode.SEARCH);
        }

        @Override
        public void onFound(Message message) {
            if (checkInvalidState(message)) {
                // error already emitted in
                // Observable so exit
                return;
            }

            if (mPartners.size() == 0) {
                // Another device within range, begin publishing
                publish(PartnerMessage.Mode.PING);
            }

            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setActive(true); // Set the partner to true since within range
            trackPartner(partner, false);
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
            trackPartner(partner, true);
            if (mPartners.size() == 0) {
                // if no partners within range, unpublish
                unpublish();
            }

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
            trackPartner(partner, false);
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
            trackPartner(partner, false);
            emitter.onNext(new PartnerResult(partner));
        }
    }


    public NearbyPartnerTransmitter(@NonNull Activity activity, @NonNull PartnerRepository repository) {
        super(activity, repository);
        mPartners = new ArrayList<>();
    }

    public NearbyPartnerTransmitter(@NonNull Context context, @NonNull PartnerRepository repository) {
        super(context, repository);
        mPartners = new ArrayList<>();
    }

    @Override
    protected MessageListener getMessageListener(FlowableEmitter<PartnerResult> emitter) {
        return messageListener == null
            ? new PartnerTransceiverMessageListener(emitter)
            : messageListener;
    }

    @Override
    protected void cleanUp() {
        super.cleanUp();
        mPartners = null;
    }

    protected void trackPartner(Partner partner, boolean isLost) {
        if (partner == null) {
            return;
        }

        String uuid = partner.getUuid();
        if (isLost) {
            if (uuid != null) {
                mPartners.remove(uuid);
            }
        } else if (!mPartners.contains(uuid)) {
            mPartners.add(uuid);
        }
    }
}
