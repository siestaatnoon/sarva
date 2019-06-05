package com.cccdlabs.sarva.data.entity;

import com.google.gson.annotations.SerializedName;
import com.cccdlabs.sarva.data.entity.base.Entity;

public class TestEntity extends Entity {

    @SerializedName("image_file")
    private byte[] imageFile;

    @SerializedName("is_test")
    private boolean isTest;

    @Override
    public String getTableName() {
        return "test";
    }

    public byte[] getImageFile() {
        return imageFile;
    }

    public void setImageFile(byte[] imageFile) {
        this.imageFile = imageFile;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }
}
