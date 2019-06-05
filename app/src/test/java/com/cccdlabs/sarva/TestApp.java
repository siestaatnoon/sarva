package com.cccdlabs.sarva;

import com.cccdlabs.sarva.presentation.di.components.DaggerTestAppComponent;
import com.cccdlabs.sarva.presentation.di.components.TestAppComponent;
import com.cccdlabs.sarva.presentation.di.modules.TestAppModule;
import com.cccdlabs.sarva.presentation.di.modules.TestDataModule;

import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

public class TestApp extends App implements TestLifecycleApplication {

    private TestAppComponent appComponent;

    @Override
    public void onCreate() {
        //super.onCreate();
        appComponent = DaggerTestAppComponent.builder()
                .testAppModule(new TestAppModule(this))
                .testDataModule(new TestDataModule(this))
                .build();
    }

    public TestAppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void beforeTest(Method method) {}

    @Override
    public void prepareTest(Object test) {}

    @Override
    public void afterTest(Method method) {}
}
