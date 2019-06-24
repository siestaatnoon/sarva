package com.cccdlabs.sarva.presentation.presenters.partners;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.executor.ExecutorThread;
import com.cccdlabs.sarva.domain.executor.MainThread;
import com.cccdlabs.sarva.domain.interactors.partners.GetAllPartnersUseCase;
import com.cccdlabs.sarva.domain.interactors.partners.PartnerCheckUseCase;
import com.cccdlabs.sarva.domain.interactors.partners.SetPartnerActiveUseCase;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.presentation.mappers.partners.PartnerUiModelMapper;
import com.cccdlabs.sarva.presentation.model.partners.PartnerUiModel;
import com.cccdlabs.sarva.presentation.model.utils.PartnerUiModelUtils;
import com.cccdlabs.sarva.presentation.presenters.base.AbstractPresenter;
import com.cccdlabs.sarva.presentation.presenters.observers.PresenterCompletableObserver;
import com.cccdlabs.sarva.presentation.presenters.observers.PresenterDisposableSubscriber;
import com.cccdlabs.sarva.presentation.presenters.observers.PresenterSingleObserver;
import com.cccdlabs.sarva.presentation.views.MainView;
import com.cccdlabs.sarva.presentation.views.PartnerCheckView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import timber.log.Timber;

public class PartnerCheckPresenter extends AbstractPresenter<Partner> {

    @Inject PartnerCheckUseCase mPartnerCheckUseCase;
    @Inject GetAllPartnersUseCase mGetAllPartnersUseCase;
    @Inject SetPartnerActiveUseCase mSetPartnerActiveUseCase;
    @Inject ExecutorThread mExecutorThread;
    @Inject MainThread mMainThread;
    @Inject Context mContext;

    private CompositeDisposable mDisposables;
    private PresenterSingleObserver<List<PartnerUiModel>> mShowPartnersObserver;
    private PresenterDisposableSubscriber<PartnerResult> mPartnerCheckSubscriber;
    private PresenterCompletableObserver mSetPartnerActiveObserver;
    private List<PartnerUiModel> mPartners;
    private boolean hasStartedEmitter;

    class PartnerCheckSubscriber extends PresenterDisposableSubscriber<PartnerResult> {

        private PartnerCheckView view;

        private PartnerCheckSubscriber() {
            super(mContext, PartnerCheckPresenter.this);
            view = (PartnerCheckView) getView();
        }

        @Override
        public void onNext(final PartnerResult result) {
            if (result.hasError()) {
                Timber.e(result.getException());
                return;
            }

            PartnerUiModel uiModel = PartnerUiModelMapper.fromDomainModel(result.getPartner());
            mPartners.add(uiModel);
            mPartners = PartnerUiModelUtils.sortByActive(mPartners, true);
            view.showPartners(mPartners);
        }

        @Override
        public void onError(Throwable t) {
            super.onError(t);
            stopEmitter();
        }

        @Override
        public void onComplete() {
            view = null;
        }
    }

    class ShowPartnersObserver extends PresenterSingleObserver<List<PartnerUiModel>> {

        private PartnerCheckView view;

        private ShowPartnersObserver() {
            super(mContext, PartnerCheckPresenter.this);
            view = (PartnerCheckView) getView();
        }

        @Override
        public void onSuccess(final List<PartnerUiModel> partners) {
            mPartners = PartnerUiModelUtils.sortByActive(partners, true);
            view.showPartners(mPartners);

            // Start partner check emitter only after
            // current partners retrieved
            startEmitter();
        }
    }

    class SetPartnerActiveObserver extends PresenterCompletableObserver {

        private PartnerCheckView view;
        private PartnerUiModel uiModel;

        private SetPartnerActiveObserver(@NonNull PartnerUiModel uiModel) {
            super(mContext, PartnerCheckPresenter.this);
            view = (PartnerCheckView) getView();
            this.uiModel = uiModel;
        }

