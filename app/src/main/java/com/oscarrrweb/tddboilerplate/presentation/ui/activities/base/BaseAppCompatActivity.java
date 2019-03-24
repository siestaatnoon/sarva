package com.oscarrrweb.tddboilerplate.presentation.ui.activities.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.oscarrrweb.tddboilerplate.App;
import com.oscarrrweb.tddboilerplate.presentation.di.components.ApplicationComponent;
import com.oscarrrweb.tddboilerplate.presentation.di.modules.ActivityModule;

abstract public class BaseAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getApplicationComponent().inject(this);
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((App) getApplication()).getApplicationComponent();
    }

    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }
}
