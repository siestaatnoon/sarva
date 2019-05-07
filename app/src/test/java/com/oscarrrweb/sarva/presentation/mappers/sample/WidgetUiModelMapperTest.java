package com.oscarrrweb.sarva.presentation.mappers.sample;

import com.oscarrrweb.sarva.domain.model.sample.Doodad;
import com.oscarrrweb.sarva.domain.model.sample.Widget;
import com.oscarrrweb.sarva.presentation.model.sample.DoodadUiModel;
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
public class WidgetUiModelMapperTest {

    private static final String WIDGET_UUID_1 =  "589a0b6e-5ce8-4759-944f-81f993b7694e";
    private static final String WIDGET_UUID_2 =  "86261454-f597-4586-8506-192ac6ddd471";
    private static final String GIZMO_UUID =  "be50d7f6-bf8c-4bad-9869-56990737d1a2";

    private WidgetUiModel uiModel1;
    private WidgetUiModel uiModel2;

    @Before
    public void setUp() throws Exception {
        List<DoodadUiModel> doodads = new ArrayList<>(2);
        DoodadUiModel doodad;
        Date now = new Date();

        doodad = new DoodadUiModel();
        doodad.setId(1);
        doodad.setUuid("85ab710a-b0ea-4e6d-ac0b-b933ef61e4a4");
        doodad.setWidgetUuid(WIDGET_UUID_1);
        doodad.setName("Doodad One");
        doodad.setDescription("Not much to say here");
        doodad.setCreatedAt(now);
        doodad.setUpdatedAt(now);
        doodads.add(doodad);

        doodad = new DoodadUiModel();
        doodad.setId(2);
        doodad.setUuid("343d935d-d480-4cf7-af25-fae99f38e24a");
        doodad.setWidgetUuid(WIDGET_UUID_1);
        doodad.setName("Doodad Two");
        doodad.setDescription("Even less to say here");
        doodad.setCreatedAt(now);
        doodad.setUpdatedAt(now);
        doodads.add(doodad);

        uiModel1 = new WidgetUiModel();
        uiModel1.setId(1);
        uiModel1.setUuid(WIDGET_UUID_1);
        uiModel1.setGizmoUuid(GIZMO_UUID);
        uiModel1.setName("WidgetEntity One");
        uiModel1.setDescription("Whooptee doo");
        uiModel1.setDoodads(doodads);
        uiModel1.setCreatedAt(now);
        uiModel1.setUpdatedAt(now);

        doodads = new ArrayList<>(2);

        doodad = new DoodadUiModel();
        doodad.setId(3);
        doodad.setUuid("005f82f6-30eb-49a8-b239-f1715d700b53");
        doodad.setWidgetUuid(WIDGET_UUID_2);
        doodad.setName("Doodad One and a half");
        doodad.setDescription("Lots going on");
        doodad.setCreatedAt(now);
        doodad.setUpdatedAt(now);
        doodads.add(doodad);

        doodad = new DoodadUiModel();
        doodad.setId(4);
        doodad.setUuid("c95ffaca-91f9-4019-9e47-68a99e92598e");
        doodad.setWidgetUuid(WIDGET_UUID_2);
        doodad.setName("Doodad Two and a half");
        doodad.setDescription("A real party over here");
        doodad.setCreatedAt(now);
        doodad.setUpdatedAt(now);
        doodads.add(doodad);

        uiModel2 = new WidgetUiModel();
        uiModel2.setId(2);
        uiModel2.setUuid(WIDGET_UUID_2);
        uiModel2.setGizmoUuid(GIZMO_UUID);
        uiModel2.setName("WidgetEntity Two");
        uiModel2.setDescription("Whooptee doo too");
        uiModel2.setDoodads(doodads);
        uiModel2.setCreatedAt(now);
        uiModel2.setUpdatedAt(now);
    }

