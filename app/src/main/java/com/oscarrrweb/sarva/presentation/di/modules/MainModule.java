package com.oscarrrweb.sarva.presentation.di.modules;

import android.app.Activity;
import android.content.Context;

import com.oscarrrweb.sarva.data.repository.sample.GizmoRepository;
import com.oscarrrweb.sarva.domain.interactors.sample.SampleDisplayUseCase;
import com.oscarrrweb.sarva.presentation.di.PerActivity;
import com.oscarrrweb.sarva.presentation.presenters.MainPresenter;
import com.oscarrrweb.sarva.presentation.ui.adapters.SampleAdapter;
import com.oscarrrweb.sarva.presentation.views.MainView;

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
