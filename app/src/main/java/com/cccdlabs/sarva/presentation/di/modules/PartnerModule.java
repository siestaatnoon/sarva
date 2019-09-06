package com.cccdlabs.sarva.presentation.di.modules;

import com.cccdlabs.sarva.presentation.di.PerActivity;
import com.cccdlabs.sarva.presentation.views.base.PartnerEmitterView;
import com.cccdlabs.sarva.presentation.views.partners.PartnerCheckView;

import dagger.Module;
import dagger.Provides;

@Module
public class PartnerModule {

    private PartnerEmitterView view;

    public PartnerModule(PartnerEmitterView view) {
        this.view = view;
    }

    @Provides @PerActivity PartnerCheckView providePartnerCheckView() {
        if (!(view instanceof PartnerCheckView)) {
            String message = "PartnerEmitterView passed into PartnerModule constructor must be of ";
            message += "type PartnerCheckView";
            throw new ClassCastException(message);
        }
        return (PartnerCheckView) view;
    }
}
