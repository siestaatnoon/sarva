package com.oscarrrweb.sarva.domain.interactors.sample;

import com.oscarrrweb.sarva.data.repository.sample.GizmoRepository;
import com.oscarrrweb.sarva.domain.interactors.base.AbstractUseCase;
import com.oscarrrweb.sarva.domain.model.sample.Gizmo;

import java.util.List;

import javax.inject.Inject;

public class SampleDisplayUseCase extends AbstractUseCase<Void, List<Gizmo>> {

    @Inject GizmoRepository mGizmoRepository;

    @Inject
    public SampleDisplayUseCase() {}

    @Override
    public List<Gizmo> run(Void parameter) throws Exception {
        List<Gizmo> gizmos = mGizmoRepository.getAll();
        return mGizmoRepository.attachWidgets(gizmos);
    }
}
