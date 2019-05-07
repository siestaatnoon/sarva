package com.oscarrrweb.sarva.presentation.di.components;

import android.app.Activity;

import com.oscarrrweb.sarva.presentation.di.PerActivity;
import com.oscarrrweb.sarva.presentation.di.modules.ActivityModule;

import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    Activity activity();
}
