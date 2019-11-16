package com.cccdlabs.sarva.presentation.presenters.partners;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.executor.ExecutorThread;
import com.cccdlabs.sarva.domain.executor.MainThread;
import com.cccdlabs.sarva.domain.interactors.partners.AddPartnerUseCase;
import com.cccdlabs.sarva.domain.interactors.partners.DeletePartnerUseCase;
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
import com.cccdlabs.sarva.presentation.views.partners.PartnerCheckView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import timber.log.Timber;

public class PartnerCheckPresenter extends AbstractPresenter<Partner> {

    @Inject AddPartnerUseCase mAddPartnerUseCase;
    @Inject DeletePartnerUseCase mDeletePartnerUseCase;
    @Inject PartnerCheckUseCase mPartnerCheckUseCase;
    @Inject GetAllPartnersUseCase mGetAllPartnersUseCase;
    @Inject SetPartnerActiveUseCase mSetPartnerActiveUseCase;
    @Inject ExecutorThread mExecutorThread;
    @Inject MainThread mMainThread;
    @Inject Context mContext;

    private PresenterSingleObserver<PartnerUiModel> mAddPartnerObserver;
    private PresenterSingleObserver<Integer> mDeletePartnerObserver;
    private PresenterSingleObserver<List<PartnerUiModel>> mShowPartnersObserver;
    private PresenterDisposableSubscriber<PartnerResult> mPartnerCheckSubscriber;
    private PresenterCompletableObserver mSetPartnerActiveObserver;
    private CompositeDisposable mDisposables;
    private List<PartnerUiModel> mPartners;
    private boolean isPublishing;

    class PartnerCheckSubscriber extends PresenterDisposableSubscriber<PartnerResult> {

        private PartnerCheckView view;

        PartnerCheckSubscriber() {
            super(PartnerCheckPresenter.this);
            view = (PartnerCheckView) getView();
        }

        @Override
        public void onNext(final PartnerResult result) {
            if (result.hasError()) {
                Timber.e(result.getException());
                return;
            }

            PartnerUiModel uiModel = PartnerUiModelMapper.fromDomainModel(result.getPartner());
            addOrUpdatePartner(uiModel);
            mPartners = PartnerUiModelUtils.sortByActive(mPartners, true);
            view.showPartners(mPartners);
        }

        @Override
        public void onError(Throwable t) {
            super.onError(t);
            stop();
        }

        @Override
        public void onComplete() {
            view = null;
        }

        protected void addOrUpdatePartner(@NonNull PartnerUiModel partner) {
            for (int i=0; i < mPartners.size(); i++) {
                PartnerUiModel uiModel = mPartners.get(i);
                if (uiModel.getUuid().equals(partner.getUuid())) {
                    mPartners.set(i, partner);
                    return;
                }
            }
            mPartners.add(partner);
        }
    }

    class AddPartnerObserver  extends PresenterSingleObserver<PartnerUiModel> {

        private PartnerCheckView view;

        AddPartnerObserver() {
            super(PartnerCheckPresenter.this);
            view = (PartnerCheckView) getView();
        }

        @Override
        public void onSuccess(final PartnerUiModel partner) {
            view.onPartnerAdded(partner);
            view.hideLoading();
        }
    }

    class DeletePartnerObserver  extends PresenterSingleObserver<Integer> {

        private PartnerCheckView view;
        private PartnerUiModel uiModel;

        DeletePartnerObserver(@NonNull PartnerUiModel uiModel) {
            super(PartnerCheckPresenter.this);
            view = (PartnerCheckView) getView();
            this.uiModel = uiModel;
        }

        @Override
        public void onSuccess(final Integer numDeleted) {
            view.onPartnerDeleted(uiModel, numDeleted == 1);
            view.hideLoading();
        }
    }

    class ShowPartnersObserver extends PresenterSingleObserver<List<PartnerUiModel>> {

        private PartnerCheckView view;

        ShowPartnersObserver() {
            super(PartnerCheckPresenter.this);
            view = (PartnerCheckView) getView();
        }

