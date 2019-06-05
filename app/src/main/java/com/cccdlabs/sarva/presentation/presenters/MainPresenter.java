package com.cccdlabs.sarva.presentation.presenters;

import android.content.Context;

import com.cccdlabs.sarva.data.repository.sample.GizmoRepository;
import com.cccdlabs.sarva.domain.executor.ExecutorThread;
import com.cccdlabs.sarva.domain.executor.MainThread;
import com.cccdlabs.sarva.domain.interactors.sample.SampleDisplayUseCase;
import com.cccdlabs.sarva.domain.model.sample.Gizmo;
import com.cccdlabs.sarva.presentation.mappers.sample.GizmoUiModelMapper;
import com.cccdlabs.sarva.presentation.model.sample.GizmoUiModel;
import com.cccdlabs.sarva.presentation.presenters.base.AbstractPresenter;
import com.cccdlabs.sarva.presentation.presenters.observers.PresenterObserver;
import com.cccdlabs.sarva.presentation.views.MainView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Function;

public class MainPresenter extends AbstractPresenter<Gizmo> {

    @Inject SampleDisplayUseCase mUseCase;
    @Inject ExecutorThread mExecutorThread;
    @Inject MainThread mMainThread;
    @Inject Context mContext;

    class ShowSampleDisplayObserver extends PresenterObserver<List<GizmoUiModel>> {

        private ShowSampleDisplayObserver() {
            super(mContext, MainPresenter.this);
        }

        @Override
        public void onSuccess(final List<GizmoUiModel> items) {
            ((MainView) getView()).showGizmos(items);
        }
    }

    public MainPresenter(final GizmoRepository repository, final MainView view) {
        super(repository, view);
    }

    public void getAllGizmos() {
        mUseCase.execute(null)
                .map(new Function<List<Gizmo>, List<GizmoUiModel>>() {
                    @Override
                    public List<GizmoUiModel> apply(List<Gizmo> items) throws Exception {
                        return GizmoUiModelMapper.fromDomainModel(items);
                    }
                })
                .subscribeOn(mExecutorThread.getScheduler())
                .observeOn(mMainThread.getScheduler())
                .subscribe(getShowSampleDisplayObserver());
    }

    ShowSampleDisplayObserver getShowSampleDisplayObserver() {
        return new ShowSampleDisplayObserver();
    }

    @Override
    public void resume() {
        getAllGizmos();
    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void onError(final String message) {
        getView().showError(message);
    }
}
