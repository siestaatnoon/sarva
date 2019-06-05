package com.cccdlabs.sarva.presentation.di.modules;

import android.app.Activity;
import android.content.Context;

import com.cccdlabs.sarva.data.repository.sample.GizmoRepository;
import com.cccdlabs.sarva.domain.interactors.sample.SampleDisplayUseCase;
import com.cccdlabs.sarva.presentation.di.PerActivity;
import com.cccdlabs.sarva.presentation.presenters.MainPresenter;
import com.cccdlabs.sarva.presentation.ui.adapters.SampleAdapter;
import com.cccdlabs.sarva.presentation.views.MainView;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    public MainModule() {}

    @Provides @PerActivity SampleDisplayUseCase provideSampleDisplayUseCase() {
        return new SampleDisplayUseCase();
    }

    @Provides @PerActivity MainPresenter provideMainPresenter(GizmoRepository repository, Activity activity) {
        return new MainPresenter(repository, (MainView) activity);
    }

    @Provides @PerActivity SampleAdapter provideSampleAdapter(Activity activity, Context context) {
        return new SampleAdapter((MainView) activity, context);
    }
}