        @Override
        public void onSuccess(final List<PartnerUiModel> partners) {
            mPartners = PartnerUiModelUtils.sortByActive(partners, true);
            view.showPartners(mPartners);
            view.hideLoading();

            // Start partner check emitter after
            // partners retrieved for the first time,
            // NOTE: will only start emitter once and
            // successive calls are ignored
            startEmitter();
        }
    }

    class SetPartnerActiveObserver extends PresenterCompletableObserver {

        private PartnerCheckView view;
        private PartnerUiModel uiModel;

        SetPartnerActiveObserver(@NonNull PartnerUiModel uiModel) {
            super(PartnerCheckPresenter.this);
            view = (PartnerCheckView) getView();
            this.uiModel = uiModel;
        }

        @Override
        public void onComplete() {
            view.onPartnerSetActive(uiModel);
            view.hideLoading();
        }
    }

    @Inject
    public PartnerCheckPresenter(final PartnerRepository repository, final PartnerCheckView view) {
        super(repository, view);
        mDisposables = new CompositeDisposable();
        mPartners = new ArrayList<>();
    }

    @SuppressWarnings("all")
    public void addPartner(@NonNull PartnerUiModel uiModel) {
        if (mAddPartnerObserver != null) {
            mDisposables.remove(mAddPartnerObserver);
        }

        getView().showLoading();
        mAddPartnerObserver = getAddPartnerObserver();
        mAddPartnerUseCase.execute(PartnerUiModelMapper.toDomainModel(uiModel))
                .map(new Function<Partner, PartnerUiModel>() {
                    @Override
                    public PartnerUiModel apply(Partner partner) throws Exception {
                        return PartnerUiModelMapper.fromDomainModel(partner);
                    }
                })
                .subscribeOn(mExecutorThread.getScheduler())
                .observeOn(mMainThread.getScheduler())
                .subscribeWith(mAddPartnerObserver);
        mDisposables.add(mAddPartnerObserver);
    }

    @SuppressWarnings("all")
    public void deletePartner(@NonNull PartnerUiModel uiModel) {
        if (mDeletePartnerObserver != null) {
            mDisposables.remove(mDeletePartnerObserver);
        }

        getView().showLoading();
        mDeletePartnerObserver = getDeletePartnerObserver(uiModel);
        mDeletePartnerUseCase.execute(PartnerUiModelMapper.toDomainModel(uiModel))
                .subscribeOn(mExecutorThread.getScheduler())
                .observeOn(mMainThread.getScheduler())
                .subscribeWith(mDeletePartnerObserver);
        mDisposables.add(mDeletePartnerObserver);
    }

    @SuppressWarnings("all")
    public void retrieveAllPartners() {
        if (mPartners != null) {
            mPartners.clear();
        }
        if (mShowPartnersObserver != null) {
            mDisposables.remove(mShowPartnersObserver);
        }

        getView().showLoading();
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
        if (mSetPartnerActiveObserver != null) {
            mDisposables.remove(mSetPartnerActiveObserver);
        }

        getView().showLoading();
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
    public void pause() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        stopEmitter();
        isPublishing = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        stopEmitter();
        if (mPartners != null) {
            mPartners.clear();
        }
        if (mPartners != null) {
            mDisposables.clear();
        }
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
        isPublishing = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError(final Throwable throwable) {
        getView().showError(throwable);
    }

    protected void startEmitter() {
        if (isPublishing) {
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
        isPublishing = true;
    }

    protected void stopEmitter() {
        if (mPartnerCheckSubscriber != null && !mPartnerCheckSubscriber.isDisposed()) {
            mPartnerCheckSubscriber.onComplete();
            mDisposables.remove(mPartnerCheckSubscriber); // also calls dispose() on subscriber
            mPartnerCheckSubscriber = null;
            ((PartnerCheckView)getView()).onEmissionStopped();
        }
    }

    protected PresenterSingleObserver<PartnerUiModel> getAddPartnerObserver() {
        return new AddPartnerObserver();
    }

    protected PresenterSingleObserver<Integer> getDeletePartnerObserver(@NonNull PartnerUiModel uiModel) {
        return new DeletePartnerObserver(uiModel);
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