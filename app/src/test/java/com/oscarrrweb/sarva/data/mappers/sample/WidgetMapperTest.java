package com.oscarrrweb.sarva.data.mappers.sample;

import com.oscarrrweb.sarva.data.entity.sample.DoodadEntity;
import com.oscarrrweb.sarva.data.entity.sample.WidgetEntity;
import com.oscarrrweb.sarva.domain.model.sample.Doodad;
import com.oscarrrweb.sarva.domain.model.sample.Widget;
import com.oscarrrweb.sarva.presentation.di.components.DaggerTestMapperComponent;
import com.oscarrrweb.sarva.presentation.di.components.TestMapperComponent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class WidgetMapperTest {

    private static final String WIDGET_UUID_1 =  "589a0b6e-5ce8-4759-944f-81f993b7694e";
    private static final String WIDGET_UUID_2 =  "86261454-f597-4586-8506-192ac6ddd471";
    private static final String GIZMO_UUID =  "be50d7f6-bf8c-4bad-9869-56990737d1a2";

    private WidgetMapper mMapper;
    private WidgetEntity entity1;
    private WidgetEntity entity2;

    @Before
    public void setUp() throws Exception {
        TestMapperComponent dataComponent = DaggerTestMapperComponent
                .builder()
                .build();
        mMapper = dataComponent.widgetMapper();
        dataComponent.inject(mMapper);

        List<DoodadEntity> doodads = new ArrayList<>(2);
        DoodadEntity doodad;

        doodad = new DoodadEntity();
        doodad.setId(1);
        doodad.setUuid("85ab710a-b0ea-4e6d-ac0b-b933ef61e4a4");
        doodad.setWidgetUuid(WIDGET_UUID_1);
        doodad.setName("Doodad One");
        doodad.setDescription("Not much to say here");
        doodad.touch();
        doodads.add(doodad);

        doodad = new DoodadEntity();
        doodad.setId(2);
        doodad.setUuid("343d935d-d480-4cf7-af25-fae99f38e24a");
        doodad.setWidgetUuid(WIDGET_UUID_1);
        doodad.setName("Doodad Two");
        doodad.setDescription("Even less to say here");
        doodad.touch();
        doodads.add(doodad);

        entity1 = new WidgetEntity();
        entity1.setId(1);
        entity1.setUuid(WIDGET_UUID_1);
        entity1.setGizmoUuid(GIZMO_UUID);
        entity1.setName("WidgetEntity One");
        entity1.setDescription("Whooptee doo");
        entity1.setDoodads(doodads);
        entity1.touch();

        doodads = new ArrayList<>(2);

        doodad = new DoodadEntity();
        doodad.setId(3);
        doodad.setUuid("005f82f6-30eb-49a8-b239-f1715d700b53");
        doodad.setWidgetUuid(WIDGET_UUID_2);
        doodad.setName("Doodad One and a half");
        doodad.setDescription("Lots going on");
        doodad.touch();
        doodads.add(doodad);

        doodad = new DoodadEntity();
        doodad.setId(4);
        doodad.setUuid("c95ffaca-91f9-4019-9e47-68a99e92598e");
        doodad.setWidgetUuid(WIDGET_UUID_2);
        doodad.setName("Doodad Two and a half");
        doodad.setDescription("A real party over here");
        doodad.touch();
        doodads.add(doodad);

        entity2 = new WidgetEntity();
        entity2.setId(2);
        entity2.setUuid(WIDGET_UUID_2);
        entity2.setGizmoUuid(GIZMO_UUID);
        entity2.setName("WidgetEntity Two");
        entity2.setDescription("Whooptee doo too");
        entity2.setDoodads(doodads);
        entity2.touch();
    }

    @Test
    public void WidgetMapper_shouldConvertEntityToDomainAndBack() {
        // entity model -> domain model
        Widget model = mMapper.toDomainModel(entity1);
        assertNotNull("Converted domain object null", model);
        assertEqualValues(entity1, model);

        // domain model -> entity model
        entity1 = mMapper.fromDomainModel(model);
        assertNotNull("Converted entity object null", entity1);
        assertEqualValues(entity1, model);
    }

    @Test
    public void WidgetMapper_shouldConvertEntityToDomainListAndBack() {
        List<WidgetEntity> entityList = new ArrayList<>(2);
        entityList.add(entity1);
        entityList.add(entity2);

        // List<entity> -> List<domain>
        List<Widget> domainList = mMapper.toDomainModel(entityList);
        assertNotNull("Converted entity List null", domainList);
        assertEquals("Original entity and converted domain List are not equal size", entityList.size(), domainList.size());
        for (int i=0; i < entityList.size(); i++) {
            assertEqualValues(entityList.get(i), domainList.get(i));
        }

        // List<domain> -> List<entity>
        entityList = mMapper.fromDomainModel(domainList);
        assertNotNull("Converted domain List null", domainList);
        assertEquals("Original domain and converted entity List are not equal size", domainList.size(), entityList.size());
        for (int i=0; i < entityList.size(); i++) {
            assertEqualValues(entityList.get(i), domainList.get(i));
        }
    }

    @After
    public void tearDown() throws Exception {
        mMapper = null;
    }

    private void assertEqualValues(WidgetEntity entity, Widget model) {
        assertNotNull("Entity null", entity);
        assertNotNull("Model null", model);
        assertEquals("IDs not equal", entity.getId(), model.getId());
        assertEquals("UUIDs not equal", entity.getUuid(), model.getUuid());
        assertEquals("Gizmo UUIDs not equal", entity.getGizmoUuid(), model.getGizmoUuid());
        assertEquals("name not equal", entity.getName(), model.getName());
        assertEquals("description not equal", entity.getDescription(), model.getDescription());

        assertNotNull("Entity getCreatedAt null", entity.getCreatedAt());
        assertNotNull("Model getCreatedAt null", model.getCreatedAt());
        assertEquals("createdAt not equal", entity.getCreatedAt().getTime(), model.getCreatedAt().getTime());

        assertNotNull("Entity getUpdatedAt null", entity.getUpdatedAt());
        assertNotNull("Model getUpdatedAt null", model.getUpdatedAt());
        assertEquals("updatedAt not equal", entity.getUpdatedAt().getTime(), model.getUpdatedAt().getTime());

        assertEqualDoodads(entity.getDoodads(), model.getDoodads());
    }

    private void assertEqualDoodads(List<DoodadEntity> entities, List<Doodad> models) {
        assertNotNull("List<DoodadEntity> null", entities);
        assertNotNull("List<Doodad> null", models);
        assertEquals("List<DoodadEntity> size not 2", 2, entities.size());
        assertEquals("List<Doodad> size not 2", 2, models.size());

        for (int i=0; i < entities.size(); i++) {
            DoodadEntity entity = entities.get(i);
            Doodad model = models.get(i);
            assertEquals("Doodad IDs not equal", entity.getId(), model.getId());
            assertEquals("Doodad UUIDs not equal", entity.getUuid(), model.getUuid());
            assertEquals("Doodad Widget UUIDs not equal", entity.getWidgetUuid(), model.getWidgetUuid());
            assertEquals("Doodad name not equal", entity.getName(), model.getName());
            assertEquals("Doodad description not equal", entity.getDescription(), model.getDescription());

            assertNotNull("Doodad entity getCreatedAt null", entity.getCreatedAt());
            assertNotNull("Doodad model getCreatedAt null", model.getCreatedAt());
            assertEquals("Doodad createdAt not equal", entity.getCreatedAt().getTime(), model.getCreatedAt().getTime());

            assertNotNull("Doodad entity getUpdatedAt null", entity.getUpdatedAt());
            assertNotNull("Doodad model getUpdatedAt null", model.getUpdatedAt());
            assertEquals("Doodad updatedAt not equal", entity.getUpdatedAt().getTime(), model.getUpdatedAt().getTime());
        }
    }
}
