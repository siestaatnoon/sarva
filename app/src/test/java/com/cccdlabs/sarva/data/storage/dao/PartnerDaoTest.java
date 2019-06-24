package com.cccdlabs.sarva.data.storage.dao;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.cccdlabs.sarva.TestApp;
import com.cccdlabs.sarva.data.entity.partners.PartnerEntity;
import com.cccdlabs.sarva.data.storage.dao.partners.PartnerDao;
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

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28, application=TestApp.class)
public class PartnerDaoTest {

    private AppDatabase mDb;
    private PartnerDao mDao;
    private PartnerEntity entity1;
    private PartnerEntity entity2;
    private PartnerEntity entity3;
    private PartnerEntity entity4;

    @Before
    public void setUp() throws Exception {
        Context appContext = ApplicationProvider.getApplicationContext();
        TestDataComponent dataComponent = DaggerTestDataComponent.builder()
                .testDataModule(new TestDataModule(appContext, true))
                .build();

        mDb = dataComponent.appDatabase();
        mDao = dataComponent.partnerDao();

        entity1 = new PartnerEntity();
        entity1.setUuid("d4f74431-2f49-4acb-8f81-57c2ed67047a");
        entity1.setUsername("Partner One");
        entity1.setDeviceName("Samsung J3");
        entity1.setActive(false);
        entity1.touch();

        entity2 = new PartnerEntity();
        entity2.setUuid("421b7f06-c3de-4949-869b-fb1ee237c2f6");
        entity2.setUsername("Partner Two");
        entity2.setDeviceName("LG Bello");
        entity2.setActive(true);
        entity2.touch();

        entity3 = new PartnerEntity();
        entity3.setUuid("44cf4c02-542c-4f0c-acfd-1bbcce1bc30a");
        entity3.setUsername("Partner Three");
        entity3.setDeviceName("Samsung Galaxy S4");
        entity3.setActive(false);
        entity3.touch();

        entity4 = new PartnerEntity();
        entity4.setUuid("58fce4cd-0f1c-4331-80d1-c057f71caa76");
        entity4.setUsername("Partner Four");
        entity4.setDeviceName("Google Pixel 3");
        entity4.setActive(true);
        entity4.touch();
    }

    @Test
    public void shouldInsertAndGetFromId() throws Exception {
        long id = mDao.insert(entity1);
        assertTrue("ID is zero", id > 0);

        PartnerEntity entity = mDao.fromId((int) id);
        assertNotNull("Entity after fromId(id)", entity);
        assertEntitiesEqual(entity1, entity);
    }

    @Test
    public void shouldInsertAndGetFromUuid() throws Exception {
        long id = mDao.insert(entity1);
        assertTrue("ID is zero", id > 0);

        PartnerEntity entity = mDao.fromUuid(entity1.getUuid());
        assertNotNull("Entity after fromUuid(uuid)", entity);
        assertEntitiesEqual(entity1, entity);
    }

    @Test
    public void shouldUpdate() throws Exception {
        long id = mDao.insert(entity1);
        assertTrue("ID is zero", id > 0);

        PartnerEntity entity = mDao.fromUuid(entity1.getUuid());
        assertNotNull("Entity after fromUuid(uuid)", entity);

        entity.setUsername("Partner One point One");
        entity.setDeviceName("Nokia 8110");
        entity.setActive(true);
        entity.touch(); // update timestamps
        int count = mDao.update(entity);

        assertEquals("updateCount not 1", 1, count);

        PartnerEntity entity2 = mDao.fromUuid(entity1.getUuid());
        assertEntitiesEqual(entity, entity2);
    }