        @Override
        public void onComplete() {
            view.onSetPartnerActive(uiModel);
        }
    }

    public PartnerCheckPresenter(final PartnerRepository repository, final MainView view) {
        super(repository, view);
        mDisposables = new CompositeDisposable();
    }

    @SuppressWarnings("all")
    public void retrieveAllPartners() {
        if (mPartners != null) {
            mPartners.clear();
            mDisposables.remove(mShowPartnersObserver);
        }

        mPartners = new ArrayList<>();
        mShowPartnersObserver = getShowPartnersObserver();
        mGetAllPartnersUseCase.execute(null)
                .map(new Function<List<Partner>, List<PartnerUiModel>>() {
                    @Override
                    public List<PartnerUiModel> apply(List<Partner> partners) throws Exception {
                        return PartnerUiModelMapper.fromDomainModel(partners);
                    }
                })
                .subscribeOn(mExecutorThread.getScheduler())
                .observeOn(mMainThread.getScheduler())
                .subscribeWith(mShowPartnersObserver);
        mDisposables.add(mShowPartnersObserver);
    }

    @SuppressWarnings("all")
    public void setPartnerActive(@NonNull PartnerUiModel uiModel, boolean isActive) {
        uiModel.setActive(isActive);
        mSetPartnerActiveObserver = getSetPartnerActiveObserver(uiModel);
        mSetPartnerActiveUseCase.complete(PartnerUiModelMapper.toDomainModel(uiModel))
                .subscribeOn(mExecutorThread.getScheduler())
                .observeOn(mMainThread.getScheduler())
                .subscribeWith(mSetPartnerActiveObserver);
        mDisposables.add(mSetPartnerActiveObserver);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resume() {
        retrieveAllPartners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pause() {
        mPartnerCheckUseCase.pauseEmitterSource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        stopEmitter();
        if (mPartners != null) {
            mPartners.clear();
        }

        mDisposables.clear();
        mPartners = null;
        mDisposables = null;
        mShowPartnersObserver = null;
        mPartnerCheckSubscriber = null;
        mSetPartnerActiveObserver = null;
        mPartnerCheckUseCase = null;
        mGetAllPartnersUseCase = null;
        mSetPartnerActiveUseCase = null;
        mExecutorThread = null;
        mMainThread = null;
        mContext = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError(final String message) {
        getView().showError(message);
    }

    protected void startEmitter() {
        if (hasStartedEmitter) {
            mPartnerCheckUseCase.resumeEmitterSource();
            return;
        }

        mPartnerCheckSubscriber = getPartnerCheckSubscriber();
        mPartnerCheckUseCase.emit(null)
                .subscribeOn(mExecutorThread.getScheduler())
                .observeOn(mMainThread.getScheduler())
                .onBackpressureBuffer(100)
                .subscribe(mPartnerCheckSubscriber);
        ((PartnerCheckView)getView()).onEmissionStarted();
        mDisposables.add(mPartnerCheckSubscriber);
        hasStartedEmitter = true;
    }

    protected void stopEmitter() {
        if (mPartnerCheckSubscriber != null && !mPartnerCheckSubscriber.isDisposed()) {
            mPartnerCheckSubscriber.onComplete();
            mDisposables.remove(mPartnerCheckSubscriber);
            mPartnerCheckSubscriber.dispose();
            mPartnerCheckSubscriber = null;
            ((PartnerCheckView)getView()).onEmissionStopped();
        }
    }

    protected PresenterSingleObserver<List<PartnerUiModel>> getShowPartnersObserver() {
        return new ShowPartnersObserver();
    }

    protected PresenterDisposableSubscriber<PartnerResult> getPartnerCheckSubscriber() {
        return new PartnerCheckSubscriber();
    }

    protected PresenterCompletableObserver getSetPartnerActiveObserver(@NonNull PartnerUiModel uiModel) {
        return new SetPartnerActiveObserver(uiModel);
    }
}