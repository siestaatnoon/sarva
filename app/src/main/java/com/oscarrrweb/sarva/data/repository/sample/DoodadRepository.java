package com.oscarrrweb.sarva.data.repository.sample;

import com.oscarrrweb.sarva.data.entity.sample.DoodadEntity;
import com.oscarrrweb.sarva.data.mappers.sample.DoodadMapper;
import com.oscarrrweb.sarva.data.repository.base.AbstractRepository;
import com.oscarrrweb.sarva.data.storage.dao.sample.DoodadDao;
import com.oscarrrweb.sarva.domain.model.sample.Doodad;

import javax.inject.Inject;

public class DoodadRepository extends AbstractRepository<DoodadEntity, Doodad, DoodadMapper, DoodadDao> {

    @Inject
    public DoodadRepository() {}
}
