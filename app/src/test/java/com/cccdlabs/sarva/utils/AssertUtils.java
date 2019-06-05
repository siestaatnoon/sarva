package com.cccdlabs.sarva.utils;

import com.cccdlabs.sarva.data.entity.TestEntity;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

final public class AssertUtils {

    /**
     * Asserts two {@link TestEntity}s are equal.
     */
    public static void assertEntitiesEqual(TestEntity entity1, TestEntity entity2) {
        assertNotNull("entity1 null", entity1);
        assertNotNull("entity2 null", entity2);
        assertEquals("id not correct value", entity1.getId(), entity2.getId());
        assertEquals("uuid not correct value", entity1.getUuid(), entity2.getUuid());

        assertNotNull("createdAt null", entity2.getCreatedAt());
        assertNotNull("updatedAt null", entity2.getUpdatedAt());

        // need to account for string timestamps precision
        // to only seconds upon converting from JSON
        long time =  entity1.getCreatedAt().getTime();
        long createdAt = time - (time % 1000);
        time =  entity1.getUpdatedAt().getTime();
        long updatedAt = time - (time % 1000);
        assertEquals("createdAt not correct value", createdAt, entity2.getCreatedAt().getTime());
        assertEquals("updatedAt not correct value", updatedAt, entity2.getUpdatedAt().getTime());

        assertTrue("imageFile not correct value", Arrays.equals(entity1.getImageFile(), entity2.getImageFile()));
        assertEquals("isTest not correct value", entity1.isTest(), entity2.isTest());
    }

    /**
     * Checks for valid YYYY-MM-DD or YYYY-MM-DD HH:MM:SS date string.
     */
    public static void assertValidDateString(String objName, String str) {
        if (objName == null) {
            objName = "Date string";
        }

        assertNotNull(objName + " null", str);
        int length = str.length();
        if (length != 10 && length != 19) {
            fail(objName + " not a valid date string, length: " + length);
        }

        // regex matching YYYY-MM-DD or YYYY-MM-DD HH:MM:SS
        String dateTimeRegex = "^([2][01]|[1][6-9])\\d{2}-([0]\\d|[1][0-2])-([0-2]\\d|[3][0-1])(\\s([0-1]\\d|[2][0-3])(\\:[0-5]\\d){1,2})?$";
        assertTrue(objName + " not a valid date string, value: " + str, str.matches(dateTimeRegex));
    }
}
