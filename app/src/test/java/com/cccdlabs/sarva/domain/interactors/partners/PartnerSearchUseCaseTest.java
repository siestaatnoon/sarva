package com.cccdlabs.sarva.domain.interactors.partners;

import com.cccdlabs.sarva.data.p2p.nearby.PartnerSearchEmitter;
import com.cccdlabs.sarva.domain.interactors.base.MockPartnerEmitter;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartnerSearchUseCaseTest {

    private PartnerSearchEmitter mEmitterMock;

    @Before
    public void setUp() throws Exception {
        mEmitterMock = mock(PartnerSearchEmitter.class);
    }

    @Test
    public void shouldEmitPartners() throws Exception {
        List<PartnerResult> partnerResults = new ArrayList<>();
        Partner partner = new Partner();
        partner.setUuid("bc317a3f-099f-42c1-bc13-d4aa9998c681");
        partnerResults.add(new PartnerResult(partner));
        partner = new Partner();
        partner.setUuid("61c83f23-13cf-40b4-80c8-3cc5a334f020");
        partnerResults.add(new PartnerResult(partner));
        MockPartnerEmitter emitter = new MockPartnerEmitter(partnerResults);
        when(mEmitterMock.getPartnerFlowable()).thenReturn(emitter.getPartnerFlowable());
        PartnerSearchUseCase useCase = new PartnerSearchUseCase(mEmitterMock);

        TestSubscriber<PartnerResult> subscriber = useCase.emit(null).test();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(2);
        subscriber.assertValueSequence(partnerResults);
    }

    @Test
    public void shouldEmitIsPublising() throws Exception {
        PartnerResult result = new PartnerResult(true);
        MockPartnerEmitter emitter = new MockPartnerEmitter(result);
        when(mEmitterMock.getPartnerFlowable()).thenReturn(emitter.getPartnerFlowable());
        PartnerSearchUseCase useCase = new PartnerSearchUseCase(mEmitterMock);

        TestSubscriber<PartnerResult> subscriber = useCase.emit(null).test();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertValue(result);
    }

    @Test
    public void shouldEmitException() throws Exception {
        PartnerResult result = new PartnerResult(new Exception("Test error"));
        MockPartnerEmitter emitter = new MockPartnerEmitter(result);
        when(mEmitterMock.getPartnerFlowable()).thenReturn(emitter.getPartnerFlowable());
        PartnerSearchUseCase useCase = new PartnerSearchUseCase(mEmitterMock);

        TestSubscriber<PartnerResult> subscriber = useCase.emit(null).test();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertValue(result);
    }

    @Test
    public void shouldHandleException() throws Exception {
        MockPartnerEmitter emitter = new MockPartnerEmitter(new Exception("Test error"));
        when(mEmitterMock.getPartnerFlowable()).thenReturn(emitter.getPartnerFlowable());
        PartnerSearchUseCase useCase = new PartnerSearchUseCase(mEmitterMock);

        TestSubscriber<PartnerResult> subscriber = useCase.emit(null).test();
        subscriber.assertError(Exception.class);
        subscriber.assertNotComplete();
        subscriber.assertNoValues();
    }
}
