package com.cccdlabs.sarva.domain.interactors.partners.base;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.domain.interactors.base.AbstractUseCase;
import com.cccdlabs.sarva.domain.interactors.base.PartnerEmitterUseCase;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.domain.p2p.base.PartnerEmitter;

import io.reactivex.Flowable;

/**
 * Abstraction {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} for P2P interactions
 * in the <code>data/p2p</code> package.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
abstract public class AbstractPartnerUseCase extends AbstractUseCase<Void, PartnerResult>
        implements PartnerEmitterUseCase {

    /**
     * Object to emit P2P connection info form other devices.
     */
    protected PartnerEmitter emitter;

    /**
     * Constructor.
     *
     * @param emitter Object to emit P2P connection info form other devices
     */
    public AbstractPartnerUseCase(@NonNull PartnerEmitter emitter) {
        this.emitter = emitter;
    }

    /**
     * Returns an RxJava {@link Flowable} that emits remote device info from other devices
     * connecting to this device via a {@link PartnerResult} object. Also starts the emitter
     * to begin p2p communication.
     *
     * @param parameter The generic type parameter passed to the UseCase to process
     * @return          The RxJava Flowable emitting PartnerResult objects containing
     *                  Remote device information
     */
    @Override
    public Flowable<PartnerResult> emit(Void parameter) {
        emitter.startEmitter();
        return emitter.getPartnerFlowable();
    }

    /**
     * Pauses this device's P2P communication with other devices.
     */
    @Override
    public void pauseEmitterSource() {
        emitter.pauseEmitter();
    }

    /**
     * Resets this device's P2P communication with other devices.
     */
    @Override
    public void resetEmitterSource() {
        emitter.resetEmitter();
    }

    /**
     * Resumes this device's P2P communication with other devices.
     */
    @Override
    public void resumeEmitterSource() {
        emitter.resumeEmitter();
    }
}
