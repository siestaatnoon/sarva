package com.cccdlabs.sarva.data.p2p.nearby.base;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.p2p.nearby.exception.PermissionException;
import com.cccdlabs.sarva.data.p2p.nearby.exception.PublishExpiredException;
import com.cccdlabs.sarva.data.p2p.nearby.exception.SubscribeExpiredException;
import com.cccdlabs.sarva.data.p2p.nearby.utils.NearbyUtils;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.domain.p2p.base.PartnerEmitter;
import com.cccdlabs.sarva.domain.p2p.exception.InvalidPartnerException;
import com.cccdlabs.sarva.domain.p2p.exception.UnpairedPartnerException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryQueryException;
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
abstract public class AbstractNearbyPartnerEmitter implements PartnerEmitter {

    /**
     * Default {@link Strategy} for device communication, in this case Bluetooth BLE and audio
     * with an indefinite TTL.
     */
    protected static final Strategy STRATEGY = Strategy.DEFAULT;

    /**
     * The Nearby Messages client.
     */
    protected final MessagesClient messagesClient;

    /**
     * Container for RxJava emitters that subscribe to Nearby Messages.
     *
     * @see <a href="http://reactivex.io/RxJava/javadoc/io/reactivex/FlowableEmitter.html" target="_top">RxJava FlowableEmitter</a>
     */
    protected Map<Integer, FlowableEmitter<PartnerResult>> emitters;

    /**
     * RxJava Flowable object that passes on Nearby messages to subscribers.
     *
     * @see <a href="http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Flowable.html" target="_top">RxJava Flowable</a>
     */
    protected Flowable<PartnerResult> flowable;

    /**
     * Storage for {@link com.cccdlabs.sarva.domain.model.partners.Partner} objects used in communication
     * between devices.
     */
    protected PartnerRepository repository;

    /**
     * The Android application context.
     */
    protected Context context;

    /**
     * The Android activity utilizing this class.
     */
    protected Activity activity;

    /**
     * Callback for permission change of Nearby use in a user device.
     */
    protected StatusCallback statusCallback;

    /**
     * Listener for Nearby messages received from other devices.
     */
    protected MessageListener messageListener;

    /**
     * The Nearby Message that emits from this device to other devices.
     */
    protected Message message;

    /**
     * Flag if device is currently publishing a message.
     */
    protected boolean isPublishing;

    /**
     * Flag if device is currently listening for messages from other devices.
     */
    protected boolean isSubscribing;

    /**
     * Flag to determine if device was publishing before a call to {@link #pauseEmitter()} so it
     * will or will not resume publishing on subsequent call to {@link #resumeEmitter()}.
     */
    private boolean wasPublishing;

    /**
     * Extension of the {@link MessageListener} that links the Nearby messages received to the
     * RxJava Flowable objects that emit the result in the application.
     */
    public class PartnerMessageListener extends MessageListener {

        /**
         * The RxJava FlowableEmitter object emitting the resulting {@link PartnerResult} from
         * a Nearby message.
         */
        protected final FlowableEmitter<PartnerResult> emitter;

        /**
         * The message type contained within the Nearby message that is expected by this listener.
         *
         * @see PartnerMessage.Mode
         */
        protected final PartnerMessage.Mode receiveMode;

        /**
         * Flag if device is expected to be paired. NOTE: This does not mean Bluetooth paired but
         * rather the device has been acknowledged by this device and info saved in the database.
         */
        protected final boolean isPaired;

        /**
         * Constructor, does not expect Nearby messages received to be from devices that are already
         * saved and recorded in the database.
         *
         * @param emitter       The RxJava object emitting results of Nearby messages received
         * @param receiveMode   Message type contained within the Nearby message
         * @see                 PartnerMessage.Mode
         */
        protected PartnerMessageListener(FlowableEmitter<PartnerResult> emitter, PartnerMessage.Mode receiveMode) {
            this(emitter, receiveMode, false);
        }

        /**
         * Constructor with flag if it expects or does not expect Nearby messages received
         * to be from devices that are already saved and recorded in the database.
         *
         * @param emitter       The RxJava object emitting results of Nearby messages received
         * @param receiveMode   Message type contained within the Nearby message
         * @param isPaired      Flag if device is expected to be paired
         * @see                 PartnerMessage.Mode
         */
        protected PartnerMessageListener(FlowableEmitter<PartnerResult> emitter,
                PartnerMessage.Mode receiveMode, boolean isPaired) {
            this.emitter = emitter;
            this.receiveMode = receiveMode;
            this.isPaired = isPaired;
        }

