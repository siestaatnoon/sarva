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

abstract public class AbstractNearbyPartnerEmitter implements PartnerEmitter {

    protected static final Strategy STRATEGY = Strategy.DEFAULT;

    protected final MessagesClient messagesClient;

    protected Map<Integer, FlowableEmitter<PartnerResult>> emitters;

    protected Flowable<PartnerResult> flowable;

    protected PartnerRepository repository;

    protected Context context;

    protected Activity activity;

    protected StatusCallback statusCallback;

    protected MessageListener messageListener;

    protected Message message;

    protected boolean isPublishing;

    protected boolean isSubscribing;

    private boolean wasPublishing;


    public class PartnerMessageListener extends MessageListener {

        protected final FlowableEmitter<PartnerResult> emitter;

        protected final PartnerMessage.Mode receiveMode;

        protected final boolean isPaired;

        protected PartnerMessageListener(FlowableEmitter<PartnerResult> emitter, PartnerMessage.Mode receiveMode) {
            this(emitter, receiveMode, false);
        }

        protected PartnerMessageListener(FlowableEmitter<PartnerResult> emitter,
                PartnerMessage.Mode receiveMode, boolean isPaired) {
            this.emitter = emitter;
            this.receiveMode = receiveMode;
            this.isPaired = isPaired;
        }

        @Override
        public void onFound(Message message) {}

        @Override
        public void onLost(Message message) {}

        @Override
        public void onDistanceChanged(Message message, Distance distance) {}

        @Override
        public void onBleSignalChanged(Message message, BleSignal bleSignal) {}

        public void cancelWithError(Throwable throwable) {
            emitter.onError(throwable);
        }

        public void continueWithError(Throwable throwable) {
            emitter.onNext(new PartnerResult(throwable));
        }

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

    private class PartnerStatusCallback extends StatusCallback {
        @Override
        public void onPermissionChanged(boolean hasPermission) {
            if ( ! hasPermission) {
                emitCancelableError(new PermissionException("Nearby permission not granted"));
            }
        }
    }

    private class PartnerSubscribeCallback extends SubscribeCallback {
        @Override
        public void onExpired() {
            emitCancelableError(new SubscribeExpiredException("Subscription has expired"));
        }
    }

    private class PartnerPublishCallback extends PublishCallback {
        @Override
        public void onExpired() {
            emitCancelableError(new PublishExpiredException("Publishing has expired"));
        }
    }


    public AbstractNearbyPartnerEmitter(@NonNull Activity activity, PartnerRepository repository) {
        this.context = null;
        this.activity = activity;
        this.repository = repository;
        messagesClient = getMessagesClient();
        statusCallback = getStatusCallback();
        messagesClient.registerStatusCallback(statusCallback);
        emitters = new HashMap<>();
        initFlowable();
    }

    public AbstractNearbyPartnerEmitter(@NonNull Context context, PartnerRepository repository) {
        this.context = context;
        this.activity = null;
        this.repository = repository;
        messagesClient = getMessagesClient();
        statusCallback = getStatusCallback();
        messagesClient.registerStatusCallback(statusCallback);
        emitters = new HashMap<>();
        initFlowable();
    }

    abstract protected MessageListener getMessageListener(FlowableEmitter<PartnerResult> emitter);

    abstract protected PartnerMessage.Mode getPublishMode();

    @Override
    public Flowable<PartnerResult> getPartnerEmitter() {
        return flowable;
    }

    @Override
    public void pauseEmitter() {
        wasPublishing = isPublishing;
        unpublish();
        unsubscribe();
    }

    @Override
    public void resumeEmitter() {
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

    public boolean isPublishing() {
        return isPublishing;
    }

    public boolean isSubscribing() {
        return isSubscribing;
    }

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

    protected MessagesClient getMessagesClient() {
        return context == null
            ? Nearby.getMessagesClient(activity)
            : Nearby.getMessagesClient(context);
    }

    protected StatusCallback getStatusCallback() {
        return new PartnerStatusCallback();
    }

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

        PublishCallback callback = new PartnerPublishCallback();
        PublishOptions options = new PublishOptions.Builder()
                .setStrategy(STRATEGY)
                .setCallback(callback)
                .build();
        
        messagesClient.publish(message, options);
        isPublishing = true;
    }

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

        SubscribeCallback callback = new PartnerSubscribeCallback();
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(STRATEGY)
                .setCallback(callback)
                .build();
        messagesClient.subscribe(messageListener, options);
        isSubscribing = true;
    }

    protected void unpublish() {
        if (message == null || ! isPublishing) {
            return;
        }
        messagesClient.unpublish(message);
        message = null;
        isPublishing = false;
    }

    protected void unsubscribe() {
        if (messageListener == null || ! isSubscribing) {
            return;
        }
        messagesClient.unsubscribe(messageListener);
        messageListener = null;
        isSubscribing = false;
    }

    protected int registerEmitter(@NonNull FlowableEmitter<PartnerResult> emitter) {
        int hashCode = emitter.hashCode();
        if (!emitters.containsKey(hashCode)) {
            emitters.put(hashCode, emitter);
        }
        return hashCode;
    }

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
