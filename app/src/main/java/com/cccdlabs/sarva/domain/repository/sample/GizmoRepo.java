package com.cccdlabs.sarva.domain.repository.sample;

import com.cccdlabs.sarva.domain.model.sample.Gizmo;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryQueryException;

import java.util.List;

public interface GizmoRepo {

    Gizmo attachWidgets(Gizmo gizmo) throws RepositoryQueryException;

    List<Gizmo> attachWidgets(List<Gizmo> gizmo) throws RepositoryQueryException;
}
