package com.oscarrrweb.sarva.data.storage.dao.sample;

import android.content.Context;

import com.oscarrrweb.sarva.TestApp;
import com.oscarrrweb.sarva.data.entity.sample.GizmoEntity;
import com.oscarrrweb.sarva.data.entity.sample.WidgetEntity;
import com.oscarrrweb.sarva.data.storage.database.AppDatabase;
import com.oscarrrweb.sarva.data.utils.DateUtils;
import com.oscarrrweb.sarva.presentation.di.components.DaggerTestDataComponent;
import com.oscarrrweb.sarva.presentation.di.components.TestDataComponent;
import com.oscarrrweb.sarva.presentation.di.modules.TestDataModule;

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
public class WidgetDaoTest {
    private static final String GIZMO_UUID =  "be50d7f6-bf8c-4bad-9869-56990737d1a2";

    private AppDatabase mDb;
    private WidgetDao mDao;
    private WidgetEntity entity1;
    private WidgetEntity entity2;
    private WidgetEntity entity3;
    private WidgetEntity entity4;

    @Before
    public void setUp() throws Exception {
        Context appContext = ApplicationProvider.getApplicationContext();
        TestDataComponent dataComponent = DaggerTestDataComponent.builder()
                .testDataModule(new TestDataModule(appContext, true))
                .build();

        mDb = dataComponent.appDatabase();
        mDao = dataComponent.widgetDao();

        entity1 = new WidgetEntity();
        entity1.setUuid("589a0b6e-5ce8-4759-944f-81f993b7694e");
        entity1.setGizmoUuid(GIZMO_UUID);
        entity1.setName("WidgetEntity One");
        entity1.setDescription("Whooptee doo");
        entity1.touch();

        entity2 = new WidgetEntity();
        entity2.setUuid("86261454-f597-4586-8506-192ac6ddd471");
        entity2.setGizmoUuid(GIZMO_UUID);
        entity2.setName("WidgetEntity Two");
        entity2.setDescription("Whooptee doo too");
        entity2.touch();

        entity3 = new WidgetEntity();
        entity3.setUuid("ac47372a-8589-4139-98ef-1559c28d974a");
        entity3.setGizmoUuid(GIZMO_UUID);
        entity3.setName("WidgetEntity Three");
        entity3.setDescription("Whooptee doo too three");
        entity3.touch();

        entity4 = new WidgetEntity();
        entity4.setUuid("98021546-5437-40ef-8f3e-5f0afae2f564");
        entity4.setGizmoUuid(GIZMO_UUID);
        entity4.setName("WidgetEntity Four");
        entity4.setDescription("Four is the chore");
        entity4.touch();

        // Add a gizmo so we don't get an SQL foreign key error
        GizmoEntity gizmo = new GizmoEntity();
        gizmo.setUuid(GIZMO_UUID);
        gizmo.setName("Gizmo One");
        gizmo.setDescription("One, two, three");
        dataComponent.gizmoDao().insert(gizmo);
    }

    @Test
    public void WidgetDao_shouldPerformAllFuntions() throws Exception {
        // TEST insert(entity)
        long id = mDao.insert(entity1);

        // TEST insert(Entity[]...)
        mDao.insert(new WidgetEntity[]{entity2, entity3, entity4});

        // TEST fromId(id)
        WidgetEntity entity = mDao.fromId((int)id);
        assertNotNull("Entity after fromId(id)", entity);
        assertEntitiesEqual(entity1, entity);

        // TEST getByGizmo(uuid)
        List<WidgetEntity> entities = mDao.getByGizmo(GIZMO_UUID);
        assertNotNull("List<WidgetEntity> after getByGizmo(uuid) null", entities);
        assertEquals("List<WidgetEntity> after getByGizmo(uuid) count incorrect", 4, entities.size());

        // TEST getByGizmos(List)
        List<String> uuids = new ArrayList<>(1);
        uuids.add(GIZMO_UUID);
        entities = mDao.getByGizmos(uuids);
        assertNotNull("List<WidgetEntity> after getByGizmo(List<String>) null", entities);
        assertEquals("List<WidgetEntity> after getByGizmo(List<String>) count incorrect", 4, entities.size());

        // TEST update(entity)
        entity.setName("WidgetEntity One point One");
        entity.setDescription("Whooptee doo daa");
        entity.touch(); // update timestamps
        int count = mDao.update(entity);
        assertEquals("updateCount not 1", 1, count);

        // TEST fromUuid(uuid)
        WidgetEntity updated = mDao.fromUuid(entity.getUuid());
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
        assertNotNull("List<WidgetEntity> after getAll() null", entities);
        assertEquals("List<WidgetEntity> after getAll() count incorrect", 4, entities.size());

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
        WidgetEntity[] entitiesArr = new WidgetEntity[]{entities.get(2), entities.get(3)};
        count = mDao.delete(entitiesArr);
        assertEquals("deleteCount[entity[]...] not 2", 2, count);

        // TEST getAll() returns empty List after deletes
        entities = mDao.getAll();
        assertNotNull("List<WidgetEntity> after getAll() null", entities);
        assertEquals("List<WidgetEntity> after getAll() count incorrect", 0, entities.size());
    }

    @After
    public void tearDown() throws Exception {
        mDb.close();
    }

    private void assertEntitiesEqual(WidgetEntity entity1, WidgetEntity entity2) {
        assertNotNull("Entity 1 null", entity1);
        assertNotNull("Entity 2 null", entity1);
        assertEquals("UUIDs not equal", entity1.getUuid(), entity2.getUuid());
        assertEquals("Gizmo UUIDs not equal", entity1.getGizmoUuid(), entity2.getGizmoUuid());
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