    @Test
    public void shouldInsertMultipleAndGetAll() throws Exception {
        PartnerEntity[] entities = new PartnerEntity[]{entity1, entity2, entity3, entity4};
        long[] ids = mDao.insert(entities);
        assertTrue("IDs count is zero", ids.length > 0);
        for (int i=0; i < ids.length; i++) {
            assertTrue("ID[" + i + "] is zero", ids[i] > 0);
        }

        List<PartnerEntity> entityList = mDao.getAll();
        assertNotNull("List<PartnerEntity> after getAll() null", entityList);
        assertEquals("List<PartnerEntity> after getAll() count incorrect", 4, entityList.size());

        // entities can be returned in any order so we'll use a loop,
        // check each matching UUID and compare to the corresponding entity
        String uuid;
        for (PartnerEntity entity : entityList) {
            uuid = entity.getUuid();
            if (uuid.equals(entity1.getUuid())) {
                assertEntitiesEqual(entity1, entity);
            } else if (uuid.equals(entity2.getUuid())) {
                assertEntitiesEqual(entity2, entity);
            } else if (uuid.equals(entity3.getUuid())) {
                assertEntitiesEqual(entity3, entity);
            } else if (uuid.equals(entity4.getUuid())) {
                assertEntitiesEqual(entity4, entity);
            } else {
                fail("Invalid UUID [" + uuid + "]");
            }
        }
    }

    @Test
    public void shouldDeleteByEntityAndUuidAndArray() throws Exception {
        long[] ids = mDao.insert(new PartnerEntity[]{entity1, entity2, entity3, entity4});
        assertTrue("IDs count is zero", ids.length > 0);
        for (int i=0; i < ids.length; i++) {
            assertTrue("ID[" + i + "] is zero", ids[i] > 0);
        }

        List<PartnerEntity> entities = mDao.getAll();
        assertNotNull("List<PartnerEntity> after getAll() null", entities);
        assertEquals("List<PartnerEntity> after getAll() count incorrect", 4, entities.size());

        // TEST delete(entity)
        PartnerEntity entity = entities.get(0);
        int count = mDao.delete(entity);
        assertEquals("deleteCount[entity] not 1", 1, count);

        // TEST delete(uuid)
        entity = entities.get(1);
        String uuid = entity.getUuid();
        assertNotNull("Entity for DELETE by UUID null", entity);
        count = mDao.delete(uuid);
        assertEquals("deleteCount[UUID] not 1", 1, count);

        // TEST delete(entity[]...)
        PartnerEntity[] entitiesArr = new PartnerEntity[]{entities.get(2), entities.get(3)};
        count = mDao.delete(entitiesArr);
        assertEquals("deleteCount[entity[]...] not 2", 2, count);

        // TEST getAll() returns empty List after deletes
        entities = mDao.getAll();
        assertNotNull("List<PartnerEntity> after getAll() null", entities);
        assertEquals("List<PartnerEntity> after getAll() count incorrect", 0, entities.size());
    }

    @Test
    public void shouldSetUpdatedAt() throws Exception {
        long id = mDao.insert(entity1);
        assertTrue("ID is zero", id > 0);

        PartnerEntity entity = mDao.fromUuid(entity1.getUuid());
        assertNotNull("Entity after fromUuid(uuid)", entity);
        assertEntitiesEqual(entity1, entity);

        // TEST setUpdatedAt(uuid, updatedAt)
        long newTime = entity.getUpdatedAt().getTime() + 2000;
        Date updatedAt = new Date(newTime);
        mDao.setUpdatedAt(entity.getUuid(), DateUtils.dateToSqlString(updatedAt));
        entity = mDao.fromUuid(entity.getUuid());
        assertNotNull("Entity after setUpdatedAt(uuid, updatedAt) null", entity);
        assertEquals("updatedAt timestamp incorrect", newTime, entity.getUpdatedAt().getTime());
    }

    @Test
    public void shouldGetActive() throws Exception {
        entity1.setActive(true);
        entity2.setActive(false);
        entity3.setActive(true);
        entity4.setActive(false);
        long[] ids = mDao.insert(new PartnerEntity[]{entity1, entity2, entity3, entity4});
        assertTrue("IDs count is zero", ids.length > 0);
        for (int i=0; i < ids.length; i++) {
            assertTrue("ID[" + i + "] is zero", ids[i] > 0);
        }

        List<PartnerEntity> entities = mDao.getActive();
        assertNotNull("List<PartnerEntity> after getActive() null", entities);
        assertEquals("List<PartnerEntity> after getActive() count incorrect", 2, entities.size());
    }

