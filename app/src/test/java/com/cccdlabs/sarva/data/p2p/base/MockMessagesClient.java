package com.cccdlabs.sarva.data.p2p.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cccdlabs.sarva.data.p2p.nearby.base.AbstractNearbyPartnerEmitter;
import com.cccdlabs.sarva.data.p2p.nearby.exception.PublishExpiredException;
import com.cccdlabs.sarva.domain.p2p.exception.PartnerException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.StatusCallback;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.mock;

/**
 * Introducing... the MockMessageClient! Bypasses the Google Play Services verification and allows
 * for unit testing classes utilizing the Nearby Messages API publish() and
 * subscribe() methods. Methods in this class are implemented to run synchronously.
 *
 */
public class MockMessagesClient extends MessagesClient {

    private static final String CLASS_TAG = "[MockMessagesClient]";

    private ClientState mClientState;
    private Map<Integer, StatusCallback> statusCallbacks;
    private Message mMessage;
    private MessageListener mMessageListener;
    private PublishOptions mPublishOptions;
    private SubscribeOptions mSubscribeOptions;
    private MockTask mStatusTask;
    private MockTask mPublishTask;
    private MockTask mSubscribeTask;
    private boolean isPublishing;
    private boolean isSubscribing;
    private boolean hasPublishError;
    private boolean hasSubscribeError;
    private boolean hasUnpublishError;
    private boolean hasUnsubscribeError;
    private boolean isClosed;
    private static boolean debug;

    private enum ListenerCallbackMethod {
        ON_FOUND,
        ON_LOST,
        ON_DISTANCE_CHANGED,
        ON_BLE_SIGNAL_CHANGED
    }

    /**
     * Saves members from publish() and subscribe() to reset the state of this client object.
     */
    private class ClientState {

        private Message message;
        private MessageListener messageListener;
        private PublishOptions publishOptions;
        private SubscribeOptions subscribeOptions;

        private Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public MessageListener getMessageListener() {
            return messageListener;
        }

        public void setMessageListener(MessageListener messageListener) {
            this.messageListener = messageListener;
        }

        public PublishOptions getPublishOptions() {
            return publishOptions;
        }

        public void setPublishOptions(PublishOptions publishOptions) {
            this.publishOptions = publishOptions;
        }

        public SubscribeOptions getSubscribeOptions() {
            return subscribeOptions;
        }

        public void setSubscribeOptions(SubscribeOptions subscribeOptions) {
            this.subscribeOptions = subscribeOptions;
        }

        public void clear() {
            message = null;
            messageListener = null;
            publishOptions = null;
            subscribeOptions = null;
        }
    }

    /**
     * Most of the overriden methods from MessagesClient return a Task<Void>, as they are
     * asynchronous operations. Here we'll mock the Task<Void> object as well along with the
     * MessagesClient.
     */
    public static class MockTask extends Task<Void> {

        private OnSuccessListener<? super Void> onSuccessListener;
        private OnCompleteListener<Void> onCompleteListener;
        private OnCanceledListener onCanceledListener;
        private OnFailureListener onFailureListener;
        private Exception thrownException;
        private boolean isComplete;
        private boolean isSuccessful;
        private boolean isCanceled;

        public MockTask() {
            super();
        }

        @Override
        public boolean isComplete() {
            return isComplete;
        }

        @Override
        public boolean isSuccessful() {
            return isSuccessful;
        }

        @Override
        public boolean isCanceled() {
            return isCanceled;
        }

        @Nullable
        @Override
        public Void getResult() {
            if (!isComplete) {
                throw new IllegalStateException("This Task<Void> has not completed");
            }
            return null;
        }

        @Nullable
        @Override
        public <X extends Throwable> Void getResult(@NonNull Class<X> aClass) throws X {
            return getResult();
        }

        @Nullable
        @Override
        public Exception getException() {
            return thrownException;
        }

