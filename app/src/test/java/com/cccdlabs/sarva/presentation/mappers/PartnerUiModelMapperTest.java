package com.cccdlabs.sarva.presentation.mappers;

import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.presentation.mappers.partners.PartnerUiModelMapper;
import com.cccdlabs.sarva.presentation.model.partners.PartnerUiModel;

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
public class PartnerUiModelMapperTest {

    private PartnerUiModel uiModel1;
    private PartnerUiModel uiModel2;

    @Before
    public void setUp() throws Exception {
        Date now = new Date();

        uiModel1 = new PartnerUiModel();
        uiModel1.setId(1);
        uiModel1.setUuid("589a0b6e-5ce8-4759-944f-81f993b7694e");
        uiModel1.setUsername("Jerry");
        uiModel1.setDeviceName("Samsung J3");
        uiModel1.setActive(false);
        uiModel1.setEmitting(false);
        uiModel1.setDistance(128.00);
        uiModel1.setAccuracy(10);
        uiModel1.setRssi(-100);
        uiModel1.setTxPower(-64);
        uiModel1.setCreatedAt(now);
        uiModel1.setUpdatedAt(now);

        uiModel2 = new PartnerUiModel();
        uiModel2.setId(2);
        uiModel2.setUuid("86261454-f597-4586-8506-192ac6ddd471");
        uiModel2.setUsername("SkiBob");
        uiModel2.setDeviceName("LG Bello");
        uiModel2.setActive(true);
        uiModel2.setEmitting(true);
        uiModel2.setDistance(56.00);
        uiModel2.setAccuracy(8);
        uiModel2.setRssi(-128);
        uiModel2.setTxPower(-42);
        uiModel2.setCreatedAt(now);
        uiModel2.setUpdatedAt(now);
    }

    @Test
    public void WidgetMapper_shouldConvertUiModelToDomainAndBack() {
        // UI model model -> domain model
        Partner model = PartnerUiModelMapper.toDomainModel(uiModel1);
        assertNotNull("Converted domain object null", model);
        assertEqualValues(uiModel1, model);

        // domain model -> UI model model
        uiModel1 = PartnerUiModelMapper.fromDomainModel(model);
        assertNotNull("Converted UI model object null", uiModel1);
        assertEqualValues(uiModel1, model);
    }

    @Test
    public void WidgetMapper_shouldConvertUiModelToDomainListAndBack() {
        List<PartnerUiModel> uiModelList = new ArrayList<>(2);
        uiModelList.add(uiModel1);
        uiModelList.add(uiModel2);

        // List<UI model> -> List<domain>
        List<Partner> domainList = PartnerUiModelMapper.toDomainModel(uiModelList);
        assertNotNull("Converted UI model List null", domainList);
        assertEquals("Original UI model and converted domain List are not equal size", uiModelList.size(), domainList.size());
        for (int i=0; i < uiModelList.size(); i++) {
            assertEqualValues(uiModelList.get(i), domainList.get(i));
        }

        // List<domain> -> List<UI model>
        uiModelList = PartnerUiModelMapper.fromDomainModel(domainList);
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

    private void assertEqualValues(PartnerUiModel uiModel, Partner model) {
        assertNotNull("UI model null", uiModel);
        assertNotNull("Domain model null", model);
        assertEquals("IDs not equal", uiModel.getId(), model.getId());
        assertEquals("UUIDs not equal", uiModel.getUuid(), model.getUuid());
        assertEquals("Usernames not equal", uiModel.getUsername(), model.getUsername());
        assertEquals("Device names not equal", uiModel.getDeviceName(), model.getDeviceName());
        assertEquals("isActive not equal", uiModel.isActive(), model.isActive());
        assertEquals("isEmitting not equal", uiModel.isEmitting(), model.isEmitting());
        assertEquals("Distances not equal", uiModel.getDistance(), model.getDistance(), .01);
        assertEquals("Accuracy not equal", uiModel.getAccuracy(), model.getAccuracy());
        assertEquals("RSSI not equal", uiModel.getRssi(), model.getRssi());
        assertEquals("TX power not equal", uiModel.getTxPower(), model.getTxPower());

        assertNotNull("UI model getCreatedAt null", uiModel.getCreatedAt());
        assertNotNull("Domain model getCreatedAt null", model.getCreatedAt());
        assertEquals("createdAt not equal", uiModel.getCreatedAt().getTime(), model.getCreatedAt().getTime());

        assertNotNull("UI model getUpdatedAt null", uiModel.getUpdatedAt());
        assertNotNull("Domain model getUpdatedAt null", model.getUpdatedAt());
        assertEquals("updatedAt not equal", uiModel.getUpdatedAt().getTime(), model.getUpdatedAt().getTime());
    }
}
