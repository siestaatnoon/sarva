package com.cccdlabs.sarva.presentation.presenters.partners;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.cccdlabs.sarva.domain.interactors.base.MockPartnerEmitter;
import com.cccdlabs.sarva.domain.interactors.partners.AddPartnerUseCase;
import com.cccdlabs.sarva.domain.interactors.partners.DeletePartnerUseCase;
import com.cccdlabs.sarva.domain.interactors.partners.GetAllPartnersUseCase;
import com.cccdlabs.sarva.domain.interactors.partners.PartnerCheckUseCase;
import com.cccdlabs.sarva.domain.interactors.partners.SetPartnerActiveUseCase;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.presentation.di.components.DaggerTestAppComponent;
import com.cccdlabs.sarva.presentation.di.components.DaggerTestPartnerComponent;
import com.cccdlabs.sarva.presentation.di.components.TestAppComponent;
import com.cccdlabs.sarva.presentation.di.components.TestPartnerComponent;
import com.cccdlabs.sarva.presentation.di.modules.TestActivityModule;
import com.cccdlabs.sarva.presentation.di.modules.TestAppModule;
import com.cccdlabs.sarva.presentation.di.modules.TestDataModule;
import com.cccdlabs.sarva.presentation.di.modules.TestP2pModule;
import com.cccdlabs.sarva.presentation.di.modules.TestPartnerModule;
import com.cccdlabs.sarva.presentation.model.partners.PartnerUiModel;
import com.cccdlabs.sarva.presentation.ui.activities.partners.PartnerCheckActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class PartnerCheckPresenterTest {

    private static final String TEST_UUID_1 = "d4f74431-2f49-4acb-8f81-57c2ed67047a";
    private static final String TEST_UUID_2 = "cc908cbf-6a49-4646-86a9-12bc61132e5d";

    private PartnerCheckPresenter mPresenter;
    private AddPartnerUseCase mAddPartnerUseCase;
    private DeletePartnerUseCase mDeletePartnerUseCase;
    private PartnerCheckUseCase mPartnerCheckUseCase;
    private GetAllPartnersUseCase mGetAllPartnersUseCase;
    private SetPartnerActiveUseCase mSetPartnerActiveUseCase;
    private PartnerCheckActivity mActivityMock;

    @Before
    public void setUp() throws Exception {
        mActivityMock = mock(PartnerCheckActivity.class);
        Context appContext = ApplicationProvider.getApplicationContext();
        TestAppComponent appComponent = DaggerTestAppComponent.builder()
                .testAppModule(new TestAppModule(appContext))
                .testDataModule(new TestDataModule(appContext))
                .build();

        TestPartnerComponent partnerComponent = DaggerTestPartnerComponent.builder()
                .testAppComponent(appComponent)
                .testActivityModule(new TestActivityModule(mActivityMock))
                .testP2pModule(new TestP2pModule())
                .testPartnerModule(new TestPartnerModule(mActivityMock))
                .build();

        mPresenter = partnerComponent.partnerCheckPresenter();
        mAddPartnerUseCase = partnerComponent.addPartnerUseCase();
        mDeletePartnerUseCase = partnerComponent.deletePartnerUseCase();
        mPartnerCheckUseCase = partnerComponent.partnerCheckUseCase();
        mGetAllPartnersUseCase = partnerComponent.getAllPartnersUseCase();
        mSetPartnerActiveUseCase = partnerComponent.setPartnerActiveUseCase();
    }

    @Test
    public void shouldAddPartner() throws Exception {
        AddPartnerUseCase useCaseSpy = spy(mAddPartnerUseCase);
        mPresenter.mAddPartnerUseCase = useCaseSpy;
        Partner model = new Partner();
        Single<Partner> single = Single.just(model);
        when(useCaseSpy.execute(model)).thenReturn(single);

        mPresenter.addPartner(new PartnerUiModel());
        verify(useCaseSpy, times(1)).execute(any(Partner.class));
    }

    @Test
    public void shouldDeletePartner() throws Exception {
        DeletePartnerUseCase useCaseSpy = spy(mDeletePartnerUseCase);
        mPresenter.mDeletePartnerUseCase = useCaseSpy;
        Partner model = new Partner();
        Single<Integer> single = Single.just(1);
        when(useCaseSpy.execute(model)).thenReturn(single);

        mPresenter.deletePartner(new PartnerUiModel());
        verify(useCaseSpy, times(1)).execute(any(Partner.class));
    }

    @Test
    public void shouldRetrieveAllPartners() throws Exception {
        GetAllPartnersUseCase useCaseSpy = spy(mGetAllPartnersUseCase);
        mPresenter.mGetAllPartnersUseCase = useCaseSpy;
        List<Partner> models = new ArrayList<>(0);
        models.add(new Partner());
        Single<List<Partner>> single = Single.just(models);
        when(useCaseSpy.execute(null)).thenReturn(single);

        mPresenter.retrieveAllPartners();
        verify(useCaseSpy, times(1)).execute(null);
    }

    @Test
    public void shouldSetPartnerActive() throws Exception {
        SetPartnerActiveUseCase useCaseSpy = spy(mSetPartnerActiveUseCase);
        mPresenter.mSetPartnerActiveUseCase = useCaseSpy;
        PartnerUiModel uiModel = new PartnerUiModel();

        mPresenter.setPartnerActive(uiModel, true);
        verify(useCaseSpy, times(1)).complete(any(Partner.class));
    }

    @Test
    public void shouldStartEmitter() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        PartnerCheckUseCase useCaseSpy = spy(mPartnerCheckUseCase);
        List<PartnerResult> partnerResults = new ArrayList<>(0);
        MockPartnerEmitter emitter = new MockPartnerEmitter(partnerResults);
        when(useCaseSpy.emit(null)).thenReturn(emitter.getPartnerFlowable());

        presenterSpy.startEmitter();
        verify(useCaseSpy, times(1)).emit(null);
    }

    @Test
    public void shouldStopEmitter() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        PartnerCheckUseCase useCaseSpy = spy(mPartnerCheckUseCase);
        PartnerCheckPresenter.PartnerCheckSubscriber subscriberSpy = spy(presenterSpy.new PartnerCheckSubscriber());
        List<PartnerResult> partnerResults = new ArrayList<>(0);
        MockPartnerEmitter emitter = new MockPartnerEmitter(partnerResults);
        when(useCaseSpy.emit(null)).thenReturn(emitter.getPartnerFlowable());
        when(presenterSpy.getPartnerCheckSubscriber()).thenReturn(subscriberSpy);

        presenterSpy.startEmitter();
        presenterSpy.stopEmitter();
        verify(subscriberSpy, times(1)).onComplete();
        verify(subscriberSpy, times(1)).dispose();
    }

    @Test
    public void shouldResume() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        presenterSpy.resume();
        verify(presenterSpy, times(1)).retrieveAllPartners();
    }

    @Test
    public void shouldStop() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        presenterSpy.stop();
        verify(presenterSpy, times(1)).stop();
        verify(presenterSpy, times(1)).stopEmitter();
    }

    @Test
    public void shouldDestroy() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        presenterSpy.destroy();
        verify(presenterSpy, times(1)).destroy();
        verify(presenterSpy, times(1)).stopEmitter();
        verifyNoMoreInteractions(presenterSpy);
    }

    @Test
    public void shouldOnError() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        Exception error = new Exception("Test error");

        presenterSpy.onError(error);
        verify(presenterSpy, times(1)).onError(error);
        verify(mActivityMock, times(1)).showError(error);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSubscriberOnNextReturnPartners() throws Exception {
        List<PartnerResult> partnerResults = new ArrayList<>(2);
        Partner partner = new Partner();
        partner.setUuid(TEST_UUID_1);
        partnerResults.add(new PartnerResult(partner));
        partner = new Partner();
        partner.setUuid(TEST_UUID_2);
        partnerResults.add(new PartnerResult(partner));

        PartnerCheckPresenter.PartnerCheckSubscriber subscriberSpy =
                (PartnerCheckPresenter.PartnerCheckSubscriber) spy(mPresenter.getPartnerCheckSubscriber());
        MockPartnerEmitter emitter = new MockPartnerEmitter(partnerResults);
        Flowable<PartnerResult> flowable = emitter.getPartnerFlowable();
        doNothing().when(subscriberSpy).addOrUpdatePartner(any(PartnerUiModel.class));

        flowable.subscribe(subscriberSpy);
        verify(subscriberSpy, times(2)).onNext(any(PartnerResult.class));
        verify(subscriberSpy, times(1)).onComplete();
        verify(mActivityMock, times(2)).showPartners(any(List.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldPartnerCheckSubscriberOnNextErrorDoNothing() throws Exception {
        PartnerCheckPresenter.PartnerCheckSubscriber subscriberSpy =
                (PartnerCheckPresenter.PartnerCheckSubscriber) spy(mPresenter.getPartnerCheckSubscriber());
        Exception exception = new Exception("Test error");
        PartnerResult result = new PartnerResult(exception);
        MockPartnerEmitter emitter = new MockPartnerEmitter(result);
        Flowable<PartnerResult> flowable = emitter.getPartnerFlowable();
        flowable.subscribe(subscriberSpy);

        verify(subscriberSpy, times(1)).onNext(result);
        verify(subscriberSpy, times(1)).onComplete();
        verify(mActivityMock, never()).showPartners(any(List.class));
    }

    @Test
    public void shouldPartnerCheckSubscriberOnError() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        PartnerCheckPresenter.PartnerCheckSubscriber subscriberSpy =
                (PartnerCheckPresenter.PartnerCheckSubscriber) spy(presenterSpy.getPartnerCheckSubscriber());
        Exception exception = new Exception("Test error");
        MockPartnerEmitter emitter = new MockPartnerEmitter(exception);
        Flowable<PartnerResult> flowable = emitter.getPartnerFlowable();
        flowable.subscribe(subscriberSpy);

        verify(subscriberSpy, times(1)).onError(exception);
        verify(presenterSpy, times(1)).onError(any(Exception.class));
        verify(presenterSpy, times(1)).stopEmitter();
        verify(subscriberSpy, never()).onComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldAddPartnerObserverOnSuccessReturnPartnerUiModelAndResetPartners() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        PartnerCheckPresenter.AddPartnerObserver observerSpy =
                (PartnerCheckPresenter.AddPartnerObserver) spy(presenterSpy.getAddPartnerObserver());
        PartnerUiModel uiModel = new PartnerUiModel();
        uiModel.setUuid(TEST_UUID_1);
        Single<PartnerUiModel> single = Single.just(uiModel);
        doNothing().when(presenterSpy).retrieveAllPartners();
        single.subscribe(observerSpy);

        verify(observerSpy, times(1)).onSuccess(uiModel);
        verify(mActivityMock, times(1)).onPartnerAdded(uiModel);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldAddPartnerObserverOnError() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        PartnerCheckPresenter.AddPartnerObserver subscriberSpy =
                (PartnerCheckPresenter.AddPartnerObserver) spy(presenterSpy.getAddPartnerObserver());
        Exception exception = new Exception("Test error");
        Single<PartnerUiModel> single = Single.error(exception);
        single.subscribe(subscriberSpy);

        verify(subscriberSpy, times(1)).onError(exception);
        verify(presenterSpy, times(1)).onError(any(Exception.class));
        verify(subscriberSpy, never()).onSuccess(any(PartnerUiModel.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldDeletePartnerObserverOnSuccessReturnPartnerUiModelAndResetPartners() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        PartnerUiModel uiModel = new PartnerUiModel();
        uiModel.setUuid(TEST_UUID_1);
        PartnerCheckPresenter.DeletePartnerObserver observerSpy =
                (PartnerCheckPresenter.DeletePartnerObserver) spy(presenterSpy.getDeletePartnerObserver(uiModel));
        Single<Integer> single = Single.just(1);
        doNothing().when(presenterSpy).retrieveAllPartners();

        single.subscribe(observerSpy);
        verify(observerSpy, times(1)).onSuccess(1);
        verify(mActivityMock, times(1)).onPartnerDeleted(uiModel, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldDeletePartnerObserverOnError() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        PartnerUiModel uiModel = new PartnerUiModel();
        uiModel.setUuid(TEST_UUID_1);
        PartnerCheckPresenter.DeletePartnerObserver subscriberSpy =
                (PartnerCheckPresenter.DeletePartnerObserver) spy(presenterSpy.getDeletePartnerObserver(uiModel));
        Exception exception = new Exception("Test error");
        Single<Integer> single = Single.error(exception);
        single.subscribe(subscriberSpy);

        verify(subscriberSpy, times(1)).onError(exception);
        verify(presenterSpy, times(1)).onError(any(Exception.class));
        verify(subscriberSpy, never()).onSuccess(any(Integer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldShowPartnersObserverOnSuccessReturnPartnerUiModelsAndStartEmitter() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        PartnerCheckPresenter.ShowPartnersObserver observerSpy =
                (PartnerCheckPresenter.ShowPartnersObserver) spy(presenterSpy.getShowPartnersObserver());
        List<PartnerUiModel> uiModels = new ArrayList<>(2);
        uiModels.add(new PartnerUiModel());
        uiModels.add(new PartnerUiModel());
        Single<List<PartnerUiModel>> single = Single.just(uiModels);
        doNothing().when(presenterSpy).startEmitter();
        single.subscribe(observerSpy);

        verify(observerSpy, times(1)).onSuccess(uiModels);
        verify(mActivityMock, times(1)).showPartners(any(List.class));
        verify(presenterSpy, times(1)).startEmitter();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldShowPartnersObserverOnError() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        PartnerCheckPresenter.ShowPartnersObserver subscriberSpy =
                (PartnerCheckPresenter.ShowPartnersObserver) spy(presenterSpy.getShowPartnersObserver());
        Exception exception = new Exception("Test error");
        Single<List<PartnerUiModel>> single = Single.error(exception);
        single.subscribe(subscriberSpy);

        verify(subscriberSpy, times(1)).onError(exception);
        verify(presenterSpy, times(1)).onError(any(Exception.class));
        verify(subscriberSpy, never()).onSuccess(any(List.class));
    }

    @Test
    public void shouldSetPartnerActiveObserverComplete() throws Exception {
        PartnerUiModel uiModel = new PartnerUiModel();
        PartnerCheckPresenter.SetPartnerActiveObserver completableSpy =
                (PartnerCheckPresenter.SetPartnerActiveObserver) spy(mPresenter.getSetPartnerActiveObserver(uiModel));
        Completable completable = Completable.complete();
        completable.subscribe(completableSpy);

        verify(completableSpy, times(1)).onComplete();
        verify(mActivityMock, times(1)).onPartnerSetActive(uiModel);
    }

    @Test
    public void shouldSetPartnerActiveObserverOnError() throws Exception {
        PartnerCheckPresenter presenterSpy = spy(mPresenter);
        PartnerUiModel uiModel = new PartnerUiModel();
        PartnerCheckPresenter.SetPartnerActiveObserver completableSpy =
                (PartnerCheckPresenter.SetPartnerActiveObserver) spy(presenterSpy.getSetPartnerActiveObserver(uiModel));
        Exception exception = new Exception("Test error");
        Completable completable = Completable.error(exception);
        completable.subscribe(completableSpy);

        verify(completableSpy, times(1)).onError(exception);
        verify(presenterSpy, times(1)).onError(any(Exception.class));
        verify(completableSpy, never()).onComplete();
    }

    @After
    public void tearDown() throws Exception {
        mPartnerCheckUseCase = null;
        mGetAllPartnersUseCase = null;
        mSetPartnerActiveUseCase = null;
        mPresenter = null;
        mActivityMock = null;
    }
}
