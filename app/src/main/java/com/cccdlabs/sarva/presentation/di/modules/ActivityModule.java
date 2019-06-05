package com.cccdlabs.sarva.presentation.di.modules;

import android.app.Activity;

import com.cccdlabs.sarva.presentation.di.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides @PerActivity Activity provideActivity() {
        return activity;
    }
}
