package com.oscarrrweb.tddboilerplate.data.repository.sample;

import com.oscarrrweb.tddboilerplate.data.entity.sample.GizmoEntity;
import com.oscarrrweb.tddboilerplate.data.mappers.sample.GizmoMapper;
import com.oscarrrweb.tddboilerplate.data.repository.base.AbstractRepository;
import com.oscarrrweb.tddboilerplate.data.storage.dao.sample.GizmoDao;
import com.oscarrrweb.tddboilerplate.domain.model.sample.Gizmo;

import javax.inject.Inject;

public class GizmoRepository extends AbstractRepository<GizmoEntity, Gizmo, GizmoMapper, GizmoDao> {

    @Inject
    public GizmoRepository() {}
}