        /**
         * No op, defined in subclass.
         *
         * @param message The Nearby message received from a user device
         */
        @Override
        public void onFound(Message message) {}

        /**
         * No op, defined in subclass.
         *
         * @param message The Nearby message received from a user device
         */
        @Override
        public void onLost(Message message) {}

        /**
         * No op, defined in subclass.
         *
         * @param message   The Nearby message received from a user device
         * @param distance  The {@link Distance} object with distance and accuracy info from a
         *                  user device
         */
        @Override
        public void onDistanceChanged(Message message, Distance distance) {}

        /**
         * No op, defined in subclass.
         *
         * @param message   The Nearby message received from a user device
         * @param bleSignal The {@link BleSignal} object with Bluetooth BLE RSSI and TX power info
         *                  from a user device
         */
        @Override
        public void onBleSignalChanged(Message message, BleSignal bleSignal) {}

        /**
         * Emits an error through the FlowableEmitter onError() which effectively cancels
         * the emitter and all future emissions. Should be called for errors where processes
         * cannot/should not continue.
         *
         * @param throwable The error to emit in the FlowableEmitter onError()
         */
        public void cancelWithError(Throwable throwable) {
            emitter.onError(throwable);
        }

        /**
         * Emits an error through the FlowableEmitter onNext(), via a {@link PartnerResult} object,
         * which allows the emitter to continue on. Should be called where errors do not affect
         * processes and the emitter can still continue.
         *
         * @param throwable The error to emit in the FlowableEmitter onError()
         */
        public void continueWithError(Throwable throwable) {
            emitter.onNext(new PartnerResult(throwable));
        }

        /**
         * Validates a Nearby message received contains properly formatted data from a user device and,
         * optionally, if the user device has been saved on this device.
         *
         * @param message   The Nearby message received
         * @return          True if the message has validated, false otherwise
         */
        protected boolean checkInvalidState(Message message) {
            PartnerMessage partnerMessage = NearbyUtils.toPartnerMessage(message);
            if (emitter.isCancelled() || (receiveMode != null && receiveMode != partnerMessage.getMode())) {
                // If FlowableEmitter is canceled (will perform any cleanup operations from Cancellable)
                // or if receive mode not matching, exit and return false. No exception to emit.
                return true;
            }

            Throwable throwable = null;
            if (partnerMessage == null) {
                throwable = new InvalidPartnerException("Partner retrieved from Message null value");
            } else if (isPaired) {
                try {
                    if (!repository.isActive(partnerMessage.getUuid())) {
                        throwable = new UnpairedPartnerException("Partner does not exist locally from received message");
                    }
                } catch (RepositoryQueryException e) {
                    throwable = e;
                }
            }

            if (throwable != null) {
                // NOTE: onNext used to propagate error instead of onError
                // to continue FlowableEmitter emission
                continueWithError(throwable);
                return true;
            }

            return false;
        }
    }

    /**
     * Callback for when Nearby Messages permission is changed on this device.
     */
    protected class PartnerStatusCallback extends StatusCallback {
        @Override
        public void onPermissionChanged(boolean hasPermission) {
            if ( ! hasPermission) {
                emitCancelableError(new PermissionException("Nearby permission not granted"));
            }
        }
    }

    /**
     * Callback for when Nearby Messages subscribing has expired by TTL.
     */
    private class PartnerSubscribeCallback extends SubscribeCallback {
        @Override
        public void onExpired() {
            emitCancelableError(new SubscribeExpiredException("Subscription has expired"));
        }
    }

    /**
     * Callback for when Nearby Messages publishing has expired by TTL.
     */
    private class PartnerPublishCallback extends PublishCallback {
        @Override
        public void onExpired() {
            emitCancelableError(new PublishExpiredException("Publishing has expired"));
        }
    }

    /**
     * No argument constructor, throws IllegalArgumentException. Must be instatiated with:
     * <br>
     * AbstractNearbyPartnerEmitter(Context, PartnerRepository) or<br>
     * AbstractNearbyPartnerEmitter(Activity, PartnerRepository)
     */
    public AbstractNearbyPartnerEmitter() {
        String message = "Must be instantiated with ";
        message += "AbstractNearbyPartnerEmitter(Context, PartnerRepository) or ";
        message += "AbstractNearbyPartnerEmitter(Activity, PartnerRepository)";
        throw new IllegalArgumentException(message);
    }

