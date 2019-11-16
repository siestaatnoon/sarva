package com.cccdlabs.sarva.data.p2p.nearby.client;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.p2p.nearby.exception.ClientStateException;
import com.cccdlabs.sarva.data.p2p.nearby.exception.PermissionException;
import com.cccdlabs.sarva.data.p2p.nearby.utils.NearbyUtils;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.StatusCallback;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Cancellable;

/**
 * Abstraction for Nearby Messages implementation of P2P communication between devices.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public final class PartnerNearbyMessagesClient {

    /**
     * Object to use for synchronizing Map variables.
     */
    private static final Object LOCK = new Object();

    /**
     * Nearby discovery mode, set to broadcast and scan for pairing codes to/from other devices.
     */
    private static final int DISCOVERY_MODE = Strategy.DISCOVERY_MODE_DEFAULT;

    /**
     * Nearby distance covered, allows messages to be exchanged over any distance.
     */
    private static final int DISTANCE_TYPE = Strategy.DISTANCE_TYPE_DEFAULT;

    /**
     * Nearby TTL in seconds, NOTE: Strategy.TTL_SECONDS_INFINITE not supported for publishing.
     */
    private static final int TTL_SECONDS = Strategy.TTL_SECONDS_MAX;

    /**
     * Container for instances created from the {@link Builder} class.
     *
     * @see Builder
     */
    private static volatile Map<Integer, PartnerNearbyMessagesClient> instances;

    /**
     * Container for RxJava emitters that subscribe to Nearby Messages.
     *
     * @see <a href="http://reactivex.io/RxJava/javadoc/io/reactivex/FlowableEmitter.html" target="_top">RxJava FlowableEmitter</a>
     */
    private static volatile Map<Integer, FlowableEmitter<PartnerResult>> emitters;

    /**
     * The publish/subscribe status to emit upon status change.
     */
    private volatile static PartnerResult.Status pubSubStatus;

    /**
     * Flag if device is currently publishing a message.
     */
    private volatile static boolean isPublishing;

    /**
     * Flag if device is currently listening for messages from other devices.
     */
    private volatile static boolean isSubscribing;

    /**
     * True if publishing initialized, false if not.
     */
    private volatile static boolean hasInitializedPublish;

    /**
     * True if publishing initialized, false if not.
     */
    private volatile static boolean hasInitializedSubscribe;

    /**
     * Tag used for debug.
     */
    private final String TAG = "[" + PartnerNearbyMessagesClient.this.getClass().getSimpleName() + "]";

    /**
     * Callback for permission change of Nearby use in a user device.
     */
    private static StatusCallback statusCallback;

    /**
     * Listener for Nearby messages received from other devices.
     */
    private static MessageListener messageListener;

    /**
     * The Nearby message that emits from this device to other devices.
     */
    private static Message message;

    /**
     * The MessagesClient to use for publishing and subscribing.
     */
    private static MessagesClient messagesClient;

    /**
     * The Android application context.
     */
    private Context context;

    /**
     * The Android activity utilizing this class.
     */
    private Activity activity;

    /**
     * RxJava Flowable object that passes on Nearby messages to subscribers.
     *
     * @see <a href="http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Flowable.html" target="_top">RxJava Flowable</a>
     */
    private Flowable<PartnerResult> flowable;

    /**
     * {@link com.cccdlabs.sarva.domain.model.partners.PartnerMessage.Mode} for publishing.
     */
    private PartnerMessage.Mode publishMode;

    /**
     * True if client to publish, false if not.
     */
    private boolean hasPublish;

    /**
     * True if client to subscribe, false if not.
     */
    private boolean hasSubscribe;

    /**
     * True if class instance has called {@link #finish()}, effectively ending functionality.
     */
    private boolean hasFinished;

    /**
     * The instance MessagesClient to use for publishing and subscribing. Use should only be limited to mock
     * instances for unit tests.
     */
    private MessagesClient subMessagesClient;

    /**
     * True to debug output to System.out.
     */
    private boolean debug;

    /**
     * Extension of the {@link MessageListener} that links the Nearby messages received to the
     * RxJava Flowable objects that emit the result in the application.
     */
    public static class PartnerMessageListener extends MessageListener {

        /**
         * Constructor.
         */
        private PartnerMessageListener() {}

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
            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setEmitting(true); // Set the partner to true since within range
            emitResult(new PartnerResult(partner));
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
            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setEmitting(false); // Set the partner to false since out of range
            emitResult(new PartnerResult(partner));
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
            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setDistance(distance.getMeters());
            partner.setAccuracy(distance.getAccuracy());
            partner.setEmitting(true); // Set the partner to true since within range
            emitResult(new PartnerResult(partner));
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
            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setRssi(bleSignal.getRssi());
            partner.setTxPower(bleSignal.getTxPower());
            partner.setEmitting(true); // Set the partner to true since within range
            emitResult(new PartnerResult(partner));
        }
    }

    /**
     * Callback for when Nearby Messages permission is changed on this device.
     */
    protected class PartnerStatusCallback extends StatusCallback {
        @Override
        public void onPermissionChanged(boolean hasPermission) {
            if ( ! hasPermission) {
                emitError(new PermissionException("Nearby permission not granted"));
            }
        }
    }

    /**
     * Callback for when Nearby Messages publishing has expired by TTL.
     */
    protected class PartnerPublishCallback extends PublishCallback {
        @Override
        public void onExpired() {
            if (debug) {
                String msg = TAG + "[" + getClass().getSimpleName() + "][onExpired] instance[";
                msg += PartnerNearbyMessagesClient.this.hashCode() + "]";
                System.out.println(msg);
            }

            isPublishing = false;
            /*
            synchronized (LOCK) {
                message = null;
                pubSubStatus.isPublishing = false;
                //emitResult(new PartnerResult(pubSubStatus));
            }
            */

            // Nearby seems to like to invoke the PublishCallback when unpublishing,
            // so this bit here to restart publishing if necessary
            if (hasPublish && ! hasInitializedPublish) {
                publish();
            }
        }
    }

    /**
     * Callback for when Nearby Messages subscribing has expired by TTL.
     */
    protected class PartnerSubscribeCallback extends SubscribeCallback {
        @Override
        public void onExpired() {
            if (debug) {
                String msg = TAG + "[" + getClass().getSimpleName() + "][onExpired] instance[";
                msg += PartnerNearbyMessagesClient.this.hashCode() + "]";
                System.out.println(msg);
            }

            isSubscribing = false;
            // Nearby seems to like to invoke the PublishCallback when unpublishing,
            // so this bit here to restart publishing if necessary
            if (hasSubscribe && ! hasInitializedSubscribe) {
                subscribe();
            }
            /*
            synchronized (LOCK) {
                messageListener = null;
                pubSubStatus.isSubscribing = false;
                //emitResult(new PartnerResult(pubSubStatus));
            }
            */
        }
    }

    /**
     * Builder pattern class to create instances of PartnerNearbyMessagesClient. Note that instances
     * are tracked in a {@link Map}.
     */
    public static class Builder {

        /**
         * The Android context.
         */
        private Context context;

        /**
         * The Android Activity.
         */
        private Activity activity;

        /**
         * {@link com.cccdlabs.sarva.domain.model.partners.PartnerMessage.Mode} used in Nearby
         * message to publish.
         */
        private PartnerMessage.Mode publishMode;

        /**
         * True if instance to use publishing, false if not.
         */
        private boolean hasPublish;

        /**
         * True if instance to use subscribing, false if not.
         */
        private boolean hasSubscribe;

        /**
         * The Nearby Messages client instance.
         */
        private MessagesClient messagesClient;

        /**
         * True for debug output to System.out.
         */
        private boolean debug;

        /**
         * Constructor.
         *
         * @param activity The Android activity
         */
        public Builder(Activity activity) {
            this.activity = activity;
        }

        /**
         * Constructor.
         *
         * @param context The Android context
         */
        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Sets the publishing {@link com.cccdlabs.sarva.domain.model.partners.PartnerMessage.Mode}.
         *
         * @param publishMode   The publish Mode
         * @return              Builder instance for chaining methods in this class
         */
        public Builder setPublishMode(PartnerMessage.Mode publishMode) {
            this.publishMode = publishMode;
            return this;
        }

        /**
         * Sets publishing active or inactive. Inactive is default.
         *
         * @param hasPublish    True to enable publishing, false if not
         * @return              Builder instance for chaining methods in this class
         */
        public Builder hasPublish(boolean hasPublish) {
            this.hasPublish = hasPublish;
            return this;
        }

        /**
         * Sets subscribing active or inactive. Inactive is default.
         *
         * @param hasSubscribe  True to enable subscribing, false if not
         * @return              Builder instance for chaining methods in this class
         */
        public Builder hasSubscribe(boolean hasSubscribe) {
            this.hasSubscribe = hasSubscribe;
            return this;
        }

        /**
         * Sets the MessagesClient for use with unit tests.
         *
         * @param messagesClient    The MessagesClient
         * @return                  Builder instance for chaining methods in this class
         */
        public Builder setMessagesClient(MessagesClient messagesClient) {
            this.messagesClient = messagesClient;
            return this;
        }

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        /**
         * Creates and returns an instance of the PartnerNearbyMessagesClient class.
         *
         * @return PartnerNearbyMessagesClient object
         */
        public PartnerNearbyMessagesClient build() {
            checkParameters();
            PartnerNearbyMessagesClient client = new PartnerNearbyMessagesClient();
            client.activity = activity;
            client.context = context;
            client.publishMode = publishMode == null ? PartnerMessage.Mode.DEFAULT : publishMode;
            client.hasPublish = hasPublish;
            client.hasSubscribe = hasSubscribe;
            client.subMessagesClient = messagesClient;
            client.debug = debug;
            int index = client.hashCode();

            synchronized (LOCK) {
                if (debug) {
                    String msg = client.TAG + "[" + getClass().getSimpleName() + "][build] instance[";
                    msg += index + "]";
                    System.out.println(msg);
                }

                instances.put(index, client);
            }

            return client;
        }

        /**
         * Verifies that a non-null {@link Activity} or {@link Context} is set and publishing
         * and/or subscribing is enabled.
         */
        private void checkParameters() {
            String message = null;
            if (activity == null && context == null) {
                message += "Builder(Activity) or Builder(Context) must be set with non-null value";
            }

            if (!hasPublish && !hasSubscribe) {
                if (message != null) {
                    message += ", ";
                }
                message += "either hasPublish(true) or hasSubscribe(true) must be called for publish/subscribe";
            }

            if (message != null) {
                message = getClass().getSimpleName() + ": " + message;
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Constructor.
     */
    private PartnerNearbyMessagesClient() {
        if (instances == null) {
            synchronized (LOCK) {
                if (instances == null) {
                    instances = new HashMap<>();
                    emitters = new HashMap<>();
                    pubSubStatus = new PartnerResult.Status();
                }
            }
        }
        initFlowable();
    }

    /**
     * Subclass to return the type of message for Nearby publishing.
     *
     * @return  The Mode, published message type
     * @see     PartnerMessage.Mode
     */
    public PartnerMessage.Mode getPublishMode() {
        checkStatus();
        return publishMode;
    }

    /**
     * Returns the RxJava Flowable, or connection the Nearby messages received to other functions
     * within the application.
     *
     * @return  The RxJava Flowable object
     * @see     <a href="http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Flowable.html" target="_top">Flowable</a>
     */
    public Flowable<PartnerResult> getPartnerFlowable() {
        checkStatus();
        return flowable;
    }

    /**
     * Returns true if publishing a Nearby message, false if not.
     *
     * @return True if publishing a Nearby message, false if not
     */
    public boolean isPublishing() {
        return isPublishing;
    }

    /**
     * Returns true if subscribing to Nearby messages, false if not.
     *
     * @return True if subscribing to Nearby messages, false if not
     */
    public boolean isSubscribing() {
        return isSubscribing;
    }

    /**
     * Returns true if {@link #finish()} called on instance of this class.
     *
     * @return True if <code>finish()</code> called, false if not
     */
    public boolean hasFinished() {
        return hasFinished;
    }

    /**
     * Unregisters emitters and resets the class to an initial state.
     */
    public static void destroy() {
        synchronized (LOCK) {
            if (instances == null || instances.size() == 0) {
                return;
            }

            Set<Integer> iKeys = instances.keySet();
            for (Integer iKey : iKeys) {
                PartnerNearbyMessagesClient instance = instances.get(iKey);
                if (instance == null) {
                    continue;
                }
                if (emitters != null && emitters.size() > 0) {
                    Set<Integer> eKeys = emitters.keySet();
                    for (Integer eKey : eKeys) {
                        FlowableEmitter<PartnerResult> emitter = emitters.get(eKey);
                        if (emitter == null) {
                            continue;
                        }

System.out.println("[PartnerNearbyMessagesClient][destroy] unregister emitter[" + emitter.hashCode() + "]");

                        instance.unregisterEmitter(emitter);
                        emitter.onComplete();
                    }
                }
                instance.finish();
            }
        }
    }

    /**
     * Checks if the <code>finish()</code> method has been called which terminates the Client
     * instance of this class. If called, throws a {@link ClientStateException} runtime exception.
     */
    protected void checkStatus() {
        if (hasFinished) {
            throw new ClientStateException("Client instance has been destroyed");
        }
    }

    /**
     * Initializes the RxJava {@link Flowable} which provides the connection for Nearby messages
     * received, handles the message data and provides it to other "observers" for functions
     * within the application.
     * <p>
     * Note that after initializing, {@link #subscribe()} is triggered when
     * a "subscriber" is set for this <code></code>Flowable</code> via <code>Flowable.subscribe()</code>.
     *
     * @see <a href="http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Flowable.html" target="_top">Flowable</a>
     */
    protected void initFlowable() {
        flowable = Flowable.create(
                new FlowableOnSubscribe<PartnerResult>() {
                    @Override
                    public void subscribe(final FlowableEmitter<PartnerResult> emitter) throws Exception {
                        try {
                            emitter.setCancellable(new Cancellable() {
                                @Override
                                public void cancel() throws Exception {
                                    if (debug) {
                                        String msg = TAG + "[initFlowable] canceled";
                                        System.out.println(msg);
                                    }

                                    PartnerNearbyMessagesClient.this.unregisterEmitter(emitter);
                                    emitter.onComplete();
                                }
                            });

                            if (debug) {
                                String msg = TAG + "[initFlowable] subscribed";
                                System.out.println(msg);
                            }
                            PartnerNearbyMessagesClient.this.registerEmitter(emitter);
                        } catch (Exception e) {
                            if (emitter.isCancelled()) {
                                Exceptions.propagate(e);
                            } else {
                                emitter.onError(e); // NOTE: this will trigger the Cancellable above
                            }
                        }
                    }
                },
                BackpressureStrategy.MISSING
        );
    }

    /**
     * Returns the Nearby Messages client with a {@link Context} or {@link Activity} passed
     * into the constructor of an instance of this class.
     *
     * @return The MessagesClient
     */
    protected MessagesClient getMessagesClient() {
        if (subMessagesClient != null) {
            return subMessagesClient;
        }

        MessagesClient messagesClient = context == null
                ? Nearby.getMessagesClient(activity)
                : Nearby.getMessagesClient(context);

        if (statusCallback == null) {
            statusCallback = new PartnerStatusCallback();
            messagesClient.registerStatusCallback(statusCallback);
        }

        if (debug) {
            String msg = TAG + "[getMessagesClient] instance[" + messagesClient.hashCode() + "]";
            System.out.println(msg);
        }

        return messagesClient;
    }

    /**
     * Upon receving a subscriber in the class Flowable, this saves the FlowableEmitter in a
     * {@link Map} and will trigger {@link #publish()} and {@link #subscribe()} if it is the first
     * subscriber.
     *
     * @param emitter   The FlowableEmitter
     * @return          The hash code generated from the emitter and map index
     */
    protected int registerEmitter(@NonNull FlowableEmitter<PartnerResult> emitter) {
        int hashCode = emitter.hashCode();
        synchronized (LOCK) {
            if (emitters.containsKey(hashCode)) {
                return hashCode;
            }
            emitters.put(hashCode, emitter);
        }

        if (debug) {
            String msg = TAG + "[registerEmitter] emitters[" + hashCode + ", size: " + emitters.size() + "]";
            System.out.println(msg);
        }

        if (isPublishing) {
            // Cancel any current publishing before publishing
            unpublish(hasPublish);
        } else if (hasPublish) {
            publish();
        }

        if (isSubscribing) {
            // Cancel any current subscribing before publishing
            unsubscribe(hasSubscribe);
        } else if (hasSubscribe) {
            subscribe();
        }

        return hashCode;
    }

    /**
     * Upon canceling a subscriber in the class Flowable, this removes the FlowableEmitter from the
     * {@link Map} and will trigger {@link #unpublish(boolean)} and {@link #unsubscribe(boolean)}
     * if it is the only subscriber left.
     *
     * @param emitter   The FlowableEmitter
     * @return          True if emitter unregistered successfully
     */
    protected boolean unregisterEmitter(FlowableEmitter<PartnerResult> emitter) {
        if (emitter != null) {
            int hashCode = emitter.hashCode();

            synchronized (LOCK) {
                if (emitters != null) {
                    if (emitters.containsKey(hashCode)) {
                        reset();
                        emitters.remove(hashCode);
                        if (debug) {
                            String msg = TAG + "[unregisterEmitter] emitters[" + hashCode + ", size: ";
                            msg += emitters.size() + "]";
                            System.out.println(msg);
                        }

                        finish();
                    }
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Emits a {@link PartnerResult} from {@link FlowableEmitter} emitters.
     *
     * @param partner The PartnerResult object
     */
    protected static void emitResult(@NonNull PartnerResult partner) {
        Throwable throwable = partner.getException();
        boolean hasEmittedError = false;

        synchronized (LOCK) {
            if (emitters != null && emitters.size() > 0) {
                Set<Integer> keys = emitters.keySet();
                for (Integer key : keys) {
                    FlowableEmitter<PartnerResult> emitter = emitters.get(key);
                    if (emitter != null && !emitter.isCancelled()) {
                        emitter.onNext(partner);
                        hasEmittedError = throwable != null;
                    }
                }
            }
        }

        // check if Throwable to be emitted and if not,
        // send to RxJava default handler
        if (throwable != null && !hasEmittedError) {
            Exceptions.propagate(throwable);
        }
    }

    /**
     * If an error occurs where Nearby Messages publishing and/or subscribing cannot continue,
     * this will propagate the error to the {@link FlowableEmitter} emitters' code>onError()</code>
     * handlers which effectively stops their subscriptions.
     * <p>
     * Note that if there no emitters, the error is propagated to the RxJava error handler.
     *
     * @param throwable The thrown error
     */
    protected static void emitError(@NonNull Throwable throwable) {
        boolean hasEmitted = false;

        synchronized (LOCK) {
            if (emitters != null && emitters.size() > 0) {
                Set<Integer> keys = emitters.keySet();
                for (Integer key : keys) {
                    FlowableEmitter<PartnerResult> emitter = emitters.get(key);
                    if (emitter != null && !emitter.isCancelled()) {
                        emitter.onError(throwable);
                        hasEmitted = true;
                    }
                }
            }
        }

        if (!hasEmitted) {
            Exceptions.propagate(throwable);
        }
    }

    /**
     * Starts Nearby Messages publishing, emitting a {@link Message} containing this device's
     * information and {@link PartnerMessage.Mode} or message type.
     */
    protected synchronized void publish() {
        if (isPublishing || hasFinished) {
            if (debug) {
                String msg = TAG + "[publish] exiting, ";
                msg += hasFinished ? "instance is destroyed" : "already publishing";
                System.out.println(msg);
            }

            return;
        }

        isPublishing = true;
        Context c = context == null ? activity : context;
        message = NearbyUtils.createMessage(c, publishMode);

        PublishCallback callback = new PartnerPublishCallback();
        Strategy strategy = new Strategy.Builder()
                .setDiscoveryMode(DISCOVERY_MODE)
                .setDistanceType(DISTANCE_TYPE)
                .setTtlSeconds(TTL_SECONDS)
                .build();
        PublishOptions options = new PublishOptions.Builder()
                .setStrategy(strategy)
                .setCallback(callback)
                .build();

        if (debug) {
            System.out.println(TAG + "[publish]");
        }

        Task<Void> task =  getMessagesClient().publish(message, options);
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (debug) {
                    System.out.println(TAG + "[publish] onFailure[" + e + "]");
                }

                if (hasFinished) {
                    isPublishing = false;
                    return;
                }

                synchronized (LOCK) {
                    isPublishing = false;
                    emitError(e);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (debug) {
                    System.out.println(TAG + "[publish] onSuccess");
                }
                if (hasFinished) {
                    if (debug) {
                        System.out.println(TAG + "[publish] onSuccess aborting, not publishing");
                    }
                    isPublishing = false;
                    return;
                }

                synchronized (LOCK) {
                    hasInitializedPublish = true;
                    pubSubStatus.isPublishing = true;
                    emitResult(new PartnerResult(pubSubStatus));
                }
            }
        });
    }

    /**
     * Initializes a {@link MessageListener} from a given {@link FlowableEmitter} and starts
     * Nearby Messages subscribing. The message listener will listen for received messages and the
     * emitter handles the messages, passing them on to subscribers in the application.
     * Subsequent calls are ingnored until {@link #unsubscribe(boolean)} called.
     */
    protected synchronized void subscribe() {
        if (isSubscribing || hasFinished) {
            if (debug) {
                String msg = TAG + "[subscribe] exiting, ";
                msg += hasFinished ? "instance is destroyed" : "already subscribing";
                System.out.println(msg);
            }

            return;
        }

        isSubscribing = true;
        messageListener = new PartnerMessageListener();
        SubscribeCallback callback = new PartnerSubscribeCallback();
        Strategy strategy = new Strategy.Builder()
                .setDiscoveryMode(DISCOVERY_MODE)
                .setDistanceType(DISTANCE_TYPE)
                .setTtlSeconds(TTL_SECONDS)
                .build();
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(strategy)
                .setCallback(callback)
                .build();

        if (debug) {
            System.out.println(TAG + "[subscribe]");
        }

        Task<Void> task = getMessagesClient().subscribe(messageListener, options);
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (debug) {
                    System.out.println(TAG + "[subscribe] onFailure[" + e + "]");
                }

                if (hasFinished) {
                    isSubscribing = false;
                    return;
                }

                synchronized (LOCK) {
                    isSubscribing = false;
                    emitError(e);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (debug) {
                    System.out.println(TAG + "[subscribe] onSuccess");
                }

                if (hasFinished) {
                    if (debug) {
                        System.out.println(TAG + "[subscribe] onSuccess aborting, not subscribing");
                    }
                    isSubscribing = false;
                    return;
                }

                synchronized (LOCK) {
                    hasInitializedSubscribe = true;
                    pubSubStatus.isSubscribing = true;
                    emitResult(new PartnerResult(pubSubStatus));
                }
            }
        });
    }

    /**
     * Stops Nearby Messages publishing.
     */
    protected synchronized void unpublish(final boolean resetPublish) {
        if (message == null || !isPublishing) {
            if (debug) {
                System.out.println(TAG + "[unpublish(" + resetPublish + ")] already not publishing, exiting");
            }

            return;
        }

        isPublishing = false;
        Task<Void> task = getMessagesClient().unpublish(message);
        if (debug) {
            System.out.println(TAG + "[unpublish(" + resetPublish + ")]");
        }

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (debug) {
                    System.out.println(TAG + "[unpublish(" + resetPublish + ")] onFailure[" + e + "]");
                }

                hasInitializedPublish = false;
                if (hasFinished) {
                    return;
                }

                synchronized (LOCK) {
                    message = null;
                    pubSubStatus.isPublishing = false;
                }

                // emitting an error will trigger unpublish/unsubscribe again,
                // so emitting through onNext
                emitResult(new PartnerResult(e));
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (debug) {
                    String msg;
                    msg = TAG + "[unpublish(" + resetPublish + ")] onSuccess ";
                    msg += "instance[" + PartnerNearbyMessagesClient.this.hashCode() + "]";
                    System.out.println(msg);
                }

                hasInitializedPublish = false;
                if (hasFinished) {
                    return;
                }

                synchronized (LOCK) {
                    message = null;
                    pubSubStatus.isPublishing = false;
                    //emitResult(new PartnerResult(pubSubStatus));

                    if (resetPublish) {
                        publish();
                    } else if (hasFinished && !isSubscribing) {
                        if (debug) {
                            System.out.println(TAG + "[unpublish(" + resetPublish + ")] setting pubSubStatus null");
                        }

                        pubSubStatus = null;
                    }
                }
            }
        });
    }

    /**
     * Stops Nearby Messages subscribing.
     */
    private synchronized void unsubscribe(final boolean resetSubscribing) {
        if (messageListener == null || !isSubscribing) {
            if (debug) {
                System.out.println(TAG + "[unsubscribe(" + resetSubscribing + ")] already not subscribing, exiting");
            }

            return;
        }

        isSubscribing = false;
        Task<Void> task = getMessagesClient().unsubscribe(messageListener);
        if (debug) {
            System.out.println(TAG + "[unsubscribe(" + resetSubscribing + ")]");
        }

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (debug) {
                    System.out.println(TAG + "[unsubscribe(" + resetSubscribing + ")] onFailure[" + e + "]");
                }

                hasInitializedSubscribe = false;
                if (hasFinished) {
                    return;
                }

                synchronized (LOCK) {
                    messageListener = null;
                    pubSubStatus.isSubscribing = false;
                }

                // emitting an error will trigger unpublish/unsubscribe again,
                // so emitting through onNext
                emitResult(new PartnerResult(e));
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (debug) {
                    String msg;
                    msg = TAG + "[unsubscribe(" + resetSubscribing + ")] onSuccess ";
                    msg += "instance[" + PartnerNearbyMessagesClient.this.hashCode() + "]";
                    System.out.println(msg);
                }

                hasInitializedSubscribe = false;
                if (hasFinished) {
                    return;
                }

                synchronized (LOCK) {
                    messageListener = null;
                    pubSubStatus.isSubscribing = false;
                    //emitResult(new PartnerResult(pubSubStatus));

                    if (resetSubscribing) {
                        subscribe();
                    } else if (hasFinished && !isPublishing) {
                        if (debug) {
                            System.out.println(TAG + "[unsubscribe(" + resetSubscribing + ")] setting pubSubStatus null");
                        }

                        pubSubStatus = null;
                    }
                }
            }
        });
    }

    /**
     * Closes the Nearby Messages publishing and subscribing and resets the messages client to
     * the initial state.
     */
    protected synchronized void reset() {
        if (emitters.size() > 1) {
            if (debug) {
                System.out.println(TAG + "[reset] exiting, emitter(s) still active");
            }

            return;
        }

        if (debug) {
            System.out.println(TAG + "[reset]");
        }

        unpublish(false);
        unsubscribe(false);
        if (pubSubStatus != null) {
            pubSubStatus.reset();
        }

        if (statusCallback != null && messagesClient != null) {
            messagesClient.unregisterStatusCallback(statusCallback);
            statusCallback = null;
        }
    }

    /**
     * Closes the Nearby Messages publishing and subscribing and tidies up resources for GC.
     */
    protected void finish() {
        if (debug) {
            System.out.println(TAG + "[finish]");
        }

        int hashCode = this.hashCode();
        synchronized (LOCK) {
            if (instances == null) {
                return;
            }

            if (instances.containsKey(hashCode)) {
                instances.remove(hashCode);
                if (debug) {
                    String msg = TAG + "[finish] removing instance[" + hashCode + ", size: ";
                    msg += instances.size() + "]";
                    System.out.println(msg);
                }

                if (instances.size() == 0) {
                    instances = null;
                    emitters = null;
                    if (!isPublishing && !isSubscribing) {
                        if (debug) {
                            System.out.println(TAG + "[finish] setting pubSubStatus null");
                        }

                        pubSubStatus = null;
                    }
                }
            }
        }

        activity = null;
        context = null;
        flowable = null;
        publishMode = null;
        hasFinished = true;
    }
}
