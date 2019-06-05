package com.cccdlabs.sarva;

import android.app.Application;

import com.cccdlabs.sarva.presentation.di.components.AppComponent;
import com.cccdlabs.sarva.presentation.di.components.DaggerAppComponent;
import com.cccdlabs.sarva.presentation.di.modules.AppModule;
import com.cccdlabs.sarva.presentation.di.modules.DataModule;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

/**
 * The top level application configuration.
 */
public class App extends Application {

    /**
     * Dagger 2 component for DI.
     */
    private AppComponent appComponent;

    /**
     * Initializes dependency injection, Leak Canary for hunting down leaks in application
     * and Timber for logging.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        } else if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
            Timber.plant(new Timber.DebugTree());
            Timber.d("Timber DebugTree initialized");
        }

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .dataModule(new DataModule(this))
                .build();
    }

    /**
     * Returns the Dagger component for dependency injection in activities or access to the
     * application {@link android.content.Context}.
     *
     * @return The Dagger 2 component for DI
     */
    public AppComponent getAppComponent() {
        return appComponent;
    }
}