    /**
     * Constructor. When passed in an {@link Activity}, notifications from the Nearby library will be
     * prompted from it for resolution of resolvable connection errors.
     *
     * @param activity      The Activity utilizing the Nearby Messaging
     * @param repository    The PartnerRepository for database functions
     */
    public AbstractNearbyPartnerEmitter(@NonNull Activity activity, PartnerRepository repository) {
        messagesClient = getMessagesClient();
        initialize(null, activity, repository);
    }

    /**
     * Constructor. When passed in an {@link Context}, notifications from the Nearby library will
     * be through a system notification for resolution of resolvable connection errors.
     *
     * @param context       The Android context utilizing the Nearby Messaging
     * @param repository    The PartnerRepository for database functions
     */
    public AbstractNearbyPartnerEmitter(@NonNull Context context, PartnerRepository repository) {
        messagesClient = getMessagesClient();
        initialize(context, null, repository);
    }

    /**
     * Initializes this AbstractNearbyPartnerEmitter. Called from constructor and requires either
     * an {@link Activity} or {@link Context} but not both.
     *
     * @param context       The Android context
     * @param activity      The Activity
     * @param repository    The PartnerRepository for database functions
     */
    private void initialize(Context context, Activity activity, PartnerRepository repository) {
        this.context = context;
        this.activity = activity;
        this.repository = repository;
        statusCallback = getStatusCallback();
        messagesClient.registerStatusCallback(statusCallback);
        emitters = new HashMap<>();
        initFlowable();
    }

    /**
     * The {@link Message} listener for Nearby subscriptions. Preferably extends
     * {@link PartnerMessageListener} but custom listener may be defined in subclass.
     *
     * @param emitter   The RxJava {@link FlowableEmitter} to emit data from messages received
     *                  from Nearby subscriptions
     * @return          The MessageListener
     */
    abstract protected MessageListener getMessageListener(FlowableEmitter<PartnerResult> emitter);

    /**
     * Subclass to return the type of message for Nearby publishing.
     *
     * @return  The Mode, published message type
     * @see     PartnerMessage.Mode
     */
    abstract protected PartnerMessage.Mode getPublishMode();

    /**
     * Returns the RxJava Flowable, or connection the Nearby messages received to other functions
     * within the application.
     *
     * @return  The RxJava Flowable object
     * @see     <a href="http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Flowable.html" target="_top">Flowable</a>
     */
    @Override
    public Flowable<PartnerResult> getPartnerEmitter() {
        return flowable;
    }

    /**
     * Allows the Nearby publishing and subscribing to be "paused" to conserve resources. If
     * already not publishing or subscribing, the call is a no op.
     */
    @Override
    public void pauseEmitter() {
        if (!isPublishing && !isSubscribing) {
            return;
        }

        wasPublishing = isPublishing;
        unpublish();
        unsubscribe();
    }

