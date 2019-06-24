package com.cccdlabs.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.interactors.base.AbstractUseCase;
import com.cccdlabs.sarva.domain.model.partners.Partner;

import java.util.List;

import javax.inject.Inject;

/**
 * {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} to retrieve all {@link Partner}s
 * that are saved on this device from a data source.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class GetAllPartnersUseCase extends AbstractUseCase<Void, List<Partner>> {

    /**
     * Data source for retrieving {@link Partner} objects.
     */
    final private PartnerRepository repository;

    /**
     * Constructor. Annotated with {@link Inject} for Dagger 2 dependency injection.
     *
     * @param repository Data source for retrieving {@link Partner} objects
     */
    @Inject
    public GetAllPartnersUseCase(@NonNull PartnerRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns a {@link List} of all {@link Partner}s saved on this device.
     *
     * @param v     The generic type parameter passed to the UseCase to process
     * @return      The Partner list
     * @throws      Exception if an error occurs in the operation
     */
    public List<Partner> run(Void v) throws Exception {
        return repository.getAll();
    }
}
