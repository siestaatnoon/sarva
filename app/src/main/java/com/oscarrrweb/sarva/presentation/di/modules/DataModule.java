package com.oscarrrweb.sarva.presentation.di.modules;

import android.content.Context;

import com.oscarrrweb.sarva.data.mappers.sample.DoodadMapper;
import com.oscarrrweb.sarva.data.mappers.sample.GizmoMapper;
import com.oscarrrweb.sarva.data.mappers.sample.WidgetMapper;
import com.oscarrrweb.sarva.data.network.retrofit.RestClient;
import com.oscarrrweb.sarva.data.repository.sample.DoodadRepository;
import com.oscarrrweb.sarva.data.repository.sample.GizmoRepository;
import com.oscarrrweb.sarva.data.repository.sample.WidgetRepository;
import com.oscarrrweb.sarva.data.storage.dao.sample.DoodadDao;
import com.oscarrrweb.sarva.data.storage.dao.sample.GizmoDao;
import com.oscarrrweb.sarva.data.storage.dao.sample.WidgetDao;
import com.oscarrrweb.sarva.data.storage.database.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    AppDatabase database;
    String apiBaseUrl;
    boolean refreshInstance;

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

    /* SAMPLE USAGE BELOW */

    @Provides @Singleton GizmoRepository provideGizmoRepository() {
        return new GizmoRepository();
    }

    @Provides @Singleton GizmoMapper provideGizmoMapper() {
        return new GizmoMapper();
    }

    @Provides @Singleton GizmoDao provideGizmoDao() {
        return database.gizmoDao();
    }

    @Provides @Singleton WidgetRepository provideWidgetRepository() {
        return new WidgetRepository();
    }

    @Provides @Singleton WidgetMapper provideWidgetMapper() {
        return new WidgetMapper();
    }

    @Provides @Singleton WidgetDao provideWidgetDao() {
        return database.widgetDao();
    }

    @Provides @Singleton DoodadRepository provideDoodadRepository() {
        return new DoodadRepository();
    }

    @Provides @Singleton DoodadMapper provideDoodadMapper() {
        return new DoodadMapper();
    }

    @Provides @Singleton DoodadDao provideDoodadDao() {
        return database.doodadDao();
    }
}
