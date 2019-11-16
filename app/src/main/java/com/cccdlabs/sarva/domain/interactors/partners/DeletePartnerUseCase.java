package com.cccdlabs.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.interactors.base.AbstractUseCase;
import com.cccdlabs.sarva.domain.model.partners.Partner;

import javax.inject.Inject;

/**
 * {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} to delete a {@link Partner}
 * saved on this device from a data source.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class DeletePartnerUseCase extends AbstractUseCase<Partner, Integer> {

    /**
     * Data source for retrieving {@link Partner} objects.
     */
    final private PartnerRepository repository;

    /**
     * Constructor. Annotated with {@link Inject} for Dagger 2 dependency injection.
     *
     * @param repository Data source for deleting {@link Partner}
     */
    @Inject
    public DeletePartnerUseCase(@NonNull PartnerRepository repository) {
        this.repository = repository;
    }

    /**
     * Performs a delete on the {@link Partner} and returns 1 if deleted or 0 if not deleted.
     *
     * @param partner   The Partner to be deleted
     * @return          1 if delete successful, zero if no delete performed
     * @throws          Exception if an error occurs in the operation
     */
    public Integer run(@NonNull final Partner partner) throws Exception {
        return repository.delete(partner);
    }
}