    /**
     * Resumes Nearby subscribing and/or publishing after call to pauseEmitter(). If
     * already publishing or subscribing, the call is a no op.
     */
    @Override
    public void resumeEmitter() {
        if (isPublishing || isSubscribing) {
            return;
        }

        if (wasPublishing) {
            publish();
        }

        if (emitters != null && emitters.size() > 0) {
            Set<Integer> keys = emitters.keySet();
            for (Integer key : keys) {
                FlowableEmitter<PartnerResult> emitter = emitters.get(key);
                if (emitter != null) {
                    subscribe(emitter);
                }
            }
        }
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
     * Initializes the RxJava {@link Flowable} which provides the connection for Nearby messages
     * received, handles the message data and provides it to other "observers" for functions
     * within the application.
     * <p>
     * Note that after initializing, {@link #subscribe(FlowableEmitter)} is triggered when
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
                                    cleanUp();
                                    emitter.onComplete();
                                }
                            });
                            AbstractNearbyPartnerEmitter.this.subscribe(emitter);
                        } catch (Exception e) {
                            if (!emitter.isCancelled()) {
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
        return context == null
            ? Nearby.getMessagesClient(activity)
            : Nearby.getMessagesClient(context);
    }

    /**
     * Returns the StatusCallback for when Nearby Messages permission is changed on this device.
     * The PartnerStatusCallback defined in this class provides default handling by throwing a
     * {@link PermissionException} and emitted in a {@link FlowableEmitter} onError()
     * but custom behavior can be defined in subclass.
     *
     * @return The StatusCallback
     */
    protected StatusCallback getStatusCallback() {
        return new PartnerStatusCallback();
    }

    /**
     * Returns the PublishCallback for when Nearby Messages publishing has expired by TTL.
     * The PartnerPublishCallback defined in this class provides default handling by throwing a
     * {@link PublishExpiredException} and emitted in a {@link FlowableEmitter} onError()
     * but custom behavior can be defined in subclass.
     *
     * @return The PublishCallback
     */
    protected PublishCallback getPublishCallback() {
        return new PartnerPublishCallback();
    }

    /**
     * Returns the SubscribeCallback for when Nearby Messages subscribing has expired by TTL.
     * The PartnerSubscribeCallback defined in this class provides default handling by throwing a
     * {@link SubscribeExpiredException} and emitted in a {@link FlowableEmitter} onError()
     * but custom behavior can be defined in subclass.
     *
     * @return The SubscribeCallback
     */
    protected SubscribeCallback getSubscribeCallback() {
        return new PartnerSubscribeCallback();
    }

    /**
     * Starts Nearby Messages publishing, emitting a {@link Message} containing this device's
     * information and {@link PartnerMessage.Mode} or message type.
     */
    protected void publish() {
        if (isPublishing) {
            return;
        }

        PartnerMessage.Mode mode = getPublishMode();
        if (mode == null) {
            mode = PartnerMessage.Mode.DEFAULT;
        }
        Context c = context == null ? activity : context;
        message = NearbyUtils.createMessage(c, mode);

        PublishCallback callback = getPublishCallback();
        PublishOptions options = new PublishOptions.Builder()
                .setStrategy(STRATEGY)
                .setCallback(callback)
                .build();
        
        messagesClient.publish(message, options);
        isPublishing = true;
    }

    /**
     * Initializes a {@link MessageListener} from a given {@link FlowableEmitter} and starts
     * Nearby Messages subscribing. The message listener will listen for received messages and the emitter
     * handles the messages, passing them on to subscribers in the application.
     *
     * @param emitter The RxJava FlowableEmitter
     */
    protected void subscribe(FlowableEmitter<PartnerResult> emitter) {
        if (isSubscribing) {
            return;
        }

        registerEmitter(emitter);
        messageListener = getMessageListener(emitter);
        if (messageListener == null) {
            String message = "Override getMessageListener(FlowableEmitter<PartnerResult>) return value null, cannot subscribe";
            emitter.onError(new IllegalStateException(message));
            return;
        }

        SubscribeCallback callback = getSubscribeCallback();
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(STRATEGY)
                .setCallback(callback)
                .build();
        messagesClient.subscribe(messageListener, options);
        isSubscribing = true;
    }

    /**
     * Stops Nearby Messages publishing.
     */
    protected void unpublish() {
        if (message == null || ! isPublishing) {
            return;
        }
        messagesClient.unpublish(message);
        message = null;
        isPublishing = false;
    }

    /**
     * Stops Nearby Messages subscribing.
     */
    protected void unsubscribe() {
        if (messageListener == null || ! isSubscribing) {
            return;
        }
        messagesClient.unsubscribe(messageListener);
        messageListener = null;
        isSubscribing = false;
    }

    /**
     * Upon a call to {@link #subscribe(FlowableEmitter)}, this saves the emitter in a {@link Map}
     * to be used on in call to {@link #resumeEmitter()} to initialize Nearby Messages subscribing
     * again after a previous call to {@link #pauseEmitter()}.
     *
     * @param emitter   The FlowableEmitter
     * @return          The hash code generated from the emitter and map index
     */
    protected int registerEmitter(@NonNull FlowableEmitter<PartnerResult> emitter) {
        int hashCode = emitter.hashCode();
        if (!emitters.containsKey(hashCode)) {
            emitters.put(hashCode, emitter);
        }
        return hashCode;
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
    protected void emitCancelableError(Throwable throwable) {
        if (emitters == null) {
            Exceptions.propagate(throwable);
            return;
        }

        Set<Integer> keys = emitters.keySet();
        for (Integer key : keys) {
            FlowableEmitter<PartnerResult> emitter = emitters.get(key);
            if (emitter != null) {
                emitter.onError(throwable);
            }
        }
    }

    /**
     * Closes the Nearby Messages publishing and subscribing and tidies up for GC.
     */
    protected void cleanUp() {
        unpublish();
        unsubscribe();

        if (emitters != null) {
            emitters.clear();
            emitters = null;
        }

        messagesClient.unregisterStatusCallback(statusCallback);
        statusCallback = null;
        context = null;
        activity = null;
        repository = null;
        flowable = null;
    }
}
