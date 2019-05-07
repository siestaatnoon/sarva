package com.oscarrrweb.sarva.presentation.mappers.sample;

import com.oscarrrweb.sarva.domain.model.sample.Gizmo;
import com.oscarrrweb.sarva.domain.model.sample.Widget;
import com.oscarrrweb.sarva.presentation.model.sample.GizmoUiModel;
import com.oscarrrweb.sarva.presentation.model.sample.WidgetUiModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class GizmoUiModelMapperTest {

    private static final String GIZMO_UUID_1 =  "589a0b6e-5ce8-4759-944f-81f993b7694e";
    private static final String GIZMO_UUID_2 =  "86261454-f597-4586-8506-192ac6ddd471";

    private GizmoUiModel uiModel1;
    private GizmoUiModel uiModel2;

    @Before
    public void setUp() throws Exception {
        List<WidgetUiModel> widgets = new ArrayList<>(2);
        WidgetUiModel widget;
        Date now = new Date();

        widget = new WidgetUiModel();
        widget.setId(1);
        widget.setUuid("f7c4b163-938e-4c16-95c1-4849ebd88a6b");
        widget.setGizmoUuid(GIZMO_UUID_1);
        widget.setName("Widget One");
        widget.setDescription("Not much to say here");
        widget.setCreatedAt(now);
        widget.setUpdatedAt(now);
        widgets.add(widget);

        widget = new WidgetUiModel();
        widget.setId(2);
        widget.setUuid("39b7b73e-bdb4-4297-82ae-70f725288871");
        widget.setGizmoUuid(GIZMO_UUID_1);
        widget.setName("Widget Two");
        widget.setDescription("Even less to say here");
        widget.setCreatedAt(now);
        widget.setUpdatedAt(now);
        widgets.add(widget);

        uiModel1 = new GizmoUiModel();
        uiModel1.setId(1);
        uiModel1.setUuid(GIZMO_UUID_1);
        uiModel1.setName("Gizmo One");
        uiModel1.setDescription("Old MacDonald had a farm.");
        uiModel1.setWidgets(widgets);
        uiModel1.setCreatedAt(now);
        uiModel1.setUpdatedAt(now);

        widgets = new ArrayList<>(2);

        widget = new WidgetUiModel();
        widget.setId(3);
        widget.setUuid("176c8ad5-c00e-4c12-a3fe-9c4da382c625");
        widget.setGizmoUuid(GIZMO_UUID_2);
        widget.setName("Widget Three");
        widget.setDescription("Lots going on");
        widget.setCreatedAt(now);
        widget.setUpdatedAt(now);
        widgets.add(widget);

        widget = new WidgetUiModel();
        widget.setId(4);
        widget.setUuid("e8abaafc-491e-49d7-b4d0-eef2950a8cee");
        widget.setGizmoUuid(GIZMO_UUID_2);
        widget.setName("Widget Four");
        widget.setDescription("A real party over here");
        widget.setCreatedAt(now);
        widget.setUpdatedAt(now);
        widgets.add(widget);

        uiModel2 = new GizmoUiModel();
        uiModel2.setId(2);
        uiModel2.setUuid(GIZMO_UUID_2);
        uiModel2.setName("Gizmo Two");
        uiModel2.setDescription("Then sold hamburgers or something.");
        uiModel2.setWidgets(widgets);
        uiModel2.setCreatedAt(now);
        uiModel2.setUpdatedAt(now);
    }

    @Test
    public void WidgetMapper_shouldConvertUiModelToDomainAndBack() {
        // UI model model -> domain model
        Gizmo model = GizmoUiModelMapper.toDomainModel(uiModel1);
        assertNotNull("Converted domain object null", model);
        assertEqualValues(uiModel1, model);

        // domain model -> UI model model
        uiModel1 = GizmoUiModelMapper.fromDomainModel(model);
        assertNotNull("Converted UI model object null", uiModel1);
        assertEqualValues(uiModel1, model);
    }

    @Test
    public void WidgetMapper_shouldConvertUiModelToDomainListAndBack() {
        List<GizmoUiModel> uiModelList = new ArrayList<>(2);
        uiModelList.add(uiModel1);
        uiModelList.add(uiModel2);

        // List<UI model> -> List<domain>
        List<Gizmo> domainList = GizmoUiModelMapper.toDomainModel(uiModelList);
        assertNotNull("Converted UI model List null", domainList);
        assertEquals("Original UI model and converted domain List are not equal size", uiModelList.size(), domainList.size());
        for (int i=0; i < uiModelList.size(); i++) {
            assertEqualValues(uiModelList.get(i), domainList.get(i));
        }

        // List<domain> -> List<UI model>
        uiModelList = GizmoUiModelMapper.fromDomainModel(domainList);
        assertNotNull("Converted domain List null", domainList);
        assertEquals("Original domain and converted UI model List are not equal size", domainList.size(), uiModelList.size());
        for (int i=0; i < uiModelList.size(); i++) {
            assertEqualValues(uiModelList.get(i), domainList.get(i));
        }
    }

    @After
    public void tearDown() throws Exception {
        uiModel1 = null;
        uiModel2 = null;
    }

    private void assertEqualValues(GizmoUiModel uiModel, Gizmo model) {
        assertNotNull("UI model null", uiModel);
        assertNotNull("Domain model null", model);
        assertEquals("IDs not equal", uiModel.getId(), model.getId());
        assertEquals("UUIDs not equal", uiModel.getUuid(), model.getUuid());
        assertEquals("name not equal", uiModel.getName(), model.getName());
        assertEquals("description not equal", uiModel.getDescription(), model.getDescription());

        assertNotNull("UI model getCreatedAt null", uiModel.getCreatedAt());
        assertNotNull("Domain model getCreatedAt null", model.getCreatedAt());
        assertEquals("createdAt not equal", uiModel.getCreatedAt().getTime(), model.getCreatedAt().getTime());

        assertNotNull("UI model getUpdatedAt null", uiModel.getUpdatedAt());
        assertNotNull("Domain model getUpdatedAt null", model.getUpdatedAt());
        assertEquals("updatedAt not equal", uiModel.getUpdatedAt().getTime(), model.getUpdatedAt().getTime());

        assertEqualWidgets(uiModel.getWidgets(), model.getWidgets());
    }

    private void assertEqualWidgets(List<WidgetUiModel> uiModels, List<Widget> models) {
        assertNotNull("List<WidgetUiModel> null", uiModels);
        assertNotNull("List<Widget> null", models);
        assertEquals("List<WidgetUiModel> size not 2", 2, uiModels.size());
        assertEquals("List<Widget> size not 2", 2, models.size());

        for (int i=0; i < uiModels.size(); i++) {
            WidgetUiModel uiModel = uiModels.get(i);
            Widget model = models.get(i);
            assertEquals("Widget IDs not equal", uiModel.getId(), model.getId());
            assertEquals("Widget UUIDs not equal", uiModel.getUuid(), model.getUuid());
            assertEquals("Widget UUIDs not equal", uiModel.getGizmoUuid(), model.getGizmoUuid());
            assertEquals("Widget name not equal", uiModel.getName(), model.getName());
            assertEquals("Widget description not equal", uiModel.getDescription(), model.getDescription());

            assertNotNull("Widget UI model getCreatedAt null", uiModel.getCreatedAt());
            assertNotNull("Widget model getCreatedAt null", model.getCreatedAt());
            assertEquals("Widget createdAt not equal", uiModel.getCreatedAt().getTime(), model.getCreatedAt().getTime());

            assertNotNull("Widget UI model getUpdatedAt null", uiModel.getUpdatedAt());
            assertNotNull("Widget model getUpdatedAt null", model.getUpdatedAt());
            assertEquals("Widget updatedAt not equal", uiModel.getUpdatedAt().getTime(), model.getUpdatedAt().getTime());
        }
    }
}
