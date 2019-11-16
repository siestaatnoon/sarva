package com.cccdlabs.sarva.presentation.presenters;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.cccdlabs.sarva.domain.interactors.base.MockPartnerEmitter;
import com.cccdlabs.sarva.domain.interactors.partners.PartnerBroadcastUseCase;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.presentation.di.components.DaggerTestAppComponent;
import com.cccdlabs.sarva.presentation.di.components.DaggerTestMainComponent;
import com.cccdlabs.sarva.presentation.di.components.TestAppComponent;
import com.cccdlabs.sarva.presentation.di.components.TestMainComponent;
import com.cccdlabs.sarva.presentation.di.modules.TestActivityModule;
import com.cccdlabs.sarva.presentation.di.modules.TestAppModule;
import com.cccdlabs.sarva.presentation.di.modules.TestDataModule;
import com.cccdlabs.sarva.presentation.di.modules.TestMainModule;
import com.cccdlabs.sarva.presentation.di.modules.TestP2pModule;
import com.cccdlabs.sarva.presentation.ui.activities.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class MainPresenterTest {

    private MainPresenter mPresenter;
    private PartnerBroadcastUseCase mPartnerBroadcastUseCase;
    private MainActivity mMainActivityMock;

    @Before
    public void setUp() throws Exception {
        mMainActivityMock = mock(MainActivity.class);
        Context appContext = ApplicationProvider.getApplicationContext();
        TestAppComponent appComponent = DaggerTestAppComponent.builder()
                .testAppModule(new TestAppModule(appContext))
                .testDataModule(new TestDataModule(appContext))
                .build();

        TestMainComponent mainComponent = DaggerTestMainComponent.builder()
                .testAppComponent(appComponent)
                .testActivityModule(new TestActivityModule(mMainActivityMock))
                .testP2pModule(new TestP2pModule())
                .testMainModule(new TestMainModule(mMainActivityMock))
                .build();
        mainComponent.inject(mMainActivityMock);
        mPresenter = mainComponent.mainPresenter();
        mPartnerBroadcastUseCase = mainComponent.partnerBroadcastUseCase();
    }

    @Test
    public void shouldStartBroadcast() throws Exception {
        MainPresenter mainPresenterSpy = spy(mPresenter);
        PartnerBroadcastUseCase useCaseSpy = spy(mPartnerBroadcastUseCase);
        List<PartnerResult> partnerResults = new ArrayList<>(0);

        // borrowed here from UseCase tests, serves well!
        MockPartnerEmitter emitter = new MockPartnerEmitter(partnerResults);

        when(useCaseSpy.emit(null)).thenReturn(emitter.getPartnerFlowable());

        mainPresenterSpy.startEmitter();

        // TODO: figure out why this verifies but useCaseSpy.pauseEmitterSource() does not
        // It's known that useCaseSpy is a different object than what's contained in MainPresenter
        // but WTF?
        verify(useCaseSpy, times(1)).emit(null);
    }

    @Test
    public void shouldEndBroadcast() throws Exception {
        MainPresenter mainPresenterSpy = spy(mPresenter);
        PartnerBroadcastUseCase useCaseSpy = spy(mPartnerBroadcastUseCase);
        MainPresenter.PartnerBroadcastSubscriber subscriberSpy = spy(mainPresenterSpy.new PartnerBroadcastSubscriber());
        List<PartnerResult> partnerResults = new ArrayList<>(0);

        // borrowed here from UseCase tests, serves well!
        MockPartnerEmitter emitter = new MockPartnerEmitter(partnerResults);

        when(useCaseSpy.emit(null)).thenReturn(emitter.getPartnerFlowable());
        when(mainPresenterSpy.getPartnerBroadcastSubscriber()).thenReturn(subscriberSpy);

        mainPresenterSpy.startEmitter();
        mainPresenterSpy.stopEmitter();
        verify(subscriberSpy, times(1)).onComplete();
        verify(subscriberSpy, times(1)).dispose();
    }

    @Test
    public void shouldResumeAndStartBroadcast() throws Exception {
        MainPresenter mainPresenterSpy = spy(mPresenter);
        PartnerBroadcastUseCase useCaseSpy = spy(mPartnerBroadcastUseCase);
        List<PartnerResult> partnerResults = new ArrayList<>(0);

        // borrowed here from UseCase tests, serves well!
        MockPartnerEmitter emitter = new MockPartnerEmitter(partnerResults);

        when(useCaseSpy.emit(null)).thenReturn(emitter.getPartnerFlowable());

        mainPresenterSpy.resume();
        verify(mainPresenterSpy, times(1)).startEmitter();
        verify(useCaseSpy, times(1)).emit(null);
    }

    @Test
    public void shouldStop() throws Exception {
        MainPresenter mainPresenterSpy = spy(mPresenter);
        mainPresenterSpy.stop();
        verify(mainPresenterSpy, times(1)).stop();
        verify(mainPresenterSpy, times(1)).stopEmitter();
    }

    @Test
    public void shouldDestroy() throws Exception {
        MainPresenter mainPresenterSpy = spy(mPresenter);
        mainPresenterSpy.destroy();
        verify(mainPresenterSpy, times(1)).destroy();
        verify(mainPresenterSpy, times(1)).stopEmitter();
        verifyNoMoreInteractions(mainPresenterSpy);
    }

    @Test
    public void shouldOnError() throws Exception {
        MainPresenter mainPresenterSpy = spy(mPresenter);
        Exception error = new Exception("Test error");

        mainPresenterSpy.onError(error);
        verify(mainPresenterSpy, times(1)).onError(error);
        verify(mMainActivityMock, times(1)).showError(error);
    }

    @Test
    public void shouldPartnerBroadcastSubscriberOnNextReturnIsBroadcasting() throws Exception {
        List<PartnerResult> partnerResults = new ArrayList<>(2);
        partnerResults.add(new PartnerResult(true));
        partnerResults.add(new PartnerResult(true));

        MainPresenter.PartnerBroadcastSubscriber subscriberSpy =
                (MainPresenter.PartnerBroadcastSubscriber) spy(mPresenter.getPartnerBroadcastSubscriber());
        MockPartnerEmitter emitter = new MockPartnerEmitter(partnerResults);
        Flowable<PartnerResult> flowable = emitter.getPartnerFlowable();
        flowable.subscribe(subscriberSpy);

        verify(subscriberSpy, times(2)).onNext(any(PartnerResult.class));
        verify(mMainActivityMock, times(2)).onBroadcastUpdate(true);
        verify(subscriberSpy, times(1)).onComplete();
        verify(mMainActivityMock, times(1)).onBroadcastUpdate(false);
    }

    @Test
    public void shouldPartnerBroadcastSubscriberOnNextReturnIsNotBroadcasting() throws Exception {
        List<PartnerResult> partnerResults = new ArrayList<>(2);
        partnerResults.add(new PartnerResult(false));
        partnerResults.add(new PartnerResult(false));

        MainPresenter.PartnerBroadcastSubscriber subscriberSpy =
                (MainPresenter.PartnerBroadcastSubscriber) spy(mPresenter.getPartnerBroadcastSubscriber());
        MockPartnerEmitter emitter = new MockPartnerEmitter(partnerResults);
        Flowable<PartnerResult> flowable = emitter.getPartnerFlowable();
        flowable.subscribe(subscriberSpy);

        verify(subscriberSpy, times(2)).onNext(any(PartnerResult.class));
        verify(subscriberSpy, times(1)).onComplete();
        verify(mMainActivityMock, times(3)).onBroadcastUpdate(false);
    }

    @Test
    public void shouldPartnerBroadcastSubscriberOnNextErrorDoNothing() throws Exception {
        MainPresenter.PartnerBroadcastSubscriber subscriberSpy =
                (MainPresenter.PartnerBroadcastSubscriber) spy(mPresenter.getPartnerBroadcastSubscriber());
        Exception exception = new Exception("Test error");
        PartnerResult result = new PartnerResult(exception);
        MockPartnerEmitter emitter = new MockPartnerEmitter(result);
        Flowable<PartnerResult> flowable = emitter.getPartnerFlowable();
        flowable.subscribe(subscriberSpy);

        verify(subscriberSpy, times(1)).onNext(result);
        verify(subscriberSpy, times(1)).onComplete();
        verify(mMainActivityMock, times(1)).onBroadcastUpdate(any(Boolean.class));
    }

    @Test
    public void shouldPartnerBroadcastSubscriberOnError() throws Exception {
        MainPresenter mainPresenterSpy = spy(mPresenter);
        MainPresenter.PartnerBroadcastSubscriber subscriberSpy =
                (MainPresenter.PartnerBroadcastSubscriber) spy(mainPresenterSpy.getPartnerBroadcastSubscriber());
        Exception exception = new Exception("Test error");
        MockPartnerEmitter emitter = new MockPartnerEmitter(exception);
        Flowable<PartnerResult> flowable = emitter.getPartnerFlowable();
        flowable.subscribe(subscriberSpy);

        verify(subscriberSpy, times(1)).onError(exception);
        verify(mainPresenterSpy, times(1)).onError(any(Exception.class));
        verify(mainPresenterSpy, times(1)).stopEmitter();
        verify(subscriberSpy, never()).onComplete();
    }

    @After
    public void tearDown() throws Exception {
        mPresenter = null;
        mPartnerBroadcastUseCase = null;
        mMainActivityMock = null;
    }
}
