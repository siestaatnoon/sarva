package com.cccdlabs.sarva.data.p2p.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.cccdlabs.sarva.data.p2p.nearby.base.AbstractNearbyPartnerEmitter;
import com.cccdlabs.sarva.domain.p2p.exception.PartnerException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryException;

import java.util.List;
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
    private Message mMessage;
    private MessageListener mMessageListener;
    private PublishOptions mPublishOptions;
    private SubscribeOptions mSubscribeOptions;
    private MockTask mPublishTask;
    private MockTask mSubscribeTask;
    private Throwable mockException;
    private int mockExceptionIndex;
    private boolean isPublishing;
    private boolean isSubscribing;
    private boolean isClosed;
    private boolean debug;

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
            return this;
        }

        @NonNull
        @Override
        public Task<Void> addOnCompleteListener(@NonNull OnCompleteListener<Void> onCompleteListener) {
            this.onCompleteListener = onCompleteListener;
            return this;
        }

        @NonNull
        @Override
        public Task<Void> addOnCanceledListener(@NonNull OnCanceledListener onCanceledListener) {
            this.onCanceledListener = onCanceledListener;
            return this;
        }

        @NonNull
        @Override
        public Task<Void> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
            return this;
        }

        /**
         * Extra method to mock an exception.
         */
        void setException(Exception exception) {
            thrownException = exception;
        }

        /**
         * Extra method to mock OnCompleteListener.
         */
        void setComplete(boolean complete) {
            if (onCompleteListener != null) {
                onCompleteListener.onComplete(this);
            }
            isComplete = complete;
        }

        /**
         * Extra method to mock OnSuccessListener.
         */
        void setSuccessful(boolean successful) {
            if (onSuccessListener != null) {
                onSuccessListener.onSuccess(null);
            }
            isSuccessful = successful;
        }

        /**
         * Extra method to mock OnCanceledListener.
         */
        void setCanceled(boolean canceled) {
            if (onCanceledListener != null) {
                onCanceledListener.onCanceled();
            }
            isCanceled = canceled;
        }

        /**
         * Easy GC.
         */
        void clear() {
            onSuccessListener = null;
            onCompleteListener = null;
            onCanceledListener = null;
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
     * Note that this will not reproduce any any automated Nearby notifications in the UI
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
        this.debug = debug;
        mClientState = new ClientState();
        isClosed = false;
    }

    @SuppressWarnings("unchecked")
    public MockMessagesClient(@NonNull Context context, boolean debug) {
        super(context, mock(Api.class), null, Settings.DEFAULT_SETTINGS);
        this.debug = debug;
        mClientState = new ClientState();
        isClosed = false;
    }

    /**
     * NOTE: returns null Task<Void> if called more than once.
     */
    @Override
    public Task<Void> publish(@NonNull Message message) {
        return publish(message, null);
    }

    /**
     * NOTE: returns null Task<Void> if called more than once.
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
        isPublishing = true;
        mPublishTask = new MockTask();
        mClientState.setMessage(mMessage);
        mClientState.setPublishOptions(mPublishOptions);
        return mPublishTask;
    }

    /**
     * NOTE: returns null Task<Void> if called more than once.
     */
    @Override
    public Task<Void> subscribe(@NonNull MessageListener messageListener) {
        return subscribe(messageListener, null);
    }

    /**
     * NOTE: returns null Task<Void> if called more than once.
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
        isSubscribing = true;
        mSubscribeTask = new MockTask();
        mClientState.setMessageListener(mMessageListener);
        mClientState.setSubscribeOptions(mSubscribeOptions);
        return mSubscribeTask;
    }

    @Override
    public Task<Void> unpublish(@NonNull Message message) {
        checkState();
        if (debug) {
            System.out.println(CLASS_TAG + " unpublish(Message)");
        }
        mMessage = null;
        mPublishOptions = null;
        setPublishing(false, true, true, true);
        return mPublishTask;
    }

    @Override
    public Task<Void> unsubscribe(@NonNull MessageListener messageListener) {
        checkState();
        if (debug) {
            System.out.println(CLASS_TAG + " unsubscribe(MessageListener)");
        }
        mMessageListener = null;
        mSubscribeOptions = null;
        setSubscribing(false, true, true, true);
        return null;
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
     * Mocks an exception thrown in any of the mock onFound, onLost, onDistanceChanged and
     * onBleSignalChanged calls.
     *
     * Optional index parameter is index, within the set of messages used in the mock callbacks,
     * to throw the exception.
     */
    public void mockMessageCallbackException(@NonNull Throwable throwable) {
        mockMessageCallbackException(throwable, -1);
    }

    public void mockMessageCallbackException(@NonNull Throwable throwable, int index) {
        checkState();
        mockException = throwable;
        mockExceptionIndex = index;
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
                setPublishing(false, true, false, false);
                callback.onExpired();
                mPublishOptions = null;
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
                setSubscribing(false, true, false, false);
                callback.onExpired();
                mSubscribeOptions = null;
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
                setPublishing(true, false, false, false);
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
                setSubscribing(true, false, false, false);
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
        mMessage = null;
        mMessageListener = null;
        mPublishOptions = null;
        mSubscribeOptions = null;
        mockException = null;

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
        if (mockException != null && mMessageListener instanceof AbstractNearbyPartnerEmitter.PartnerMessageListener) {
            if (mockExceptionIndex < 0 || mockExceptionIndex >= count) {
                // generate random index to throw exception
                // NOTE: index can be equal to messages.size() to throw at end of emission
                mockExceptionIndex = (int) Math.floor(count * Math.random());
            }
        } else {
            mockExceptionIndex = -1;
        }

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

            if (i == mockExceptionIndex) {
                boolean isPassThroughException = mockException instanceof PartnerException ||
                        mockException instanceof RepositoryException;
                String exceptionMessage = CLASS_TAG + " " + caller + ": {" + mockException.toString() + "} ";
                exceptionMessage += isPassThroughException ? "emitted" : "thrown";
                System.out.println(exceptionMessage);
                if (isPassThroughException) {
                    ((AbstractNearbyPartnerEmitter.PartnerMessageListener)mMessageListener).continueWithError(mockException);
                } else {
                    ((AbstractNearbyPartnerEmitter.PartnerMessageListener)mMessageListener).cancelWithError(mockException);
                }
                continue;
            }

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

    /**
     * Sets the publishing state and mocks the Task<Void> callbacks returned from publish().
     */
    private void setPublishing(boolean isPublishing, boolean taskCanceled, boolean taskComplete,
            boolean taskSuccessful) {
        if (mPublishTask != null) {
            mPublishTask.setCanceled(taskCanceled);
            mPublishTask.setComplete(taskComplete);
            mPublishTask.setSuccessful(taskSuccessful);
        }
        this.isPublishing = isPublishing;
    }

    /**
     * Sets the subscribing state and mocks the Task<Void> callbacks returned from subscribe().
     */
    private void setSubscribing(boolean isSubscribing, boolean taskCanceled, boolean taskComplete,
            boolean taskSuccessful) {
        if (mSubscribeTask != null) {
            mSubscribeTask.setCanceled(taskCanceled);
            mSubscribeTask.setComplete(taskComplete);
            mSubscribeTask.setSuccessful(taskSuccessful);
        }
        this.isSubscribing = isSubscribing;
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
    public Task<Void> registerStatusCallback(@NonNull StatusCallback statusCallback) {
        throw new UnsupportedOperationException("Method registerStatusCallback(StatusCallback) not supported");
    }

    @Override
    public Task<Void> unregisterStatusCallback(@NonNull StatusCallback statusCallback) {
        throw new UnsupportedOperationException("Method unregisterStatusCallback(StatusCallback) not supported");
    }

    @Override
    public void handleIntent(@NonNull Intent intent, @NonNull MessageListener messageListener) {
        throw new UnsupportedOperationException("Method handleIntent(Intent, MessageListener) not supported");
    }
}
