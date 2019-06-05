package com.cccdlabs.sarva.data.storage.dao.sample;

import android.content.Context;

import com.cccdlabs.sarva.TestApp;
import com.cccdlabs.sarva.data.entity.sample.DoodadEntity;
import com.cccdlabs.sarva.data.entity.sample.GizmoEntity;
import com.cccdlabs.sarva.data.entity.sample.WidgetEntity;
import com.cccdlabs.sarva.data.storage.database.AppDatabase;
import com.cccdlabs.sarva.data.utils.DateUtils;
import com.cccdlabs.sarva.presentation.di.components.DaggerTestDataComponent;
import com.cccdlabs.sarva.presentation.di.components.TestDataComponent;
import com.cccdlabs.sarva.presentation.di.modules.TestDataModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28, application=TestApp.class)
public class DoodadDaoTest {

    private static final String WIDGET_UUID =  "be50d7f6-bf8c-4bad-9869-56990737d1a2";
    private static final String GIZMO_UUID =  "4e44eaaa-aecf-4d54-9379-ec4c80579a5b";

    private AppDatabase mDb;
    private DoodadDao mDao;
    private DoodadEntity entity1;
    private DoodadEntity entity2;
    private DoodadEntity entity3;
    private DoodadEntity entity4;

    @Before
    public void setUp() throws Exception {
        Context appContext = ApplicationProvider.getApplicationContext();
        TestDataComponent dataComponent = DaggerTestDataComponent.builder()
                .testDataModule(new TestDataModule(appContext, true))
                .build();

        mDb = dataComponent.appDatabase();
        mDao = dataComponent.doodadDao();

        entity1 = new DoodadEntity();
        entity1.setUuid("185276b0-84bf-41b4-ab5f-8681d8d72186");
        entity1.setWidgetUuid(WIDGET_UUID);
        entity1.setName("DoodadEntity One");
        entity1.setDescription("This little piggy went to market");
        entity1.touch();

        entity2 = new DoodadEntity();
        entity2.setUuid("f417e177-c340-439c-9502-082abf53e444");
        entity2.setWidgetUuid(WIDGET_UUID);
        entity2.setName("DoodadEntity Two");
        entity2.setDescription("This little piggy stayed home");
        entity2.touch();

        entity3 = new DoodadEntity();
        entity3.setUuid("3ec19c59-c127-4c62-a353-7de551dfb382");
        entity3.setWidgetUuid(WIDGET_UUID);
        entity3.setName("DoodadEntity Three");
        entity3.setDescription("This little piggy ate roast beef");
        entity3.touch();

        entity4 = new DoodadEntity();
        entity4.setUuid("55bad4ee-12af-45bc-a0da-9b2b711fd787");
        entity4.setWidgetUuid(WIDGET_UUID);
        entity4.setName("DoodadEntity Four");
        entity4.setDescription("I forgot what the last one did");
        entity4.touch();

        // Add a gizmo so we don't get an SQL foreign key error
        GizmoEntity gizmo = new GizmoEntity();
        gizmo.setUuid(GIZMO_UUID);
        gizmo.setName("Gizmo One");
        gizmo.setDescription("One, two, three");
        gizmo.touch();
        dataComponent.gizmoDao().insert(gizmo);

        // Then add a Widget so we don't get an SQL foreign key error
        WidgetEntity widget = new WidgetEntity();
        widget.setUuid(WIDGET_UUID);
        widget.setGizmoUuid(GIZMO_UUID);
        widget.setName("Widget One");
        widget.setDescription("Just one widget for here");
        widget.touch();
        dataComponent.widgetDao().insert(widget);
    }

