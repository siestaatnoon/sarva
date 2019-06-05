package com.cccdlabs.sarva.data.repository.sample;

import android.content.Context;

import com.cccdlabs.sarva.TestApp;
import com.cccdlabs.sarva.data.entity.sample.GizmoEntity;
import com.cccdlabs.sarva.data.entity.sample.WidgetEntity;
import com.cccdlabs.sarva.data.storage.database.AppDatabase;
import com.cccdlabs.sarva.domain.model.sample.Doodad;
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

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Unit test for DoodadRepository class. While trying various configurations including
 * {@link @BeforeClass} and {@link @AfterClass}, it was decided that, for best performance,
 * a single test would be best although not very elegant. Initializing database connections
 * can get pretty expensive.
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk=28, application= TestApp.class)
public class DoodadRepositoryTest {

    private static final String WIDGET_UUID =  "ed204c50-14ca-4a9c-91aa-137e10a525fb";
    private static final String GIZMO_UUID =  "c3dd0e16-e784-4d4b-976e-81297197cbd9";

    private AppDatabase mDb;
    private DoodadRepository mRepository;
    private Doodad model1;
    private Doodad model2;
    private Doodad model3;

    @Before
    public void setUp() throws Exception {
        Context appContext = ApplicationProvider.getApplicationContext();
        TestDataComponent dataComponent = DaggerTestDataComponent.builder()
                .testDataModule(new TestDataModule(appContext))
                .build();

        mDb = dataComponent.appDatabase();
        mRepository = new DoodadRepository();
        dataComponent.inject(mRepository);

        model1 = new Doodad();
        model1.setUuid("f9f44d55-fbe3-4d55-a444-1ba899e7c9c7");
        model1.setWidgetUuid(WIDGET_UUID);
        model1.setName("Doodad One");
        model1.setDescription("Whooptee doo");

        model2 = new Doodad();
        model2.setUuid("24c8ae49-6cef-4702-9aaf-c2471caca3ce");
        model2.setWidgetUuid(WIDGET_UUID);
        model2.setName("Doodad Two");
        model2.setDescription("Whooptee doo too");

        model3 = new Doodad();
        model3.setUuid("e77f6b2e-2f26-4e33-bfee-ef5e8827193f");
        model3.setWidgetUuid(WIDGET_UUID);
        model3.setName("Doodad Three");
        model3.setDescription("Whooptee doo too three");

        // Add a gizmo so we don't get an SQL foreign key error
        GizmoEntity gizmo = new GizmoEntity();
        gizmo.setUuid(GIZMO_UUID);
        gizmo.setName("Gizmo One");
        gizmo.setDescription("One, two, three");
        gizmo.touch();
        dataComponent.gizmoDao().insert(gizmo);

        // Add a widget so we don't get an SQL foreign key error
        WidgetEntity widget = new WidgetEntity();
        widget.setUuid(WIDGET_UUID);
        widget.setName("Widget One");
        widget.setDescription("One, two, three and a four");
        widget.touch();
        dataComponent.widgetDao().insert(widget);
    }

    @Test
    public void DoodadRepository_shouldPerformAllFuntions() throws Exception {
        // TEST insert(model)
        int id = mRepository.insert(model1);

        // TEST getById(id)
        Doodad model = mRepository.getById(id);
        assertNotNull("Model after getById(id)", model);
        assertModelsEqual(model1, model);

        // TEST update(model)
        model.setName("DoodadEntity One point One");
        model.setDescription("Whooptee doo daa");
        int count = mRepository.update(model);
        assertEquals("updateCount not 1", 1, count);

        // TEST getByUuid(uuid)
        Doodad updated = mRepository.getByUuid(model.getUuid());
        assertNotNull("Model after getByUuid(uuid)", updated);
        assertModelsEqual(model, updated);

        // TEST getAll()
        model1 = model;
        mRepository.insert(model2);
        mRepository.insert(model3);

        List<Doodad> models = mRepository.getAll();
        assertNotNull("List<Doodad> after getAll() null", models);
        assertEquals("List<Doodad> after getAll() count incorrect", 3, models.size());

        // entities can be returned in any order so we'll use a loop,
        // check each matching UUID and compare to the corresponding entity
        String uuid;
        for (Doodad m : models) {
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

        // TEST delete(model)
        model = models.get(0);
        count = mRepository.delete(model);
        assertEquals("deleteCount[model] not 1", 1, count);

        // TEST delete(id)
        model = models.get(1);
        id = model.getId();
        assertNotNull("Model for DELETE by ID null", model);
        count = mRepository.delete(id);
        assertEquals("deleteCount[ID] not 1", 1, count);

        // TEST delete(uuid)
        model = models.get(2);
        uuid = model.getUuid();
        assertNotNull("Model for DELETE by UUID null", model);
        count = mRepository.delete(uuid);
        assertEquals("deleteCount[UUID] not 1", 1, count);

        // TEST getAll() returns empty List after deletes
        models = mRepository.getAll();
        assertNotNull("List<Doodad> after getAll() null", models);
        assertEquals("List<Doodad> after getAll() count incorrect", 0, models.size());

        // TEST delete(List<Doodad>)
        id = mRepository.insert(model1);
        model1.setId(id);
        id = mRepository.insert(model2);
        model2.setId(id);
        id = mRepository.insert(model3);
        model3.setId(id);
        List<Doodad> list = new ArrayList<>(3);
        list.add(model1);
        list.add(model2);
        list.add(model3);
        count = mRepository.delete(list);
        assertEquals("deleteCount[List<Doodad>] not 3", 3, count);

        // TEST getAll() returns empty List after deletes
        models = mRepository.getAll();
        assertNotNull("List<Doodad> after delete(List<Doodad>) null", models);
        assertEquals("List<Doodad> after delete(List<Doodad>) count incorrect", 0, models.size());
    }

    @After
    public void tearDown() throws Exception {
        mDb.close();
    }

    private void assertModelsEqual(Doodad model1, Doodad model2) {
        assertNotNull("Doodad 1 null", model1);
        assertNotNull("Doodad 2 null", model1);
        assertEquals("UUIDs not equal", model1.getUuid(), model2.getUuid());
        assertEquals("Widget UUIDs not equal", model1.getWidgetUuid(), model2.getWidgetUuid());
        assertEquals("name not equal", model1.getName(), model2.getName());
        assertEquals("description not equal", model1.getDescription(), model2.getDescription());
        assertNotNull("createdAt null value", model2.getCreatedAt());
        assertNotNull("updatedAt null value", model2.getUpdatedAt());
    }
}