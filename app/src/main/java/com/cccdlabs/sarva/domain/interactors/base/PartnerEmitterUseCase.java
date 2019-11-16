package com.cccdlabs.sarva.domain.interactors.base;

/**
 * Base {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} for P2P interactions in the
 * <code>data/p2p</code> package.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface PartnerEmitterUseCase {

    /**
     * Pauses this device's P2P communication with other devices.
     */
    void pauseEmitterSource();

    /**
     * Resets this device's P2P communication with other devices.
     */
    void resetEmitterSource();

    /**
     * Resumes this device's P2P communication with other devices.
     */
    void resumeEmitterSource();
}
