package com.cccdlabs.sarva.presentation.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.cccdlabs.sarva.R;
import com.cccdlabs.sarva.presentation.di.HasComponent;
import com.cccdlabs.sarva.presentation.di.components.AppComponent;
import com.cccdlabs.sarva.presentation.di.components.DaggerMainComponent;
import com.cccdlabs.sarva.presentation.di.components.MainComponent;
import com.cccdlabs.sarva.presentation.di.modules.MainModule;
import com.cccdlabs.sarva.presentation.di.modules.P2pModule;
import com.cccdlabs.sarva.presentation.presenters.MainPresenter;
import com.cccdlabs.sarva.presentation.ui.activities.base.BaseAppCompatActivity;
import com.cccdlabs.sarva.presentation.ui.widgets.BarMeterWidget;
import com.cccdlabs.sarva.presentation.views.MainView;

import javax.inject.Inject;

public class MainActivity extends BaseAppCompatActivity implements MainView, HasComponent<MainComponent> {

    @Inject MainPresenter mPresenter;

    private MainComponent mMainComponent;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppComponent appComponent = getAppComponent();
        mMainComponent = DaggerMainComponent.builder()
                .appComponent(appComponent)
                .activityModule(getActivityModule())
                .p2pModule(new P2pModule(this, appComponent.partnerRepository()))
                .mainModule(new MainModule(this))
                .build();
        mMainComponent.inject(this);
        mContext = appComponent.context();

        BarMeterWidget meter = findViewById(R.id.bar_meter_widget);
        System.out.println("barFillColor: " + meter.getBarFillColor());
        System.out.println("numActiveBars: " + meter.getNumActiveBars());
        meter.setBarFillColor(Color.BLUE);
        meter.setNumActiveBars(6);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // outState.putBoolean(STATE_DIALOG, mDialog != null && mDialog.isShowing());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        mPresenter = null;
        mMainComponent = null;
        mContext = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final Intent intent;

        /*
        switch (id) {
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_close:
                finish();
            default:
                break;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBroadcastUpdate(boolean isBroadcasting) {

    }

    @Override
    public Context context() {
        return mContext;
    }

    @Override
    public MainComponent getComponent() {
        return mMainComponent;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showRetry() {

    }

    @Override
    public void hideRetry() {

    }

    @Override
    public void showError(String message) {
        showMessage(message);
    }
}
