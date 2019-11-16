package com.cccdlabs.sarva.domain.p2p.base;

import com.cccdlabs.sarva.domain.model.partners.PartnerResult;

import io.reactivex.Flowable;

/**
 * Abstraction for a peer-to-peer (P2P) connection to emit {@link PartnerResult} objects
 * in the <code>p2p</code> package.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface PartnerEmitter {

    /**
     * Returns an RxJava {@link Flowable} for emitting {@link PartnerResult} data in the
     * implementing method.
     *
     * @return The RxJava Flowable
     */
    Flowable<PartnerResult> getPartnerFlowable();

    /**
     * Returns true if messages client is publishing, false if not.
     *
     * @return True if messages client is publishing, false if not
     */
    boolean isPublishing();

    /**
     * Returns true if p2p client is subscribing, false if not.
     *
     * @return True if p2p client is subscribing, false if not
     */
    boolean isSubscribing();

    /**
     * Starts the p2p connection.
     */
    void startEmitter();

    /**
     * Pauses the p2p connection temporarily releasing resources
     * (e.g. in the event of an onPause() somewhere).
     */
    void pauseEmitter();

    /**
     * Resets the p2p connection to an initial state.
     */
    void resetEmitter();

    /**
     * Resumes the p2p connection (e.g. in the event of an onResume() somewhere).
     */
    void resumeEmitter();
}
