package com.cccdlabs.sarva.presentation.presenters;

import android.content.Context;

import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.executor.ExecutorThread;
import com.cccdlabs.sarva.domain.executor.MainThread;
import com.cccdlabs.sarva.domain.interactors.partners.PartnerBroadcastUseCase;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.presentation.presenters.base.AbstractPresenter;
import com.cccdlabs.sarva.presentation.presenters.observers.PresenterDisposableSubscriber;
import com.cccdlabs.sarva.presentation.views.MainView;

import javax.inject.Inject;

import timber.log.Timber;

public class MainPresenter extends AbstractPresenter<Partner> {

    @Inject PartnerBroadcastUseCase mUseCase;
    @Inject ExecutorThread mExecutorThread;
    @Inject MainThread mMainThread;
    @Inject Context mContext;

    private PresenterDisposableSubscriber<PartnerResult> mSubscriber;
    private boolean hasStartedPublishing;

    class PartnerBroadcastSubscriber extends PresenterDisposableSubscriber<PartnerResult> {

        private MainView mainView;

        PartnerBroadcastSubscriber() {
            super(mContext, MainPresenter.this);
            mainView = (MainView) getView();
        }

        @Override
        public void onNext(final PartnerResult result) {
            if (result.hasError()) {
                Timber.e(result.getException());
                return;
            }
            boolean isPublishing = result.getPublishStatus() == PartnerResult.PublishStatus.PUBLISHING;
            mainView.onBroadcastUpdate(isPublishing);
        }

        @Override
        public void onError(Throwable t) {
            super.onError(t);
            endBroadcast();
        }

        @Override
        public void onComplete() {
            mainView.onBroadcastUpdate(false);
            mainView = null;
        }
    }

    @Inject
    public MainPresenter(final PartnerRepository repository, final MainView view) {
        super(repository, view);
    }

    public void startBroadcast() {
        if (hasStartedPublishing) {
            return;
        }

        mSubscriber = getPartnerBroadcastSubscriber();
        mUseCase.emit(null)
                .subscribeOn(mExecutorThread.getScheduler())
                .observeOn(mMainThread.getScheduler())
                .onBackpressureBuffer(100)
                .subscribe(mSubscriber);
        hasStartedPublishing = true;
    }

    public void endBroadcast() {
        if (mSubscriber != null && ! mSubscriber.isDisposed()) {
            mSubscriber.onComplete();
            mSubscriber.dispose();
            mSubscriber = null;
        }
    }

    @Override
    public void resume() {
        if (hasStartedPublishing) {
            mUseCase.resumeEmitterSource();
        } else {
            startBroadcast();
        }
    }

    @Override
    public void pause() {
        mUseCase.pauseEmitterSource();
    }

    @Override
    public void stop() {
        mUseCase.pauseEmitterSource();
    }

    @Override
    public void destroy() {
        endBroadcast();
        mUseCase = null;
        mExecutorThread = null;
        mMainThread = null;
        mContext = null;

    }

    @Override
    public void onError(final String message) {
        getView().showError(message);
    }

    protected PresenterDisposableSubscriber<PartnerResult> getPartnerBroadcastSubscriber() {
        return new PartnerBroadcastSubscriber();
    }
}