    @Test
    public void WidgetDao_shouldPerformAllFuntions() throws Exception {
        // TEST insert(entity)
        long id = mDao.insert(entity1);

        // TEST insert(Entity[]...)
        mDao.insert(new DoodadEntity[]{entity2, entity3, entity4});

        // TEST fromId(id)
        DoodadEntity entity = mDao.fromId((int)id);
        assertNotNull("Entity after fromId(id)", entity);
        assertEntitiesEqual(entity1, entity);

        // TEST getByWidgets(uuid)
        List<DoodadEntity> entities = mDao.getByWidget(WIDGET_UUID);
        assertNotNull("List<DoodadEntity> after getByWidget(uuid) null", entities);
        assertEquals("List<DoodadEntity> after getByWidget(uuid) count incorrect", 4, entities.size());

        // TEST getByWidgets(List)
        List<String> uuids = new ArrayList<>(1);
        uuids.add(WIDGET_UUID);
        entities = mDao.getByWidgets(uuids);
        assertNotNull("List<DoodadEntity> after getByWidgets(List<String>) null", entities);
        assertEquals("List<DoodadEntity> after getByWidgets(List<String>) count incorrect", 4, entities.size());

        // TEST update(entity)
        entity.setName("DoodadEntity One point One");
        entity.setDescription("No more piggies");
        entity.touch(); // update timestamps
        int count = mDao.update(entity);
        assertEquals("updateCount not 1", 1, count);

        // TEST fromUuid(uuid)
        DoodadEntity updated = mDao.fromUuid(entity.getUuid());
        assertNotNull("Entity after fromUuid(uuid)", updated);
        assertEntitiesEqual(entity, updated);

        // TEST setUpdatedAt(uuid, updatedAt)
        long newTime = updated.getUpdatedAt().getTime() + 2000;
        Date updatedAt = new Date(newTime);
        mDao.setUpdatedAt(updated.getUuid(), DateUtils.dateToSqlString(updatedAt));
        updated = mDao.fromUuid(updated.getUuid());
        assertNotNull("Entity after setUpdatedAt(uuid, updatedAt) null", updated);
        assertEquals("updatedAt timestamp incorrect", newTime, updated.getUpdatedAt().getTime());

        // TEST getAll()
        entities = mDao.getAll();
        assertNotNull("List<DoodadEntity> after getAll() null", entities);
        assertEquals("List<DoodadEntity> after getAll() count incorrect", 4, entities.size());

        // TEST delete(entity)
        entity = entities.get(0);
        count = mDao.delete(entity);
        assertEquals("deleteCount[entity] not 1", 1, count);

        // TEST delete(uuid)
        entity = entities.get(1);
        String uuid = entity.getUuid();
        assertNotNull("Entity for DELETE by UUID null", entity);
        count = mDao.delete(uuid);
        assertEquals("deleteCount[UUID] not 1", 1, count);

        // TEST delete(entity[]...)
        DoodadEntity[] entitiesArr = new DoodadEntity[]{entities.get(2), entities.get(3)};
        count = mDao.delete(entitiesArr);
        assertEquals("deleteCount[entity[]...] not 2", 2, count);

        // TEST getAll() returns empty List after deletes
        entities = mDao.getAll();
        assertNotNull("List<DoodadEntity> after getAll() null", entities);
        assertEquals("List<DoodadEntity> after getAll() count incorrect", 0, entities.size());
    }

    @After
    public void tearDown() throws Exception {
        mDb.close();
    }

    private void assertEntitiesEqual(DoodadEntity entity1, DoodadEntity entity2) {
        assertNotNull("Entity 1 null", entity1);
        assertNotNull("Entity 2 null", entity1);
        assertEquals("UUIDs not equal", entity1.getUuid(), entity2.getUuid());
        assertEquals("Widget UUIDs not equal", entity1.getWidgetUuid(), entity2.getWidgetUuid());
        assertEquals("name not equal", entity1.getName(), entity2.getName());
        assertEquals("description not equal", entity1.getDescription(), entity2.getDescription());

        assertNotNull("Entity 1 getCreatedAt null", entity1.getCreatedAt());
        assertNotNull("Entity 2 getCreatedAt null", entity2.getCreatedAt());
        assertEquals(
                "createdAt not equal",
                DateUtils.dateToSqlString(entity1.getCreatedAt()),
                DateUtils.dateToSqlString(entity2.getCreatedAt())
        );

        assertNotNull("Entity 1 getUpdatedAt null", entity1.getUpdatedAt());
        assertNotNull("Entity 2 getUpdatedAt null", entity2.getUpdatedAt());
        assertEquals(
                "updatedAt not equal",
                DateUtils.dateToSqlString(entity1.getUpdatedAt()),
                DateUtils.dateToSqlString(entity2.getUpdatedAt())
        );
    }
}
