package com.cccdlabs.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerBroadcast;
import com.cccdlabs.sarva.domain.interactors.partners.base.AbstractPartnerUseCase;

import javax.inject.Inject;

/**
 * {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} to broadcast this device for
 * saving device info on other remote devices.
 * <p>
 * NOTE: {@link Inject} annotation means no inject(PartnerBroadcastUseCase) required in
 * Dagger Component or {@link dagger.Provides} annotated method needed in Dagger Module
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerBroadcastUseCase extends AbstractPartnerUseCase {

    /**
     * {@inheritDoc}
     */
    @Inject
    public PartnerBroadcastUseCase(@NonNull NearbyPartnerBroadcast emitter) {
        super(emitter);
    }
}
