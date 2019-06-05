package com.cccdlabs.sarva.domain.repository.sample;

import com.cccdlabs.sarva.domain.model.sample.Widget;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryQueryException;

import java.util.List;

public interface WidgetRepo {

    Widget attachGizmo(Widget widget) throws RepositoryQueryException;

    List<Widget> attachGizmo(List<Widget> widget) throws RepositoryQueryException;

    Widget attachDoodads(Widget widget) throws RepositoryQueryException;

    List<Widget> attachDoodads(List<Widget> widget) throws RepositoryQueryException;
}