    @Test
    public void shouldGetInactive() throws Exception {
        entity1.setActive(true);
        entity2.setActive(false);
        entity3.setActive(false);
        entity4.setActive(false);
        long[] ids = mDao.insert(new PartnerEntity[]{entity1, entity2, entity3, entity4});
        assertTrue("IDs count is zero", ids.length > 0);
        for (int i=0; i < ids.length; i++) {
            assertTrue("ID[" + i + "] is zero", ids[i] > 0);
        }

        List<PartnerEntity> entities = mDao.getInactive();
        assertNotNull("List<PartnerEntity> after getInactive() null", entities);
        assertEquals("List<PartnerEntity> after getInactive() count incorrect", 3, entities.size());
    }

    @Test
    public void shouldSetActive() throws Exception {
        entity1.setActive(false);
        long id = mDao.insert(entity1);
        assertTrue("ID is zero", id > 0);

        PartnerEntity entity = mDao.fromUuid(entity1.getUuid());
        assertNotNull("Entity after fromUuid(uuid)", entity);
        assertFalse("isActive is true", entity.isActive());

        mDao.setActive(entity.getUuid());
        entity = mDao.fromUuid(entity1.getUuid());
        assertNotNull("Entity after second fromUuid(uuid)", entity);
        assertTrue("isActive is false", entity.isActive());
    }

    @Test
    public void shouldSetInactive() throws Exception {
        entity1.setActive(true);
        long id = mDao.insert(entity1);
        assertTrue("ID is zero", id > 0);

        PartnerEntity entity = mDao.fromUuid(entity1.getUuid());
        assertNotNull("Entity after fromUuid(uuid)", entity);
        assertTrue("isActive is false", entity.isActive());

        mDao.setInactive(entity.getUuid());
        entity = mDao.fromUuid(entity1.getUuid());
        assertNotNull("Entity after second fromUuid(uuid)", entity);
        assertFalse("isActive is true", entity.isActive());
    }

    @Test
    public void shouldSetAllInactive() throws Exception {
        entity1.setActive(true);
        entity2.setActive(true);
        entity3.setActive(true);
        entity4.setActive(true);
        long[] ids = mDao.insert(new PartnerEntity[]{entity1, entity2, entity3, entity4});
        assertTrue("IDs count is zero", ids.length > 0);
        for (int i=0; i < ids.length; i++) {
            assertTrue("ID[" + i + "] is zero", ids[i] > 0);
        }

        List<PartnerEntity> entities = mDao.getAll();
        assertNotNull("List<PartnerEntity> after getAll() null", entities);
        assertEquals("List<PartnerEntity> after getAll() count incorrect", 4, entities.size());
        for (int i=0; i < entities.size(); i++) {
            PartnerEntity entity = entities.get(i);
            assertTrue("PartnerEntity[" + i + "] is false", entity.isActive());
        }

        mDao.setAllInactive();
        entities = mDao.getAll();
        assertNotNull("List<PartnerEntity> after second getAll() null", entities);
        assertEquals("List<PartnerEntity> after second getAll() count incorrect", 4, entities.size());
        for (int i=0; i < entities.size(); i++) {
            PartnerEntity entity = entities.get(i);
            assertFalse("PartnerEntity[" + i + "] is true", entity.isActive());
        }
    }

    @After
    public void tearDown() throws Exception {
        mDb.close();
    }

    private void assertEntitiesEqual(PartnerEntity entity1, PartnerEntity entity2) {
        assertNotNull("Entity 1 null", entity1);
        assertNotNull("Entity 2 null", entity2);
        assertEquals("UUIDs not equal", entity1.getUuid(), entity2.getUuid());
        assertEquals("Usernames not equal", entity1.getUsername(), entity2.getUsername());
        assertEquals("Device names not equal", entity1.getDeviceName(), entity2.getDeviceName());
        assertEquals("isActive not equal", entity1.isActive(), entity2.isActive());

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
