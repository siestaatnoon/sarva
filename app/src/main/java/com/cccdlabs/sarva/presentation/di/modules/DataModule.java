package com.cccdlabs.sarva.presentation.di.modules;

import android.content.Context;

import com.cccdlabs.sarva.data.network.retrofit.RestClient;
import com.cccdlabs.sarva.data.storage.dao.partners.PartnerDao;
import com.cccdlabs.sarva.data.storage.database.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    private AppDatabase database;
    private String apiBaseUrl;
    private boolean refreshInstance;

    public DataModule(Context context) {
        this(context, null, false, false);
    }

    public DataModule(Context context, boolean isTest) {
        this(context, null, isTest, false);
    }

    public DataModule(Context context, boolean isTest, boolean refreshInstance) {
        this(context, null, false, false);
    }

    public DataModule(Context context, String apiBaseUrl) {
        this(context, apiBaseUrl, false, false);
    }

    public DataModule(Context context, String apiBaseUrl, boolean isTest) {
        this(context, apiBaseUrl, isTest, false);
    }

    public DataModule(Context context, String apiBaseUrl, boolean isTest, boolean refreshInstance) {
        this.apiBaseUrl = apiBaseUrl;
        this.refreshInstance = refreshInstance;
        database = AppDatabase.getInstance(context, isTest, refreshInstance);
    }

    @Provides @Singleton AppDatabase provideAppDatabase() {
        return database;
    }

    @Provides @Singleton RestClient provideRestClient() {
        return RestClient.getInstance(apiBaseUrl, refreshInstance);
    }

    @Provides @Singleton PartnerDao providePartnerDao() {
        return database.partnerDao();
    }
}
