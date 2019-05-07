package com.oscarrrweb.sarva.domain.repository.sample;

import com.oscarrrweb.sarva.domain.model.sample.Gizmo;
import com.oscarrrweb.sarva.domain.repository.exception.RepositoryQueryException;

import java.util.List;

public interface GizmoRepo {

    Gizmo attachWidgets(Gizmo gizmo) throws RepositoryQueryException;

    List<Gizmo> attachWidgets(List<Gizmo> gizmo) throws RepositoryQueryException;
}
