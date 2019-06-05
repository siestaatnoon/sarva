package com.cccdlabs.sarva.domain.p2p.base;

import com.cccdlabs.sarva.domain.model.partners.PartnerResult;

import io.reactivex.Flowable;

/**
 * Abstraction for a peer-to-peer connection to emit {@link PartnerResult} objects
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
    Flowable<PartnerResult> getPartnerEmitter();
}
