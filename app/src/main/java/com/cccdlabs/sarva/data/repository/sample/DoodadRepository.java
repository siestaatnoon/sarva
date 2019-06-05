package com.cccdlabs.sarva.data.repository.sample;

import com.cccdlabs.sarva.data.entity.sample.DoodadEntity;
import com.cccdlabs.sarva.data.mappers.sample.DoodadMapper;
import com.cccdlabs.sarva.data.repository.base.AbstractRepository;
import com.cccdlabs.sarva.data.storage.dao.sample.DoodadDao;
import com.cccdlabs.sarva.domain.model.sample.Doodad;

import javax.inject.Inject;

public class DoodadRepository extends AbstractRepository<DoodadEntity, Doodad, DoodadMapper, DoodadDao> {

    @Inject
    public DoodadRepository() {}
}
