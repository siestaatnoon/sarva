package com.cccdlabs.sarva.presentation.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.cccdlabs.sarva.R;
import com.cccdlabs.sarva.presentation.di.HasComponent;
import com.cccdlabs.sarva.presentation.di.components.AppComponent;
import com.cccdlabs.sarva.presentation.di.components.DaggerMainComponent;
import com.cccdlabs.sarva.presentation.di.components.MainComponent;
import com.cccdlabs.sarva.presentation.di.modules.MainModule;
import com.cccdlabs.sarva.presentation.di.modules.P2pModule;
import com.cccdlabs.sarva.presentation.exception.ErrorMessageFactory;
import com.cccdlabs.sarva.presentation.presenters.MainPresenter;
import com.cccdlabs.sarva.presentation.ui.activities.base.BaseAppCompatActivity;
import com.cccdlabs.sarva.presentation.ui.activities.partners.PartnerCheckActivity;
import com.cccdlabs.sarva.presentation.ui.utils.SnackbarUtils;
import com.cccdlabs.sarva.presentation.ui.widgets.BarMeterWidget;
import com.cccdlabs.sarva.presentation.views.MainView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

public class MainActivity extends BaseAppCompatActivity implements MainView, HasComponent<MainComponent> {

    @Inject MainPresenter mPresenter;

    private MainComponent mMainComponent;

    private class BottomNavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            final Intent intent;

            switch (id) {
                case R.id.action_partner_add:

                    return false;
                case R.id.action_partner_check:
                    mPresenter.stop();
                    intent = new Intent(MainActivity.this, PartnerCheckActivity.class);
                    startActivity(intent);
                    return false;
                case R.id.action_partner_search:

                    return false;
            }

            return false;
        }
    }


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

        // setup bottom navigation, set all unchecked
        BottomNavigationListener bottomNavigationListener = new BottomNavigationListener();
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavigationListener);
        Menu menu = bottomNavigation.getMenu();
        for (int i=0; i < menu.size(); i++) {
            menu.getItem(i).setEnabled(true).setChecked(false);
        }

        BarMeterWidget meter = findViewById(R.id.bar_meter_widget);
        System.out.println("barFillColor: " + meter.getBarFillColor());
        System.out.println("numActiveBars: " + meter.getNumActiveBars());
        meter.setBarFillColor(Color.BLUE);
        meter.setNumActiveBars(6);

        //showSnackbar(findViewById(R.id.coordinator_layout), "Does this work???");

        Snackbar snackbar = SnackbarUtils.action(
                this,
                bottomNavigation,
                "Does this work???",
                "Do it!",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setVisibility(View.GONE);
                    }
                }
        );

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
        mPresenter.pause();
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

        switch (id) {
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                /*
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                 */
                break;
            case R.id.action_close:
                finish();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBroadcastUpdate(boolean isBroadcasting) {

    }

    @Override
    public Context context() {
        return this;
    }

    @Override
    public MainComponent getComponent() {
        return mMainComponent;
    }

    @Override
    public void showError(Throwable throwable) {
        String message = ErrorMessageFactory.create(this, throwable);
        showSnackbar(findViewById(R.id.coordinator_layout), message);
    }

    @Override
    public void showLoading() {}

    @Override
    public void hideLoading() {}

    @Override
    public void showRetry() {}

    @Override
    public void hideRetry() {}
}
