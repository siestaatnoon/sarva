package com.cccdlabs.sarva.data.entity.base;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.cccdlabs.sarva.data.entity.TestEntity;
import com.cccdlabs.sarva.utils.AssertUtils;
import com.cccdlabs.sarva.utils.FileUtils;
import com.cccdlabs.sarva.utils.JsonUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class EntityTest {

    private static final int TEST_ID_1 = 1;
    private static final int TEST_ID_2 = 2;
    private static final String TEST_UUID_1 = "bbbd140f-2f60-44f7-bf1d-557820b64554";
    private static final String TEST_UUID_2 = "d287d73b-22b8-4a9b-a26d-3d31677cb787";

    private TestEntity mEntity1;
    private TestEntity mEntity2;

    private class TestEntityWithExclusion extends TestEntity {
        @Override
        protected List<ExclusionStrategy> getExclusionStrategies() {
            List<ExclusionStrategy> strategies = super.getExclusionStrategies();
            ExclusionStrategy strategy = new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getName().equals("imageFile");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            };

            strategies.add(strategy);
            return strategies;
        }
    }

    private class TestEntityWithPolicy extends TestEntity {

        private String testString;

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) {
            this.testString = testString;
        }

        @Override
        public FieldNamingPolicy getFieldNamingPolicy() {
            return FieldNamingPolicy.LOWER_CASE_WITH_DASHES;
        }
    }

    private class TestEntityWithStrategy extends TestEntity {

        private String testString;

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) {
            this.testString = testString;
        }

        @Override
        public FieldNamingStrategy getFieldNamingStrategy() {
            return new FieldNamingStrategy() {
                @Override
                public String translateName(Field f) {
                    String name = f.getName();
                    return name.equals("testString") ? "altered-test-string" : name;
                }
            };
        }
    }

    @Before
    public void setUp() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();

        mEntity1 = new TestEntity();
        mEntity1.setId(TEST_ID_1);
        mEntity1.setUuid(TEST_UUID_1);
        mEntity1.setImageFile(FileUtils.getSampleImageBytesFromAndroidRes(context));
        mEntity1.setTest(true);
        mEntity1.touch();

        mEntity2 = new TestEntity();
        mEntity2.setId(TEST_ID_2);
        mEntity2.setUuid(TEST_UUID_2);
        mEntity2.setImageFile(FileUtils.getSampleImageBytesFromTestResources());
        mEntity2.setTest(false);
        mEntity2.touch();
    }

    @Test
    public void Entity_shouldConvertFromJsonString() throws Exception {
        // NOTE: testing this method as well, otherwise id not included in JSON output
        mEntity1.hasSerializedId(true);

        String jsonStr = mEntity1.toJson();
        TestEntity entity = (TestEntity) Entity.fromJson(jsonStr, TestEntity.class);
        assertNotNull("Entity.fromJson(String, Class<T>) returned null object", entity);
        AssertUtils.assertEntitiesEqual(mEntity1, entity);
    }

    @Test
    public void Entity_shouldConvertFromJsonElement() throws Exception {
        mEntity1.hasSerializedId(true);
        String jsonStr = mEntity1.toJson();
        JsonElement jsonElement = JsonUtils.toJsonElement(jsonStr);
        TestEntity entity = (TestEntity) Entity.fromJson(jsonElement, TestEntity.class);
        assertNotNull("Entity.fromJson(JsonElement, Class<T>) returned null object", entity);
        AssertUtils.assertEntitiesEqual(mEntity1, entity);
    }

    @Test
    public void Entity_shouldConvertFromJsonObject() throws Exception {
        mEntity1.hasSerializedId(true);
        String jsonStr = mEntity1.toJson();
        JsonObject jsonObject = JsonUtils.toJsonObject(jsonStr);
        TestEntity entity = (TestEntity) Entity.fromJson(jsonObject, TestEntity.class);
        assertNotNull("Entity.fromJson(JsonObject, Class<T>) returned null object", entity);
        AssertUtils.assertEntitiesEqual(mEntity1, entity);
    }

    @Test
    public void Entity_shouldConvertFromJsonArrayString() throws Exception {
        mEntity1.hasSerializedId(true);
        mEntity2.hasSerializedId(true);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(JsonUtils.toJsonElement(mEntity1.toJson()));
        jsonArray.add(JsonUtils.toJsonElement(mEntity2.toJson()));
        String jsonStr = jsonArray.toString();
        List<Entity> entities = Entity.fromJsonArray(jsonStr, TestEntity.class);

        assertNotNull("Entity.fromJsonArray(String, Class<T>) returned null object", entities);
        assertEquals("entities.size not 2", 2, entities.size());
        AssertUtils.assertEntitiesEqual(mEntity1, (TestEntity) entities.get(0));
        AssertUtils.assertEntitiesEqual(mEntity2, (TestEntity) entities.get(1));
    }

    @Test
    public void Entity_shouldConvertFromJsonArray() throws Exception {
        mEntity1.hasSerializedId(true);
        mEntity2.hasSerializedId(true);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(JsonUtils.toJsonElement(mEntity1.toJson()));
        jsonArray.add(JsonUtils.toJsonElement(mEntity2.toJson()));
        List<Entity> entities = Entity.fromJsonArray(jsonArray, TestEntity.class);

        assertNotNull("Entity.fromJsonArray(JsonArray, Class<T>) returned null object", entities);
        assertEquals("entities.size not 2", 2, entities.size());
        AssertUtils.assertEntitiesEqual(mEntity1, (TestEntity) entities.get(0));
        AssertUtils.assertEntitiesEqual(mEntity2, (TestEntity) entities.get(1));
    }

    @Test
    public void Entity_shouldConvertToJson() throws Exception {
        String jsonStr = mEntity1.toJson();
        JsonObject jsonObject = JsonUtils.toJsonObject(jsonStr);
        assertNotNull("toJson() results in null string", jsonObject);

        // make sure id not serialized, per default
        assertFalse("id is present in JSON", jsonObject.has("id"));

        assertTrue("uuid not contained in JSON", jsonObject.has("uuid"));
        assertEquals("uuid not correct value", TEST_UUID_1, jsonObject.get("uuid").getAsString());

        assertTrue("created_at not contained in JSON", jsonObject.has("created_at"));
        AssertUtils.assertValidDateString("created_at", jsonObject.get("created_at").getAsString());

        assertTrue("updated_at not contained in JSON", jsonObject.has("updated_at"));
        AssertUtils.assertValidDateString("updated_at", jsonObject.get("updated_at").getAsString());

        assertTrue("image_file not contained in JSON", jsonObject.has("image_file"));
        assertNotNull("image_file null", jsonObject.get("image_file").getAsString());

        assertTrue("is_test not contained in JSON", jsonObject.has("is_test"));
        assertEquals("is_test not correct value", 1, jsonObject.get("is_test").getAsInt());

        //System.out.println(jsonObject.get("image_file").getAsString());
    }

    @Test
    public void Entity_shouldGenerateUuid() throws Exception {
        String uuidRegex = "^([0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12})$";
        TestEntity entity = new TestEntity();
        entity.setUuid();
        assertTrue("UUID not correct format", entity.getUuid().matches(uuidRegex));
    }

    @Test
    public void Entity_shouldNotUpdateTimestamps() throws Exception {
        mEntity1.setCreatedAt(null);
        mEntity1.setUpdatedAt(null);
        mEntity1.doUpdateTimestamps(false);
        mEntity1.touch();

        assertNull("getCreatedAt() not null", mEntity1.getCreatedAt());
        assertNull("getUpdatedAt() not null", mEntity1.getUpdatedAt());
    }

    @Test
    public void Entity_shouldGetExclusionStrategies() throws Exception {
        TestEntityWithExclusion entity = new TestEntityWithExclusion();
        entity.setId(TEST_ID_1);
        entity.setUuid(TEST_UUID_1);
        entity.setImageFile(mEntity1.getImageFile());
        entity.setTest(mEntity1.isTest());
        entity.setCreatedAt(mEntity1.getCreatedAt());
        entity.setUpdatedAt(mEntity1.getUpdatedAt());

        String jsonStr = entity.toJson();
        JsonObject jsonObject = JsonUtils.toJsonObject(jsonStr);
        assertNotNull("jsonObject null", entity);
        assertFalse("id is present in JSON", jsonObject.has("id"));
        assertFalse("image_file is present in JSON", jsonObject.has("image_file"));
    }

    /**
     * NOTE: Entity.getFieldNamingPolicy() will not override any Entity fields annotated
     * with @SerializedName.
     */
    @Test
    public void Entity_shouldGetFieldNamingPolicy() throws Exception {
        String testStr = "This is a test string.";
        TestEntityWithPolicy entity = new TestEntityWithPolicy();
        entity.setTestString(testStr);
        String jsonStr = entity.toJson();
        JsonObject jsonObject = JsonUtils.toJsonObject(jsonStr);

        assertNotNull("jsonObject null", jsonObject);
        assertTrue("test-string not present in JSON", jsonObject.has("test-string"));
        assertEquals("test-string incorrect value", testStr, jsonObject.get("test-string").getAsString());
    }

    /**
     * NOTE: Entity.getFieldNamingPolicy() will not override any Entity fields annotated
     * with @SerializedName.
     */
    @Test
    public void Entity_shouldGetFieldNamingStrategy() throws Exception {
        String testStr = "This is another test string.";
        TestEntityWithStrategy entity = new TestEntityWithStrategy();
        entity.setTestString(testStr);
        String jsonStr = entity.toJson();
        JsonObject jsonObject = JsonUtils.toJsonObject(jsonStr);

        assertNotNull("jsonObject null", jsonObject);
        assertTrue("altered-test-string not present in JSON", jsonObject.has("altered-test-string"));
        assertEquals("altered-test-string incorrect value", testStr, jsonObject.get("altered-test-string").getAsString());
    }

    @Test
    public void Entity_shouldSetSerializedNulls() throws Exception {
        mEntity1.setSerializeNulls(true);
        mEntity1.setImageFile(null);
        String jsonStr = mEntity1.toJson();
        JsonObject jsonObject = JsonUtils.toJsonObject(jsonStr);
        assertNotNull("jsonObject(1) null value", jsonObject);
        assertTrue("image_file is not present in JSON", jsonObject.has("image_file"));
        assertTrue("image_file not null value", jsonObject.get("image_file").isJsonNull());

        mEntity2.setSerializeNulls(false);
        mEntity2.setImageFile(null);
        jsonStr = mEntity2.toJson();
        jsonObject = JsonUtils.toJsonObject(jsonStr);
        assertNotNull("jsonObject(2) null value", jsonObject);
        assertFalse("image_file is present in JSON", jsonObject.has("image_file"));
    }

    @After
    public void tearDown() throws Exception {
        mEntity1 = null;
        mEntity2 = null;
    }
}
