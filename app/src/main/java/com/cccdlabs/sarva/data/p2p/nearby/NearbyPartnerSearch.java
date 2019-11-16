package com.cccdlabs.sarva.data.p2p.nearby;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.p2p.nearby.base.AbstractNearbyPartnerEmitter;
import com.cccdlabs.sarva.data.p2p.nearby.utils.NearbyUtils;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import io.reactivex.FlowableEmitter;

/**
 * Nearby Messages implementation to search, or receive messages, from other partner devices
 * within proximity, receiving approximate distance and signal strength for devices found. Also
 * publishes to other "found" devices, emitting approximate distance and signal strength .
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class NearbyPartnerSearch extends AbstractNearbyPartnerEmitter {

    /**
     * A {@link MessageListener} that subscribes to Partner {@link Message}s (from other device
     * users saved on this device) for partners deemed "lost" and are being searched. The
     * approximate distance and signal strength for devices "found" are updated and passed to the
     * RxJava {@link FlowableEmitter} for use in the application.
     * <p>
     * Note that emitting flag of the <code>Partner</code> is set to true when the user device
     * is discovered by <code>onFound()</code> and is set to false when user device communication
     * is lost from <code>onLost()</code>.
     */
    class PartnerSearchMessageListener extends PartnerMessageListener {

        /**
         * Constructor.
         *
         * @param emitter The RxJava {@link FlowableEmitter} to emit partner data to subscribers
         *                in the application
         */
        PartnerSearchMessageListener(FlowableEmitter<PartnerResult> emitter) {
            super(emitter, PartnerMessage.Mode.PING, true);
        }

        /**
         * Receives a message from a "lost" partner and emits the approximate distance and signal
         * strength.
         * <p>
         * Creates a {@link Partner} object from the user, sets the emitting flag to true and emits
         * it through a {@link PartnerResult}.
         * <p>
         * Errors occurring within this call are passed via a <code>PartnerResult</code> and emitted
         * keeping the subscription flow active.
         *
         * @param message The Nearby message received from a user device
         */
        @Override
        public void onFound(Message message) {
            if (checkInvalidState(message)) {
                // error already emitted in
                // Observable so exit
                return;
            }

            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setEmitting(true); // Set the partner to true since within range
            emitter.onNext(new PartnerResult(partner));
        }

        /**
         * Creates a {@link Partner} object for the user, sets the emitting flag to false and emits
         * it through a {@link PartnerResult}. Partner device no longer within range.
         * <p>
         * Errors occurring within this call are passed via a <code>PartnerResult</code> and emitted
         * keeping the subscription flow active.
         *
         * @param message The Nearby message received from a user device
         */
        @Override
        public void onLost(Message message) {
            if (checkInvalidState(message)) {
                // error already emitted in
                // Observable so exit
                return;
            }

            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setEmitting(false); // Set the partner to false since out of range
            emitter.onNext(new PartnerResult(partner));
        }

        /**
         * Emits a partner result when the distance between a remote partner and this device changes.
         * <p>
         * Creates a {@link Partner} object from the user, sets the emitting flag to true and emits
         * it through a {@link PartnerResult}.
         * <p>
         * Errors occurring within this call are passed via a <code>PartnerResult</code> and emitted
         * keeping the subscription flow active.
         *
         * @param message   The Nearby message received from a user device
         * @param distance  The {@link Distance} object containing approximate distance in
         *                  meters and accuracy of measurement
         */
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
            partner.setEmitting(true); // Set the partner to true since within range
            emitter.onNext(new PartnerResult(partner));
        }

        /**
         * Emits a partner result when the Bluetooth BLE signal between a remote partner and this
         * device changes.
         * <p>
         * Creates a {@link Partner} object from the user, sets the emitting flag to true and emits
         * it through a {@link PartnerResult}.
         * <p>
         * Errors occurring within this call are passed via a <code>PartnerResult</code> and emitted
         * keeping the subscription flow active.
         *
         * @param message   The Nearby message received from a user device
         * @param bleSignal The {@link BleSignal} object containing RSSI value and TX power from
         *                  remote user device
         */
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
            partner.setEmitting(true); // Set the partner to true since within range
            emitter.onNext(new PartnerResult(partner));
        }
    }


    /**
     * Constructor. When passed in an {@link Activity}, notifications from the Nearby library will be
     * prompted from it for resolution of resolvable connection errors.
     * <p>
     * Note that <code>super.publish()</code> is called here to start publishing.
     *
     * @param activity      The Activity utilizing the Nearby Messaging
     * @param repository    The PartnerRepository for database functions
     */
    public NearbyPartnerSearch(@NonNull Activity activity, @NonNull PartnerRepository repository) {
        super(activity, repository);
    }

    /**
     * Constructor. When passed in an {@link Context}, notifications from the Nearby library will
     * be through a system notification for resolution of resolvable connection errors.
     * <p>
     * Note that <code>super.publish()</code> is called here to start publishing.
     *
     * @param context       The Android context utilizing the Nearby Messaging
     * @param repository    The PartnerRepository for database functions
     */
    public NearbyPartnerSearch(@NonNull Context context, @NonNull PartnerRepository repository) {
        super(context, repository);
    }

    /**
     * Returns the MessageListener handling Nearby {@link Message} received in this class. Acts
     * as a sort of singleton returning a new instance if one hasn't already been created or was
     * previously destroyed.
     *
     * @param emitter   The RxJava {@link FlowableEmitter} to emit data from messages received
     *                  from Nearby subscriptions
     * @return          The MessageListener
     */
    @Override
    protected MessageListener getMessageListener(FlowableEmitter<PartnerResult> emitter) {
        return messageListener == null
            ? new PartnerSearchMessageListener(emitter)
            : messageListener;
    }

    /**
     * Message type, or {@link PartnerMessage.Mode}, this class uses for Nearby publishing.
     *
     * @return  The Mode, published message type, <code>Mode.SEARCH</code>
     * @see     PartnerMessage.Mode
     */
    @Override
    protected PartnerMessage.Mode getPublishMode() {
        return PartnerMessage.Mode.SEARCH;
    }
}
