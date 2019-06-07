package com.cccdlabs.sarva.presentation.presenters;

import android.content.Context;

import com.cccdlabs.sarva.R;
import com.cccdlabs.sarva.domain.network.exception.NetworkConnectionException;
import com.cccdlabs.sarva.data.repository.sample.GizmoRepository;
import com.cccdlabs.sarva.domain.interactors.sample.SampleDisplayUseCase;
import com.cccdlabs.sarva.domain.model.sample.Gizmo;
import com.cccdlabs.sarva.presentation.di.components.DaggerTestPresenterComponent;
import com.cccdlabs.sarva.presentation.di.components.TestPresenterComponent;
import com.cccdlabs.sarva.presentation.di.modules.TestPresenterModule;
import com.cccdlabs.sarva.presentation.mappers.sample.GizmoUiModelMapper;
import com.cccdlabs.sarva.presentation.model.sample.GizmoUiModel;
import com.cccdlabs.sarva.presentation.views.MainView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import io.reactivex.Single;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class MainPresenterTest {

    private static final String ERROR_MESSAGE = "Test error message. Relax.";

    @Mock SampleDisplayUseCase mockUseCase;
    @Mock GizmoRepository mockRepository;


    private MainPresenter mainPresenter;
    private MainPresenter mainPresenterSpy;
    private MainView mainViewSpy;
    private Context context;

    class StubMainView implements MainView {
        @Override
        public void showGizmos(List<GizmoUiModel> gizmos) {

        }

        @Override
        public void showLoading() {

        }

        @Override
        public void hideLoading() {

        }

        @Override
        public void showRetry() {

        }

        @Override
        public void hideRetry() {

        }

        @Override
        public void showError(String message) {

        }

        @Override
        public Context context() {
            return null;
        }
    }

    @Before
    public void setUp() throws Exception {
        mainViewSpy = spy(new StubMainView());
        mainPresenter = new MainPresenter(mockRepository, mainViewSpy);
        context = ApplicationProvider.getApplicationContext();
        TestPresenterComponent mainComponent = DaggerTestPresenterComponent.builder()
                .testPresenterModule(new TestPresenterModule(context))
                .build();
        mainComponent.inject(mainPresenter);
        mainPresenterSpy = spy(mainPresenter);
        mockUseCase = mainComponent.sampleDisplayUseCase();
    }

    @Test
    public void MainPresenterTest_shouldInvokeUseCase() throws Exception {
        List<Gizmo> list = new ArrayList<>();
        list.add(new Gizmo());
        List<GizmoUiModel> uiModelList = GizmoUiModelMapper.fromDomainModel(list);
        Single<List<Gizmo>> single = Single.just(list);
        Single<List<GizmoUiModel>> mappedSingle = Single.just(uiModelList);
        mappedSingle.subscribeWith(mainPresenter.getShowSampleDisplayObserver());
        when(mockUseCase.execute(null)).thenReturn(single);

        mainPresenter.getAllGizmos();
        verify(mockUseCase).execute(null);
        verify(mainViewSpy).showGizmos(uiModelList);
    }

    @Test
    public void MainPresenterTest_handlesRestError() throws Exception {
        NetworkConnectionException error = new NetworkConnectionException();
        Single<List<Gizmo>> single = Single.error(error);
        Single<List<GizmoUiModel>> mappedSingle = Single.error(error);
        MainPresenter.ShowSampleDisplayObserver observerSpy = spy(mainPresenterSpy.getShowSampleDisplayObserver());
        mappedSingle.subscribeWith(observerSpy);
        when(mockUseCase.execute(null)).thenReturn(single);

        mainPresenterSpy.getAllGizmos();
        verify(observerSpy).onError(error);
        verify(mainPresenterSpy).onError(context.getString(R.string.error_network_connection));
    }

    @After
    public void tearDown() throws Exception {
        mockUseCase = null;
        mockRepository = null;
        mainViewSpy = null;
        mainPresenter = null;
        mainPresenterSpy = null;
    }
}
