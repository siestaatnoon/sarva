package com.cccdlabs.sarva.data.repository.partners;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.cccdlabs.sarva.TestApp;
import com.cccdlabs.sarva.data.storage.database.AppDatabase;
import com.cccdlabs.sarva.domain.model.partners.Partner;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit test for PartnerRepository class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk=28, application= TestApp.class)
public class PartnerRepositoryTest {

    private AppDatabase mDb;
    private PartnerRepository mRepository;
    private Partner model1;
    private Partner model2;
    private Partner model3;

    @Before
    public void setUp() throws Exception {
        Context appContext = ApplicationProvider.getApplicationContext();
        TestDataComponent dataComponent = DaggerTestDataComponent.builder()
                .testDataModule(new TestDataModule(appContext, true))
                .build();

        mDb = dataComponent.appDatabase();
        mRepository = new PartnerRepository();
        dataComponent.inject(mRepository);

        model1 = new Partner();
        model1.setUuid("d4f74431-2f49-4acb-8f81-57c2ed67047a");
        model1.setUsername("Partner One");
        model1.setDeviceName("Samsung J3");
        model1.setActive(false);

        model2 = new Partner();
        model2.setUuid("421b7f06-c3de-4949-869b-fb1ee237c2f6");
        model2.setUsername("Partner Two");
        model2.setDeviceName("LG Bello");
        model2.setActive(true);

        model3 = new Partner();
        model3.setUuid("44cf4c02-542c-4f0c-acfd-1bbcce1bc30a");
        model3.setUsername("Partner Three");
        model3.setDeviceName("Samsung Galaxy S4");
        model3.setActive(true);
    }

    @Test
    public void shouldInsertAndGetByIdAndUuid() throws Exception {
        // TEST insert(model)
        int id = mRepository.insert(model1);

        // TEST getById(id)
        Partner model = mRepository.getById(id);
        assertNotNull("Model after getById(id)", model);
        assertModelsEqual(model1, model);

        // TEST update(model)
        model.setUsername("Partner One point One");
        model.setDeviceName("Nokia 8110");
        model.setActive(true);
        int count = mRepository.update(model);
        assertEquals("updateCount not 1", 1, count);

        // TEST getByUuid(uuid)
        Partner updated = mRepository.getByUuid(model.getUuid());
        assertNotNull("Model after getByUuid(uuid)", updated);
        assertModelsEqual(model, updated);
    }

    @Test
    public void shouldInsertAndGetAll() throws Exception {
        mRepository.insert(model1);
        mRepository.insert(model2);
        mRepository.insert(model3);

        List<Partner> models = mRepository.getAll();
        assertNotNull("List<Partner> after getAll() null", models);
        assertEquals("List<Partner> after getAll() count incorrect", 3, models.size());

        // entities can be returned in any order so we'll use a loop,
        // check each matching UUID and compare to the corresponding entity
        String uuid;
        for (Partner m : models) {
            uuid = m.getUuid();
            if (uuid.equals(model1.getUuid())) {
                assertModelsEqual(model1, m);
            } else if (uuid.equals(model2.getUuid())) {
                assertModelsEqual(model2, m);
            } else if (uuid.equals(model3.getUuid())) {
                assertModelsEqual(model3, m);
            } else {
                fail("Invalid UUID [" + uuid + "]");
            }
        }
    }

    @Test
    public void shouldDeleteByModelAndIdAndUuid() throws Exception {
        // TEST getAll()
        mRepository.insert(model1);
        mRepository.insert(model2);
        mRepository.insert(model3);

        List<Partner> models = mRepository.getAll();
        assertNotNull("List<Partner> after getAll() null", models);

        // TEST delete(model)
        Partner model = models.get(0);
        int count = mRepository.delete(model);
        assertEquals("deleteCount[model] not 1", 1, count);

        // TEST delete(id)
        model = models.get(1);
        int id = model.getId();
        assertNotNull("Model for DELETE by ID null", model);
        count = mRepository.delete(id);
        assertEquals("deleteCount[ID] not 1", 1, count);

        // TEST delete(uuid)
        model = models.get(2);
        String uuid = model.getUuid();
        assertNotNull("Model for DELETE by UUID null", model);
        count = mRepository.delete(uuid);
        assertEquals("deleteCount[UUID] not 1", 1, count);

        // TEST getAll() returns empty List after deletes
        models = mRepository.getAll();
        assertNotNull("List<Partner> after getAll() null", models);
        assertEquals("List<Partner> after getAll() count incorrect", 0, models.size());
    }

