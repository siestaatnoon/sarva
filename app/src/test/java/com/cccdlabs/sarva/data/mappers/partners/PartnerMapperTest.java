package com.cccdlabs.sarva.data.mappers.partners;

import com.cccdlabs.sarva.data.entity.partners.PartnerEntity;
import com.cccdlabs.sarva.domain.model.partners.Partner;

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
public class PartnerMapperTest {

    private static final String TEST_UUID_1 =  "589a0b6e-5ce8-4759-944f-81f993b7694e";
    private static final String TEST_UUID_2 =  "86261454-f597-4586-8506-192ac6ddd471";

    private PartnerMapper mMapper;
    private PartnerEntity entity1;
    private PartnerEntity entity2;

    @Before
    public void setUp() throws Exception {
        mMapper = new PartnerMapper();

        entity1 = new PartnerEntity();
        entity1.setId(1);
        entity1.setUuid(TEST_UUID_1);
        entity1.setUsername("Jerry");
        entity1.setDeviceName("Samsung J3");
        entity1.setActive(false);
        entity1.setEmitting(false);
        entity1.setDistance(128.00);
        entity1.setAccuracy(10);
        entity1.setRssi(-100);
        entity1.setTxPower(-64);
        entity1.touch();

        entity2 = new PartnerEntity();
        entity2.setId(2);
        entity2.setUuid(TEST_UUID_2);
        entity2.setUsername("SkiBob");
        entity2.setDeviceName("LG Bello");
        entity2.setActive(true);
        entity2.setEmitting(true);
        entity2.setDistance(56.00);
        entity2.setAccuracy(8);
        entity2.setRssi(-128);
        entity2.setTxPower(-42);
        entity2.touch();
    }

    @Test
    public void PartnerMapper_shouldConvertEntityToDomainAndBack() {
        // entity model -> domain model
        Partner model = mMapper.toDomainModel(entity1);
        assertNotNull("Converted domain object null", model);
        assertEqualValues(entity1, model);

        // domain model -> entity model
        entity1 = mMapper.fromDomainModel(model);
        assertNotNull("Converted entity object null", entity1);
        assertEqualValues(entity1, model);
    }

    @Test
    public void PartnerMapper_shouldConvertEntityToDomainListAndBack() {
        List<PartnerEntity> entityList = new ArrayList<>(2);
        entityList.add(entity1);
        entityList.add(entity2);

        // List<entity> -> List<domain>
        List<Partner> domainList = mMapper.toDomainModel(entityList);
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
        entity1 = null;
        entity2 = null;
    }

    private void assertEqualValues(PartnerEntity entity, Partner model) {
        assertNotNull("Entity null", entity);
        assertNotNull("Model null", model);
        assertEquals("IDs not equal", entity.getId(), model.getId());
        assertEquals("UUIDs not equal", entity.getUuid(), model.getUuid());
        assertEquals("Usernames not equal", entity.getUsername(), model.getUsername());
        assertEquals("Device names not equal", entity.getDeviceName(), model.getDeviceName());
        assertEquals("isActive not equal", entity.isActive(), model.isActive());
        assertEquals("isEmitting not equal", entity.isEmitting(), model.isEmitting());
        assertEquals("Distances not equal", entity.getDistance(), model.getDistance(), .01);
        assertEquals("Accuracy not equal", entity.getAccuracy(), model.getAccuracy());
        assertEquals("RSSI not equal", entity.getRssi(), model.getRssi());
        assertEquals("TX power not equal", entity.getTxPower(), model.getTxPower());

        assertNotNull("Entity getCreatedAt null", entity.getCreatedAt());
        assertNotNull("Model getCreatedAt null", model.getCreatedAt());
        assertEquals("createdAt not equal", entity.getCreatedAt().getTime(), model.getCreatedAt().getTime());

        assertNotNull("Entity getUpdatedAt null", entity.getUpdatedAt());
        assertNotNull("Model getUpdatedAt null", model.getUpdatedAt());
        assertEquals("updatedAt not equal", entity.getUpdatedAt().getTime(), model.getUpdatedAt().getTime());
    }
}