        @NonNull
        @Override
        public Task<Void> addOnSuccessListener(@NonNull OnSuccessListener<? super Void> onSuccessListener) {
            this.onSuccessListener = onSuccessListener;
            if (MockMessagesClient.debug) {
                System.out.println(CLASS_TAG + "[MockTask] addOnSuccessListener(OnSuccessListener)");
            }

            if (isSuccessful) {
                triggerOnSuccess();
            }
            return this;
        }

        @NonNull
        @Override
        public Task<Void> addOnCompleteListener(@NonNull OnCompleteListener<Void> onCompleteListener) {
            this.onCompleteListener = onCompleteListener;
            if (MockMessagesClient.debug) {
                System.out.println(CLASS_TAG + "[MockTask] addOnCompleteListener(OnCompleteListener)");
            }

            if (isComplete) {
                triggerOnComplete();
            }
            return this;
        }

        @NonNull
        @Override
        public Task<Void> addOnCanceledListener(@NonNull OnCanceledListener onCanceledListener) {
            this.onCanceledListener = onCanceledListener;
            if (MockMessagesClient.debug) {
                System.out.println(CLASS_TAG + "[MockTask] addOnCanceledListener(OnCanceledListener)");
            }

            if (isCanceled) {
                triggerOnCanceled();
            }
            return this;
        }

        @NonNull
        @Override
        public Task<Void> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
            this.onFailureListener = onFailureListener;
            if (MockMessagesClient.debug) {
                System.out.println(CLASS_TAG + "[MockTask] addOnFailureListener(OnFailureListener)");
            }

            if (!isSuccessful && thrownException != null) {
                triggerOnFailure();
            }
            return this;
        }

        public void setComplete(boolean complete) {
            isComplete = complete;
        }

        public void setSuccessful(boolean successful) {
            isSuccessful = successful;
        }

        public void setCanceled(boolean canceled) {
            isCanceled = canceled;
        }

        public void triggerOnSuccess() {
            if (onSuccessListener == null) {
                return;
            }

            isSuccessful = true;
            if (MockMessagesClient.debug) {
                System.out.println(CLASS_TAG + "[MockTask] triggerOnSuccess()");
            }
            onSuccessListener.onSuccess(null);
        }

        public void triggerOnComplete() {
            if (onCompleteListener == null) {
                return;
            }

            isComplete = true;
            if (MockMessagesClient.debug) {
                System.out.println(CLASS_TAG + "[MockTask] triggerOnComplete()");
            }
            onCompleteListener.onComplete(this);
        }

        public void triggerOnCanceled() {
            if (onCanceledListener == null) {
                return;
            }

            isCanceled = true;
            if (MockMessagesClient.debug) {
                System.out.println(CLASS_TAG + "[MockTask] triggerOnCanceled()");
            }
            onCanceledListener.onCanceled();
        }

        public void triggerOnFailure() {
            if (onFailureListener == null) {
                return;
            }

            if (MockMessagesClient.debug) {
                System.out.println(CLASS_TAG + "[MockTask] triggerOnFailure()");
            }
            Exception exception = thrownException == null ? new Exception("Random Test Exception") : thrownException;
            onFailureListener.onFailure(exception);
        }

        /**
         * Extra method to mock an exception.
         */
        void setException(Exception exception) {
            thrownException = exception;
        }

        /**
         * Easy GC.
         */
        void clear() {
            onSuccessListener = null;
            onCompleteListener = null;
            onCanceledListener = null;
            onFailureListener = null;
            thrownException = null;
        }

