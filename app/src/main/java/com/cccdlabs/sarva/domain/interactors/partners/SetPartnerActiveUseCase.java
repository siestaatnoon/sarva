package com.cccdlabs.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.interactors.base.AbstractUseCase;
import com.cccdlabs.sarva.domain.model.partners.Partner;

import java.util.List;

import javax.inject.Inject;

/**
 * {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} to set a single {@link Partner},
 * saved on this device from a data source, active or inactive.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class SetPartnerActiveUseCase extends AbstractUseCase<Partner, Void> {

    /**
     * Data source for retrieving {@link Partner} objects.
     */
    final private PartnerRepository repository;

    /**
     * Constructor. Annotated with {@link Inject} for Dagger 2 dependency injection.
     *
     * @param repository Data source for setting {@link Partner} object active or inactive
     */
    @Inject
    public SetPartnerActiveUseCase(@NonNull PartnerRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns a {@link List} of all {@link Partner}s saved on this device.
     *
     * @param partner   The Partner object where <code>Partner.isActive()</code> must contain
     *                  the active/inactive value to save
     * @return          null
     * @throws          Exception if an error occurs in the operation
     */
    public Void run(@NonNull final Partner partner) throws Exception {
        String uuid = partner.getUuid();
        if (partner.isActive()) {
            repository.setActive(uuid);
        } else {
            repository.setInactive(uuid);
        }
        return null;
    }
}
