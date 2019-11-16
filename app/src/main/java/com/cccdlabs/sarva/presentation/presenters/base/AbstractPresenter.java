package com.cccdlabs.sarva.presentation.presenters.base;

import com.cccdlabs.sarva.domain.repository.base.Repository;
import com.cccdlabs.sarva.presentation.views.base.BaseView;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

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
     * For RxJava default error handler. Calls the <code>onError(Throwable)</code> in subclass.
     */
    private class DefaultErrorHandler implements Consumer<Throwable> {
        @Override
        public void accept(Throwable throwable) throws Exception {
            AbstractPresenter.this.onError(throwable);
        }
    }

    /**
     * Constructor.
     *
     * @param repository    {@link Repository} used by this Presenter
     * @param view          {@link BaseView} used by this Presenter
     */
    public AbstractPresenter(final Repository<M> repository, final BaseView view) {
        mRepository = repository;
        mView = view;

        // Set the default RxJava error handler for errors w/o subscribers
        Consumer<Throwable> errorHandler = getDefaultErrorHandler();
        RxJavaPlugins.setErrorHandler(errorHandler);
    }

    /**
     * Returns the repository of this Presenter.
     *
     * @return The respository
     */
    public Repository<M> getRepository() {
        return mRepository;
    }

    /**
     * Returns the implementing view of this Presenter.
     *
     * @return The view
     */
    public BaseView getView() {
        return mView;
    }

    /**
     * Returns the default RxJava error handler.
     *
     * @return The error handler
     */
    protected Consumer<Throwable> getDefaultErrorHandler() {
        return new DefaultErrorHandler();
    }
}