    @Test
    public void WidgetMapper_shouldConvertUiModelToDomainAndBack() {
        // UI model model -> domain model
        Widget model = WidgetUiModelMapper.toDomainModel(uiModel1);
        assertNotNull("Converted domain object null", model);
        assertEqualValues(uiModel1, model);

        // domain model -> UI model model
        uiModel1 = WidgetUiModelMapper.fromDomainModel(model);
        assertNotNull("Converted UI model object null", uiModel1);
        assertEqualValues(uiModel1, model);
    }

    @Test
    public void WidgetMapper_shouldConvertUiModelToDomainListAndBack() {
        List<WidgetUiModel> uiModelList = new ArrayList<>(2);
        uiModelList.add(uiModel1);
        uiModelList.add(uiModel2);

        // List<UI model> -> List<domain>
        List<Widget> domainList = WidgetUiModelMapper.toDomainModel(uiModelList);
        assertNotNull("Converted UI model List null", domainList);
        assertEquals("Original UI model and converted domain List are not equal size", uiModelList.size(), domainList.size());
        for (int i=0; i < uiModelList.size(); i++) {
            assertEqualValues(uiModelList.get(i), domainList.get(i));
        }

        // List<domain> -> List<UI model>
        uiModelList = WidgetUiModelMapper.fromDomainModel(domainList);
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

    private void assertEqualValues(WidgetUiModel uiModel, Widget model) {
        assertNotNull("UI model null", uiModel);
        assertNotNull("Domain model null", model);
        assertEquals("IDs not equal", uiModel.getId(), model.getId());
        assertEquals("UUIDs not equal", uiModel.getUuid(), model.getUuid());
        assertEquals("Gizmo UUIDs not equal", uiModel.getGizmoUuid(), model.getGizmoUuid());
        assertEquals("name not equal", uiModel.getName(), model.getName());
        assertEquals("description not equal", uiModel.getDescription(), model.getDescription());

        assertNotNull("UI model getCreatedAt null", uiModel.getCreatedAt());
        assertNotNull("Domain model getCreatedAt null", model.getCreatedAt());
        assertEquals("createdAt not equal", uiModel.getCreatedAt().getTime(), model.getCreatedAt().getTime());

        assertNotNull("UI model getUpdatedAt null", uiModel.getUpdatedAt());
        assertNotNull("Domain model getUpdatedAt null", model.getUpdatedAt());
        assertEquals("updatedAt not equal", uiModel.getUpdatedAt().getTime(), model.getUpdatedAt().getTime());

        assertEqualDoodads(uiModel.getDoodads(), model.getDoodads());
    }

    private void assertEqualDoodads(List<DoodadUiModel> uiModels, List<Doodad> models) {
        assertNotNull("List<DoodadUiModel> null", uiModels);
        assertNotNull("List<Doodad> null", models);
        assertEquals("List<DoodadUiModel> size not 2", 2, uiModels.size());
        assertEquals("List<Doodad> size not 2", 2, models.size());

        for (int i=0; i < uiModels.size(); i++) {
            DoodadUiModel uiModel = uiModels.get(i);
            Doodad model = models.get(i);
            assertEquals("Doodad IDs not equal", uiModel.getId(), model.getId());
            assertEquals("Doodad UUIDs not equal", uiModel.getUuid(), model.getUuid());
            assertEquals("Doodad Widget UUIDs not equal", uiModel.getWidgetUuid(), model.getWidgetUuid());
            assertEquals("Doodad name not equal", uiModel.getName(), model.getName());
            assertEquals("Doodad description not equal", uiModel.getDescription(), model.getDescription());

            assertNotNull("Doodad UI model getCreatedAt null", uiModel.getCreatedAt());
            assertNotNull("Doodad domain model getCreatedAt null", model.getCreatedAt());
            assertEquals("Doodad createdAt not equal", uiModel.getCreatedAt().getTime(), model.getCreatedAt().getTime());

            assertNotNull("Doodad UI model getUpdatedAt null", uiModel.getUpdatedAt());
            assertNotNull("Doodad domain model getUpdatedAt null", model.getUpdatedAt());
            assertEquals("Doodad updatedAt not equal", uiModel.getUpdatedAt().getTime(), model.getUpdatedAt().getTime());
        }
    }
}
