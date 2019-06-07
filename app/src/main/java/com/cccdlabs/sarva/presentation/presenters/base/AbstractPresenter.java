package com.cccdlabs.sarva.presentation.presenters.base;

import com.cccdlabs.sarva.domain.repository.base.Repository;
import com.cccdlabs.sarva.presentation.views.base.BaseView;

/**
 * Abstraction for {@link Presenter} interface.
 *
 * @param <M> The POJO model type from <code>domain</code> package
 * @author Johnny Spence
 * @version 1.0.0
 */
abstract public class AbstractPresenter<M> implements Presenter {

    /**
     * {@link Repository} used by the Presenter.
     */
    private Repository<M> mRepository;

    /**
     * {@link BaseView} used by the Presenter.
     */
    private BaseView mView;

    /**
     * Constructor.
     *
     * @param repository    {@link Repository} used by this Presenter
     * @param view          {@link BaseView} used by this Presenter
     */
    public AbstractPresenter(final Repository<M> repository, final BaseView view) {
        mRepository = repository;
        mView = view;
    }

    /**
     * Returns the repository of this Presenter.
     *
     * @return The respository
     */
    protected Repository<M> getRepository() {
        return mRepository;
    }

    /**
     * Returns the implementing view of this Presenter.
     *
     * @return The view
     */
    protected BaseView getView() {
        return mView;
    }
}