    @Test
    public void shouldDeleteByModelList() throws Exception {
        int id = mRepository.insert(model1);
        model1.setId(id);
        id = mRepository.insert(model2);
        model2.setId(id);
        id = mRepository.insert(model3);
        model3.setId(id);
        List<Partner> list = new ArrayList<>(3);
        list.add(model1);
        list.add(model2);
        list.add(model3);
        int count = mRepository.delete(list);
        assertEquals("deleteCount[List<Partner>] not 3", 3, count);

        // TEST getAll() returns empty List after deletes
        List<Partner> models = mRepository.getAll();
        assertNotNull("List<Partner> after delete(List<Partner>) null", models);
        assertEquals("List<Partner> after delete(List<Partner>) count incorrect", 0, models.size());
    }

    @Test
    public void shouldGetAllActive() throws Exception {
        mRepository.insert(model1);
        mRepository.insert(model2);
        mRepository.insert(model3);

        List<Partner> models = mRepository.getAllActive();
        assertNotNull("List<Partner> after getAll() null", models);
        assertEquals("List<Partner> after getAll() count incorrect", 2, models.size());

        // entities can be returned in any order so we'll use a loop,
        // check each matching UUID and compare to the corresponding entity
        String uuid;
        for (Partner m : models) {
            uuid = m.getUuid();
            if (uuid.equals(model1.getUuid())) {
                assertModelsEqual(model1, m);
            } else if (uuid.equals(model2.getUuid())) {
                assertModelsEqual(model2, m);
            } else if (uuid.equals(model3.getUuid())) {
                assertModelsEqual(model3, m);
            } else {
                fail("Invalid UUID [" + uuid + "]");
            }
        }
    }

    @Test
    public void shouldSetAndReturnIsActive() throws Exception {
        model1.setActive(false);
        int id = mRepository.insert(model1);
        Partner model = mRepository.getById(id);
        assertNotNull("Model after getById(id)", model);
        assertFalse("Model isActive true", model.isActive());

        mRepository.setActive(model.getUuid());
        model = mRepository.getById(id);
        assertTrue("After setActive(uuid) call false", model.isActive());

        mRepository.setInactive(model.getUuid());
        model = mRepository.getById(id);
        assertFalse("After setInactive(uuid) call true", model.isActive());
    }

    @Test
    public void shouldSync() throws Exception {
        Partner model = mRepository.sync(model1);
        assertNotNull("Model after sync(model)", model);

        Partner model2 = mRepository.getById(model.getId());
        assertNotNull("Model after getById(id)", model);
        assertModelsEqual(model, model2);
    }

    @Test
    public void shouldSetAllInactive() throws Exception {
        model1.setActive(true);
        model2.setActive(true);
        model3.setActive(true);
        mRepository.insert(model1);
        mRepository.insert(model2);
        mRepository.insert(model3);

        // first make sure all items are active
        List<Partner> models = mRepository.getAll();
        assertNotNull("List<Partner> after getAll() null", models);
        assertEquals("List<Partner> after getAll() count incorrect", 3, models.size());
        for (int i=0; i < models.size(); i++) {
            Partner model = models.get(i);
            assertTrue("Partner[" + i + "] is false", model.isActive());
        }

        mRepository.setAllInactive();
        models = mRepository.getAll();

        assertNotNull("List<Partner> after second getAll() null", models);
        assertEquals("List<Partner> after second getAll() count incorrect", 3, models.size());
        for (int i=0; i < models.size(); i++) {
            Partner model = models.get(i);
            assertFalse("Partner[" + i + "] is true", model.isActive());
        }
    }

    @After
    public void tearDown() throws Exception {
        mDb.close();
    }

    private void assertModelsEqual(Partner model1, Partner model2) {
        assertNotNull("Partner 1 null", model1);
        assertNotNull("Partner 2 null", model2);
        assertEquals("UUIDs not equal", model1.getUuid(), model2.getUuid());
        assertEquals("Usernames not equal", model1.getUsername(), model2.getUsername());
        assertEquals("Device names not equal", model1.getDeviceName(), model2.getDeviceName());
        assertEquals("isActive not equal", model1.isActive(), model2.isActive());
        assertNotNull("Partner 2 getCreatedAt null", model2.getCreatedAt());
        assertNotNull("Partner 2 getUpdatedAt null", model2.getUpdatedAt());
    }
}