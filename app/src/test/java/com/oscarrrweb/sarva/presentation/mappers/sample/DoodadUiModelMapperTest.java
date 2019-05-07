package com.oscarrrweb.sarva.presentation.mappers.sample;

import com.oscarrrweb.sarva.domain.model.sample.Doodad;
import com.oscarrrweb.sarva.presentation.model.sample.DoodadUiModel;

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
public class DoodadUiModelMapperTest {

    private static final String DOODAD_UUID_1 =  "589a0b6e-5ce8-4759-944f-81f993b7694e";
    private static final String DOODAD_UUID_2 =  "86261454-f597-4586-8506-192ac6ddd471";
    private static final String WIDGET_UUID =  "be50d7f6-bf8c-4bad-9869-56990737d1a2";

    private DoodadUiModel uiModel1;
    private DoodadUiModel uiModel2;

    @Before
    public void setUp() throws Exception {
        Date now = new Date();

        uiModel1 = new DoodadUiModel();
        uiModel1.setId(1);
        uiModel1.setUuid(DOODAD_UUID_1);
        uiModel1.setWidgetUuid(WIDGET_UUID);
        uiModel1.setName("Doodad One");
        uiModel1.setDescription("Four score and seven years ago...");
        uiModel1.setCreatedAt(now);
        uiModel1.setUpdatedAt(now);

        uiModel2 = new DoodadUiModel();
        uiModel2.setId(2);
        uiModel2.setUuid(DOODAD_UUID_2);
        uiModel2.setWidgetUuid(WIDGET_UUID);
        uiModel2.setName("Doodad Two");
        uiModel2.setDescription("We ate cake.");
        uiModel2.setCreatedAt(now);
        uiModel2.setUpdatedAt(now);
    }

    @Test
    public void WidgetMapper_shouldConvertUiModelToDomainAndBack() {
        // UI model model -> domain model
        Doodad model = DoodadUiModelMapper.toDomainModel(uiModel1);
        assertNotNull("Converted domain object null", model);
        assertEqualValues(uiModel1, model);

        // domain model -> UI model model
        uiModel1 = DoodadUiModelMapper.fromDomainModel(model);
        assertNotNull("Converted UI model object null", uiModel1);
        assertEqualValues(uiModel1, model);
    }

    @Test
    public void WidgetMapper_shouldConvertUiModelToDomainListAndBack() {
        List<DoodadUiModel> uiModelList = new ArrayList<>(2);
        uiModelList.add(uiModel1);
        uiModelList.add(uiModel2);

        // List<UI model> -> List<domain>
        List<Doodad> domainList = DoodadUiModelMapper.toDomainModel(uiModelList);
        assertNotNull("Converted UI model List null", domainList);
        assertEquals("Original UI model and converted domain List are not equal size", uiModelList.size(), domainList.size());
        for (int i=0; i < uiModelList.size(); i++) {
            assertEqualValues(uiModelList.get(i), domainList.get(i));
        }

        // List<domain> -> List<UI model>
        uiModelList = DoodadUiModelMapper.fromDomainModel(domainList);
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

    private void assertEqualValues(DoodadUiModel uiModel, Doodad model) {
        assertNotNull("UI model null", uiModel);
        assertNotNull("Domain model null", model);
        assertEquals("IDs not equal", uiModel.getId(), model.getId());
        assertEquals("UUIDs not equal", uiModel.getUuid(), model.getUuid());
        assertEquals("Widget UUIDs not equal", uiModel.getWidgetUuid(), model.getWidgetUuid());
        assertEquals("name not equal", uiModel.getName(), model.getName());
        assertEquals("description not equal", uiModel.getDescription(), model.getDescription());

        assertNotNull("UI model getCreatedAt null", uiModel.getCreatedAt());
        assertNotNull("Domain model getCreatedAt null", model.getCreatedAt());
        assertEquals("createdAt not equal", uiModel.getCreatedAt().getTime(), model.getCreatedAt().getTime());

        assertNotNull("UI model getUpdatedAt null", uiModel.getUpdatedAt());
        assertNotNull("Domain model getUpdatedAt null", model.getUpdatedAt());
        assertEquals("updatedAt not equal", uiModel.getUpdatedAt().getTime(), model.getUpdatedAt().getTime());
    }
}
