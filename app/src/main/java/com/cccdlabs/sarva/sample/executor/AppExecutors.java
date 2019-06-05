package com.cccdlabs.sarva.sample.executor;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private static volatile AppExecutors sAppExecutor;
    private final Executor mThreadPool;
    private final Executor mMainThread;

    private AppExecutors(Executor threadPool, Executor mainThread) {
        this.mThreadPool = threadPool == null
                ? Executors.newCachedThreadPool()
                : threadPool;

        this.mMainThread = mainThread == null
                ? new MainThreadExecutor()
                : mainThread;
    }

    private AppExecutors() {
        this(null, null);
    }

    public static AppExecutors getInstance() {
        if (sAppExecutor == null) {
            sAppExecutor = new AppExecutors();
        }
        return sAppExecutor;
    }

    public Executor getMainExecutor() {
        return mMainThread;
    }

    public Executor getThreadExecutor() {
        return mThreadPool;
    }

    public void threadExecute(Runnable runnable) {
        mThreadPool.execute(runnable);
    }

    public void mainExecute(Runnable runnable) {
        mMainThread.execute(runnable);
    }

    private class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
