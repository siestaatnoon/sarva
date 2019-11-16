package com.cccdlabs.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.interactors.base.AbstractUseCase;
import com.cccdlabs.sarva.domain.model.partners.Partner;

import javax.inject.Inject;

/**
 * {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} to add a {@link Partner}
 * to this device from a data source.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class AddPartnerUseCase extends AbstractUseCase<Partner, Partner> {

    /**
     * Data source for adding {@link Partner} objects.
     */
    final private PartnerRepository repository;

    /**
     * Constructor. Annotated with {@link Inject} for Dagger 2 dependency injection.
     *
     * @param repository Data source for deleting {@link Partner}
     */
    @Inject
    public AddPartnerUseCase(@NonNull PartnerRepository repository) {
        this.repository = repository;
    }

    /**
     * Performs adds a {@link Partner} and returns it updated with the database auto-increment ID.
     *
     * @param partner   The Partner to be deleted
     * @return          The Partner updated with the database auto-increment ID
     * @throws          Exception if an error occurs in the operation
     */
    public Partner run(@NonNull final Partner partner) throws Exception {
        return repository.sync(partner);
    }
}
