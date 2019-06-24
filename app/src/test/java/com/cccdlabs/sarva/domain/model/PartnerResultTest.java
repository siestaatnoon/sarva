package com.cccdlabs.sarva.domain.model;

import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static com.cccdlabs.sarva.domain.model.partners.PartnerResult.PublishStatus.INVALID;
import static com.cccdlabs.sarva.domain.model.partners.PartnerResult.PublishStatus.NOT_PUBLISHING;
import static com.cccdlabs.sarva.domain.model.partners.PartnerResult.PublishStatus.PUBLISHING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PartnerResultTest {

    @Test
    public void shouldStorePartner() throws Exception {
        Partner partner = new Partner();
        partner.setUuid("bc317a3f-099f-42c1-bc13-d4aa9998c681");
        PartnerResult partnerResult = new PartnerResult(partner);

        assertNotNull(partnerResult.getPartner());
        assertNotNull(partnerResult.getUuid());
        assertNull(partnerResult.getException());
        assertTrue("hasResult() false", partnerResult.hasResult());
        assertFalse("hasResult() true", partnerResult.hasError());
        assertEquals("getPublishStatus() not PartnerResult.INVALID", INVALID, partnerResult.getPublishStatus());
    }

    @Test
    public void shouldStorePublishStatusTrue() throws Exception {
        PartnerResult partnerResult = new PartnerResult(true);

        assertNull(partnerResult.getPartner());
        assertNull(partnerResult.getUuid());
        assertNull(partnerResult.getException());
        assertFalse("hasResult() true", partnerResult.hasResult());
        assertFalse("hasError() true", partnerResult.hasError());
        assertEquals("getPublishStatus() not PartnerResult.PUBLISHING", PUBLISHING, partnerResult.getPublishStatus());
    }

    @Test
    public void shouldStorePublishStatusFalse() throws Exception {
        PartnerResult partnerResult = new PartnerResult(false);

        assertNull(partnerResult.getPartner());
        assertNull(partnerResult.getUuid());
        assertNull(partnerResult.getException());
        assertFalse("hasResult() true", partnerResult.hasResult());
        assertFalse("hasError() true", partnerResult.hasError());
        assertEquals("getPublishStatus() not PartnerResult.NOT_PUBLISHING", NOT_PUBLISHING, partnerResult.getPublishStatus());
    }

    @Test
    public void shouldStoreException() throws Exception {
        PartnerResult partnerResult = new PartnerResult(new Exception("Test error"));

        assertNull(partnerResult.getPartner());
        assertNull(partnerResult.getUuid());
        assertNotNull(partnerResult.getException());
        assertFalse("hasResult() true", partnerResult.hasResult());
        assertTrue("hasError() false", partnerResult.hasError());
        assertEquals("getPublishStatus() not PartnerResult.INVALID", INVALID, partnerResult.getPublishStatus());
    }
}
