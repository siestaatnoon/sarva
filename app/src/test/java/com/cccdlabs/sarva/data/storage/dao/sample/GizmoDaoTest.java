package com.cccdlabs.sarva.data.storage.dao.sample;

import android.content.Context;

import com.cccdlabs.sarva.TestApp;
import com.cccdlabs.sarva.data.entity.sample.GizmoEntity;
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
public class GizmoDaoTest {

    private AppDatabase mDb;
    private GizmoDao mDao;
    private GizmoEntity entity1;
    private GizmoEntity entity2;
    private GizmoEntity entity3;
    private GizmoEntity entity4;

    @Before
    public void setUp() throws Exception {
        Context appContext = ApplicationProvider.getApplicationContext();
        TestDataComponent dataComponent = DaggerTestDataComponent.builder()
                .testDataModule(new TestDataModule(appContext, true))
                .build();

        mDb = dataComponent.appDatabase();
        mDao = dataComponent.gizmoDao();

        entity1 = new GizmoEntity();
        entity1.setUuid("c59405a8-2e50-4388-a151-6fa3a5be4700");
        entity1.setName("GizmoEntity One");
        entity1.setDescription("Something to describe");
        entity1.touch();

        entity2 = new GizmoEntity();
        entity2.setUuid("5a3b7075-f7d6-4bea-8b06-8473da3141e1");
        entity2.setName("GizmoEntity Two");
        entity2.setDescription("NOT a real entity");
        entity2.touch();

        entity3 = new GizmoEntity();
        entity3.setUuid("836c319b-f3cf-4773-8b62-2c53ea5b7c70");
        entity3.setName("GizmoEntity Three");
        entity3.setDescription("Oh, how I love being a Gizmo");
        entity3.touch();

        entity4 = new GizmoEntity();
        entity4.setUuid("3d802cfb-2c9e-48c8-9bcd-540e490cee3f");
        entity4.setName("GizmoEntity Four");
        entity4.setDescription("Well, that was quick");
        entity4.touch();
    }

    @Test
    public void WidgetDao_shouldPerformAllFuntions() throws Exception {
        // TEST insert(entity)
        long id = mDao.insert(entity1);

        // TEST insert(Entity[]...)
        mDao.insert(new GizmoEntity[]{entity2, entity3, entity4});

        // TEST fromId(id)
        GizmoEntity entity = mDao.fromId((int)id);
        assertNotNull("Entity after fromId(id)", entity);
        assertEntitiesEqual(entity1, entity);

        // TEST update(entity)
        entity.setName("GizmoEntity One point One");
        entity.setDescription("No more piggies");
        entity.touch(); // update timestamps
        int count = mDao.update(entity);
        assertEquals("updateCount not 1", 1, count);

        // TEST fromUuid(uuid)
        GizmoEntity updated = mDao.fromUuid(entity.getUuid());
        assertNotNull("Entity after fromUuid(uuid)", updated);
        assertEntitiesEqual(entity, updated);

        // TEST fromUuids(uuid)
        List<String> uuids = new ArrayList<>(1);
        uuids.add(entity.getUuid());
        List<GizmoEntity> entityList = mDao.fromUuids(uuids);
        assertNotNull("Entity after fromUuids(List)", entityList);
        assertEquals("List<GizmoEntity> after fromUuids(List) count incorrect", 1, entityList.size());
        assertEntitiesEqual(entity, entityList.get(0));

        // TEST setUpdatedAt(uuid, updatedAt)
        long newTime = updated.getUpdatedAt().getTime() + 2000;
        Date updatedAt = new Date(newTime);
        mDao.setUpdatedAt(updated.getUuid(), DateUtils.dateToSqlString(updatedAt));
        updated = mDao.fromUuid(updated.getUuid());
        assertNotNull("Entity after setUpdatedAt(uuid, updatedAt) null", updated);
        assertEquals("updatedAt timestamp incorrect", newTime, updated.getUpdatedAt().getTime());

        // TEST getForList()
        List<GizmoDao.ListItem> listItems = mDao.getForList();
        assertNotNull("List<GizmoDao.ListItem> after getForList() null", listItems);
        assertEquals("List<GizmoDao.ListItem> after getForList() count incorrect", 4, listItems.size());

        // TEST getAll()
        List<GizmoEntity> entities = mDao.getAll();
        assertNotNull("List<GizmoEntity> after getAll() null", entities);
        assertEquals("List<GizmoEntity> after getAll() count incorrect", 4, entities.size());

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
        GizmoEntity[] entitiesArr = new GizmoEntity[]{entities.get(2), entities.get(3)};
        count = mDao.delete(entitiesArr);
        assertEquals("deleteCount[entity[]...] not 2", 2, count);

        // TEST getAll() returns empty List after deletes
        entities = mDao.getAll();
        assertNotNull("List<GizmoEntity> after getAll() null", entities);
        assertEquals("List<GizmoEntity> after getAll() count incorrect", 0, entities.size());
    }

    @After
    public void tearDown() throws Exception {
        mDb.close();
    }

    private void assertEntitiesEqual(GizmoEntity entity1, GizmoEntity entity2) {
        assertNotNull("Entity 1 null", entity1);
        assertNotNull("Entity 2 null", entity1);
        assertEquals("UUIDs not equal", entity1.getUuid(), entity2.getUuid());
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