        @NonNull
        @Override
        public Task<Void> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
            throw new UnsupportedOperationException("Method addOnSuccessListener(Executor, OnSuccessListener) not supported");
        }

        @NonNull
        @Override
        public Task<Void> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
            throw new UnsupportedOperationException("Method addOnSuccessListener(Activity, OnSuccessListener) not supported");
        }

        @NonNull
        @Override
        public Task<Void> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
            throw new UnsupportedOperationException("Method addOnFailureListener(Executor, OnFailureListener) not supported");
        }

        @NonNull
        @Override
        public Task<Void> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
            throw new UnsupportedOperationException("Method addOnFailureListener(Activity, OnFailureListener) not supported");
        }
    }


    /**
     * MockMessagesClient may be initialized with an Activity or Context (or instrumentation Context).
     * Note that this will not reproduce any any Nearby notifications in the UI
     * as the real class does.
     */
    public MockMessagesClient(@NonNull Activity activity) {
        this(activity, false);
    }

    public MockMessagesClient(@NonNull Context context) {
        this(context, false);
    }

    @SuppressWarnings("unchecked")
    public MockMessagesClient(@NonNull Activity activity, boolean debug) {
        super(activity, mock(Api.class), null, Settings.DEFAULT_SETTINGS);
        init(debug);
    }

    @SuppressWarnings("unchecked")
    public MockMessagesClient(@NonNull Context context, boolean debug) {
        super(context, mock(Api.class), null, Settings.DEFAULT_SETTINGS);
        init(debug);
    }

    private void init(boolean debug) {
        MockMessagesClient.debug = debug;
        mClientState = new ClientState();
        statusCallbacks  = new HashMap<>();
        mPublishTask = new MockTask();
        mSubscribeTask = new MockTask();
        isClosed = false;
    }

    /**
     * NOTE: returns null Task<Void> if called after already published.
     */
    @Override
    public Task<Void> publish(@NonNull Message message) {
        return publish(message, null);
    }

    /**
     * NOTE: returns null Task<Void> if called after already published.
     */
    @Override
    public Task<Void> publish(@NonNull Message message, @Nullable PublishOptions publishOptions) {
        checkState();
        String msg = CLASS_TAG + " publish(Message" + (publishOptions == null ? "" : ", PublishOptions") + ")";
        if (isPublishing) {
            if (debug) {
                System.out.println(msg + " ignored, already publishing");
            }
            return null;
        } else if (debug) {
            System.out.println(msg);
        }

        mMessage = message;
        mPublishOptions = publishOptions;
        mClientState.setMessage(mMessage);
        mClientState.setPublishOptions(mPublishOptions);
        isPublishing = !hasPublishError;
        mPublishTask.setComplete(true);
        mPublishTask.setSuccessful(isPublishing);
        return mPublishTask;
    }

    /**
     * NOTE: returns null Task<Void> if called after already subscribed.
     */
    @Override
    public Task<Void> subscribe(@NonNull MessageListener messageListener) {
        return subscribe(messageListener, null);
    }

    /**
     * NOTE: returns null Task<Void> if called after already subscribed.
     */
    @Override
    public Task<Void> subscribe(@NonNull MessageListener messageListener, @Nullable SubscribeOptions subscribeOptions) {
        checkState();
        String message = CLASS_TAG + " subscribe(MessageListener" + (subscribeOptions == null ? "" : ", PublishOptions") + ")";
        if (isSubscribing) {
            System.out.println(message + " ignored, already subscribing");
            return null;
        } else if (debug) {
            System.out.println(message);
        }

        mMessageListener = messageListener;
        mSubscribeOptions = subscribeOptions;
        mClientState.setMessageListener(mMessageListener);
        mClientState.setSubscribeOptions(mSubscribeOptions);
        isSubscribing = !hasSubscribeError;
        mSubscribeTask.setComplete(true);
        mSubscribeTask.setSuccessful(isSubscribing);
        return mSubscribeTask;
    }

    /**
     * NOTE: returns null Task<Void> if called after already unpublished.
     */
    @Override
    public Task<Void> unpublish(@NonNull Message message) {
        checkState();
        String msg = CLASS_TAG + " unpublish(Message)";
        if ( ! isPublishing) {
            if (debug) {
                System.out.println(msg + " ignored, already not publishing");
            }
            return mPublishTask;
        } else if (debug) {
            System.out.println(msg);
        }

        mMessage = null;
        mPublishOptions = null;
        isPublishing = false;
        mPublishTask.setComplete(true);
        mPublishTask.setSuccessful(!hasUnpublishError);
        return mPublishTask;
    }

    /**
     * NOTE: returns null Task<Void> if called after already unsubscribed.
     */
    @Override
    public Task<Void> unsubscribe(@NonNull MessageListener messageListener) {
        checkState();
        String msg = CLASS_TAG + " unsubscribe(MessageListener)";
        if (!isSubscribing) {
            if (debug) {
                System.out.println(msg + " ignored, already unsubscribed");
            }
            return mSubscribeTask;
        } else if (debug) {
            System.out.println(msg);
        }
        mMessageListener = null;
        mSubscribeOptions = null;
        isSubscribing = false;
        mSubscribeTask.setComplete(true);
        mSubscribeTask.setSuccessful(!hasUnsubscribeError);
        return mSubscribeTask;
    }

    @Override
    public Task<Void> registerStatusCallback(@NonNull StatusCallback statusCallback) {
        int hashCode = statusCallback.hashCode();
        statusCallbacks.put(hashCode, statusCallback);
        if (mStatusTask == null) {
            mStatusTask = new MockTask();
        }
        return mStatusTask;
    }

    @Override
    public Task<Void> unregisterStatusCallback(@NonNull StatusCallback statusCallback) {
        int hashCode = statusCallback.hashCode();
        if ( ! statusCallbacks.containsKey(hashCode)) {
            return null;
        }

        statusCallbacks.remove(hashCode);
        return mStatusTask;
    }

    /**
     * Returns the {@link Message} sent after a call to publish(). Not publishing, no message.
     */
    public Message capturePublishMessage() {
        if (isPublishing) {
            if (debug) {
                System.out.println(CLASS_TAG + " capturePublishMessage() published Message found");
            }
            return mMessage;
        } else if (debug) {
            System.out.println(CLASS_TAG + " capturePublishMessage() client not publishing, no Message published");
        }

        return null;
    }

    /**
     * Mocks the {@link MessageListener} onFound(Message) callback.
     *
     * Optional delays parameter is delay in ms before onBleSignalChanged() call performed with corresponding Message.
     */
    public void mockMessageOnFound(List<Message> messages) {
        mockMessageOnFound(messages, null);
    }

    public void mockMessageOnFound(List<Message> messages, List<Integer> delays) {
        checkState();
        if (debug) {
            System.out.println(CLASS_TAG + " mockMessageOnFound(List<Message>" + (delays == null ? "" : ", List<Integer>") + ")");
        }
        mockMessageListener(
                ListenerCallbackMethod.ON_FOUND,
                messages,
                delays,
                null,
                null
        );
    }

    /**
     * Mocks the {@link MessageListener} onLost(Message) callback.
     *
     * Optional delays parameter is delay in ms before onBleSignalChanged() call performed with corresponding Message.
     */
    public void mockMessageOnLost(List<Message> messages) {
        mockMessageOnLost(messages, null);
    }

    public void mockMessageOnLost(List<Message> messages, List<Integer> delays) {
        checkState();
        if (debug) {
            System.out.println(CLASS_TAG + " mockMessageOnLost(List<Message>" + (delays == null ? "" : ", List<Integer>") + ")");
        }
        mockMessageListener(
                ListenerCallbackMethod.ON_LOST,
                messages,
                delays,
                null,
                null
        );
    }

    /**
     * Mocks the {@link MessageListener} onDistanceChanged(Message, Distance) callback.
     *
     * Parameter distances is set of {@link Distance} objects corresponding to the set in messages parameter
     *
     * Optional delays parameter is delay in ms before onBleSignalChanged() call performed with corresponding Message.
     */
    public void mockMessageOnDistanceChanged(List<Message> messages, List<Distance> distances) {
        mockMessageOnDistanceChanged(messages, distances, null);
    }

    public void mockMessageOnDistanceChanged(List<Message> messages, List<Distance> distances,
            List<Integer> delays) {
        checkState();
        if (debug) {
            String message = CLASS_TAG + " mockMessageOnDistanceChanged(List<Message>, List<Distance>";
            message += (delays == null ? "" : ", List<Integer>") + ")";
            System.out.println(message);
        }
        mockMessageListener(
                ListenerCallbackMethod.ON_DISTANCE_CHANGED,
                messages,
                delays,
                distances,
                null
        );
    }

    /**
     * Mocks the {@link MessageListener} onBleSignalChanged(Message, BleSignal) callback.
     *
     * Parameter distances is set of {@link BleSignal} objects corresponding to the set in messages parameter
     *
     * Optional delays parameter is delay in ms before onBleSignalChanged() call performed with corresponding Message.
     */
    public void mockMessageOnBleSignalChanged(List<Message> messages, List<BleSignal> signals) {
        mockMessageOnBleSignalChanged(messages, signals, null);
    }

    public void mockMessageOnBleSignalChanged(List<Message> messages, List<BleSignal> signals,
            List<Integer> delays) {
        checkState();
        if (debug) {
            String message = CLASS_TAG + " mockMessageOnBleSignalChanged(List<Message>, List<BleSignal>";
            message += (delays == null ? "" : ", List<Integer>") + ")";
            System.out.println(message);
        }
        mockMessageListener(
                ListenerCallbackMethod.ON_BLE_SIGNAL_CHANGED,
                messages,
                delays,
                null,
                signals
        );
    }

    /**
     * Mocks StatusCallback.onPermissionChanged() from StatusCallback passed to registerStatusCallback().
     *
     * NOTE: Will not mock any other resulting implementations from the MessageClient
     * (e.g. will still publish and subscribe after onPermissionChanged)
     */
    public void mockStatusCallbackOnPermissionChanged(boolean hasPermission) {
        checkState();
        String message = CLASS_TAG + " mockStatusCallbackOnPermissionChanged(" + (hasPermission ? "true" : "false") + ")";

        if (statusCallbacks == null || statusCallbacks.size() == 0) {
            if (debug) {
                System.out.println(message + " ignored, no StatusCallback registered in client");
            }
            return;
        }

        if (debug) {
            System.out.println(message);
        }

        Set<Integer> hashCodes = statusCallbacks.keySet();
        for (int hashCode : hashCodes) {
            StatusCallback callback = statusCallbacks.get(hashCode);
            if (callback != null) {
                callback.onPermissionChanged(hasPermission);
            }
        }

        if (!hasPermission) {
            isPublishing = false;
            isSubscribing = false;
        }
    }

    /**
     * Needs to be called before publish() to invoke mock exception.
     */
    public void mockPublishFailure(@NonNull Exception exception) {
        checkState();
        mPublishTask.setException(exception);
        hasPublishError = true;
    }

    /**
     * Needs to be called before subscribe() to invoke mock exception.
     */
    public void mockSubscribeFailure(@NonNull Exception exception) {
        checkState();
        mSubscribeTask.setException(exception);
        hasSubscribeError = true;
    }

    /**
     * Needs to be called before unpublish() to invoke mock exception.
     */
    public void mockUnpublishFailure(@NonNull Exception exception) {
        checkState();
        mPublishTask.setException(exception);
        hasUnpublishError = true;
    }

    /**
     * Needs to be called before unsubscribe() to invoke mock exception.
     */
    public void mockUnsubscribeFailure(@NonNull Exception exception) {
        checkState();
        mSubscribeTask.setException(exception);
        hasUnsubscribeError = true;
    }

    /**
     * Mocks the PublishCallback.onExpired() passed in a PublishOptions in a publish() call-
     */
    public void mockPublishExpired() {
        checkState();
        if (mPublishOptions != null) {
            PublishCallback callback = mPublishOptions.getCallback();
            if (callback != null) {
                if (debug) {
                    System.out.println(CLASS_TAG + " mockPublishExpired()");
                }

                unpublish(mMessage);
                callback.onExpired();
            } else if (debug) {
                System.out.println(CLASS_TAG + " mockPublishExpired() IGNORED, no PublishCallback set in PublishOptions from call to publish()");
            }
        } else if (debug) {
            System.out.println(CLASS_TAG + " mockPublishExpired() IGNORED, PublishOptions from call to publish() null");
        }
    }

    /**
     * Mocks the SubscribeCallback.onExpired() passed in a SubscribeOptions in a subscribe() call-
     */
    public void mockSubscribeExpired() {
        checkState();
        if (mSubscribeOptions != null) {
            SubscribeCallback callback = mSubscribeOptions.getCallback();
            if (callback != null) {
                if (debug) {
                    System.out.println(CLASS_TAG + " mockSubscribeExpired()");
                }

                unsubscribe(mMessageListener);
                callback.onExpired();
            } else if (debug) {
                System.out.println(CLASS_TAG + " mockSubscribeExpired() IGNORED, no SubscribeCallback set in PublishOptions from call to subscribe()");
            }
        } else if (debug) {
            System.out.println(CLASS_TAG + " mockSubscribeExpired() IGNORED, SubscribeOptions from call to subscribe() null");
        }
    }

    /**
     * Resets this mock MessageClient to it's original state in the case a mock exception was thrown
     * or unsubscribe/unpublish was called on this object.
     */
    public void reset() {
        checkState();
        boolean isResetable = false;

        if (mPublishTask != null) {
            if (isPublishing) {
                System.out.println(CLASS_TAG + " reset() client already publishing");
            } else {
                if (debug) {
                    System.out.println(CLASS_TAG + " reset() publish resumed");
                }
                mMessage = mClientState.getMessage();
                mPublishOptions = mClientState.getPublishOptions();
                isPublishing = true;
            }
            isResetable = true;
        }

        if (mSubscribeTask != null) {
            if (isSubscribing) {
                System.out.println(CLASS_TAG + " reset() client already subscribing");
            } else {
                if (debug) {
                    System.out.println(CLASS_TAG + " reset() subscribe resumed");
                }
                mMessageListener = mClientState.getMessageListener();
                mSubscribeOptions = mClientState.getSubscribeOptions();
                isSubscribing = true;
            }
            isResetable = true;
        }

        if (!isResetable) {
            System.out.println(CLASS_TAG + " reset() IGNORED, publish() or subscribe() must be called first");
        }
    }

    public boolean isPublishing() {
        return isPublishing;
    }

    public boolean isSubscribing() {
        return isSubscribing;
    }

    /**
     * If MessageClient closed then mock functions can no longer be performed.
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * GC fun.
     */
    public void close() {
        if (debug) {
            System.out.println(CLASS_TAG + " close() mock functions no longer active");
        }

        statusCallbacks.clear();
        statusCallbacks = null;
        mMessage = null;
        mMessageListener = null;
        mPublishOptions = null;
        mSubscribeOptions = null;

        if (mStatusTask != null) {
            mStatusTask.clear();
            mStatusTask = null;
        }
        if (mPublishTask != null) {
            mPublishTask.clear();
            mPublishTask = null;
        }
        if (mSubscribeTask != null) {
            mSubscribeTask.clear();
            mSubscribeTask = null;
        }

        mClientState.clear();
        mClientState = null;

        isPublishing = false;
        isSubscribing = false;
        isClosed = true;
    }

    private void mockMessageListener(@NonNull ListenerCallbackMethod method, List<Message> messages,
            List<Integer> delays, List<Distance> distances, List<BleSignal> signals) {
        String error = "";
        if (mMessageListener == null) {
            error += "subscribe() must be called before calling mockMessageListener()\n";
        }
        if (!isSubscribing) {
            error += "MessagesClient is currently not subscribing\n";
        }
        if (messages == null || messages.size() == 0) {
            error += "List<Message> messages parameter must not be empty or null\n";
        }
        if (method == ListenerCallbackMethod.ON_DISTANCE_CHANGED && (distances == null ||
                distances.size() == 0)) {
            error += "List<Distance> distances parameter must not be empty or null\n";
        }
        if (method == ListenerCallbackMethod.ON_BLE_SIGNAL_CHANGED && (signals == null ||
                signals.size() == 0)) {
            error += "List<BleSignal> distances parameter must not be empty or null\n";
        }
        if (!error.equals("")) {
            throw new RuntimeException(error);
        }

        int delay = 0;
        int delayCount = delays == null ? 0 : delays.size();
        boolean hasDelay = delayCount > 0;
        int count = messages.size();

        for (int i=0; i < count; i++) {
            if (hasDelay && i < delayCount) {
                delay = delays.get(i);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }

            Message message = messages.get(i);
            int index;
            String caller;
            switch (method) {
                case ON_FOUND:
                    caller = "mockMessageOnFound";
                    break;
                case ON_LOST:
                    caller = "mockMessageOnLost";
                    break;
                case ON_DISTANCE_CHANGED:
                    caller = "mockMessageOnDistanceChanged";
                    break;
                case ON_BLE_SIGNAL_CHANGED:
                    caller = "mockMessageOnBleSignalChanged";
                    break;
                default:
                    caller = "[unknown method]";
            }

            String emitMessage = CLASS_TAG + " " + caller + ": " + message + " emitted";
            String ignoreMessage = CLASS_TAG + " " + caller + ": " + message + " IGNORED, message client not subscribing";
            switch (method) {
                case ON_FOUND:
                    if (mMessageListener != null) {
                        if (debug) {
                            System.out.println(emitMessage);
                        }
                        mMessageListener.onFound(message);
                    } else if (debug) {
                        System.out.println(ignoreMessage);
                    }
                    break;
                case ON_LOST:
                    if (mMessageListener != null) {
                        if (debug) {
                            System.out.println(emitMessage);
                        }
                        mMessageListener.onLost(message);
                    } else if (debug) {
                        System.out.println(ignoreMessage);
                    }
                    break;
                case ON_DISTANCE_CHANGED:
                    if (mMessageListener != null) {
                        if (debug) {
                            System.out.println(emitMessage);
                        }
                        index = i < distances.size() ? i : distances.size() - 1;
                        Distance distance = distances.get(index);
                        mMessageListener.onDistanceChanged(message, distance);
                    } else if (debug) {
                        System.out.println(ignoreMessage);
                    }
                    break;
                case ON_BLE_SIGNAL_CHANGED:
                    if (mMessageListener != null) {
                        if (debug) {
                            System.out.println(emitMessage);
                        }
                        index = i < signals.size() ? i : signals.size() - 1;
                        BleSignal signal = signals.get(index);
                        mMessageListener.onBleSignalChanged(message, signal);
                    } else if (debug) {
                        System.out.println(ignoreMessage);
                    }
                    break;
                default:
                    throw new RuntimeException(method + " not supported");
            }
        }
    }

    /**
     * Makes sure the close() method wasn't called prior to a mock method, otherwise
     * throws an exception.
     */
    private void checkState() {
        if (isClosed) {
            throw new IllegalStateException(CLASS_TAG + " close() already called on MockMessagesClient");
        }
    }

    @Override
    public Task<Void> subscribe(@NonNull PendingIntent pendingIntent, @NonNull SubscribeOptions subscribeOptions) {
        throw new UnsupportedOperationException("Method subscribe(PendingIntent, SubscribeOptions) not supported");
    }

    @Override
    public Task<Void> subscribe(@NonNull PendingIntent pendingIntent) {
        throw new UnsupportedOperationException("Method subscribe(PendingIntent) not supported");
    }

    @Override
    public Task<Void> unsubscribe(@NonNull PendingIntent pendingIntent) {
        throw new UnsupportedOperationException("Method unsubscribe(PendingIntent) not supported");
    }

    @Override
    public void handleIntent(@NonNull Intent intent, @NonNull MessageListener messageListener) {
        throw new UnsupportedOperationException("Method handleIntent(Intent, MessageListener) not supported");
    }
}
