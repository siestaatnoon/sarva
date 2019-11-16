package com.cccdlabs.sarva.presentation.ui.activities.partners;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cccdlabs.sarva.R;
import com.cccdlabs.sarva.presentation.di.HasComponent;
import com.cccdlabs.sarva.presentation.di.components.AppComponent;
import com.cccdlabs.sarva.presentation.di.components.DaggerPartnerComponent;
import com.cccdlabs.sarva.presentation.di.components.PartnerComponent;
import com.cccdlabs.sarva.presentation.di.modules.P2pModule;
import com.cccdlabs.sarva.presentation.di.modules.PartnerModule;
import com.cccdlabs.sarva.presentation.exception.ErrorMessageFactory;
import com.cccdlabs.sarva.presentation.model.partners.PartnerUiModel;
import com.cccdlabs.sarva.presentation.presenters.partners.PartnerCheckPresenter;
import com.cccdlabs.sarva.presentation.ui.activities.AboutActivity;
import com.cccdlabs.sarva.presentation.ui.activities.base.BaseAppCompatActivity;
import com.cccdlabs.sarva.presentation.ui.adapters.PartnerCheckAdapter;
import com.cccdlabs.sarva.presentation.views.partners.PartnerCheckView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import javax.inject.Inject;

public class PartnerCheckActivity extends BaseAppCompatActivity implements PartnerCheckView, HasComponent<PartnerComponent> {

    private static final int BOTTOM_NAVIGATION_INDEX = 1;

    @Inject PartnerCheckPresenter mPresenter;
    private PartnerCheckAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private PartnerComponent mPartnerComponent;

    private class BottomNavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            final Intent intent;

            switch (id) {
                case R.id.action_partner_add:
                    /*
                    intent = new Intent(PartnerCheckActivity.this, AboutActivity.class);
                    startActivity(intent);
                    */
                    return false;

                case R.id.action_partner_check:

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
        setContentView(R.layout.activity_partner_check);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppComponent appComponent = getAppComponent();
        mPartnerComponent = DaggerPartnerComponent.builder()
                .appComponent(appComponent)
                .activityModule(getActivityModule())
                .p2pModule(new P2pModule(this, appComponent.partnerRepository()))
                .partnerModule(new PartnerModule(this))
                .build();
        mPartnerComponent.inject(this);

        // setup recycler view
        mAdapter = new PartnerCheckAdapter(this, this);
        mRecyclerView = findViewById(R.id.partner_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        // setup bottom navigation
        BottomNavigationListener bottomNavigationListener = new BottomNavigationListener();
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavigationListener);
        Menu menu = bottomNavigation.getMenu();
        for (int i=0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setChecked(false).setEnabled(true);
        }
        menu.getItem(BOTTOM_NAVIGATION_INDEX).setEnabled(false);  // disable the item that corresponds to this activity
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        mAdapter = null;
        mRecyclerView = null;
        mPartnerComponent = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    public void showPartners(List<PartnerUiModel> partners) {
        mAdapter.addItems(partners);
    }

    @Override
    public void onClickPartnerAdd(PartnerUiModel uiModel) {
        mPresenter.addPartner(uiModel);
    }

    @Override
    public void onClickPartnerDelete(PartnerUiModel uiModel) {
        mPresenter.deletePartner(uiModel);
    }

    @Override
    public void onPartnerAdded(PartnerUiModel uiModel) {
        String message = String.format(getString(R.string.message_added), uiModel.getUsername());
        showSnackbar(findViewById(R.id.coordinator_layout), message);
        mPresenter.retrieveAllPartners();
    }

    @Override
    public void onPartnerDeleted(PartnerUiModel uiModel, boolean hasDeleted) {
        String message = String.format(getString(R.string.message_deleted), uiModel.getUsername());
        showSnackbar(findViewById(R.id.coordinator_layout), message);
        mPresenter.retrieveAllPartners();
    }

    @Override
    public void onPartnerSetActive(PartnerUiModel uiModel) {}

    @Override
    public void onEmissionStarted() {}

    @Override
    public void onEmissionStopped() {}

    @Override
    public Context context() {
        return this;
    }

    @Override
    public PartnerComponent getComponent() {
        return mPartnerComponent;
    }

    @Override
    public void showError(Throwable throwable) {
        String message = ErrorMessageFactory.create(this, throwable);
        showSnackbar(findViewById(R.id.coordinator_layout), message);
    }

    @Override
    public void showLoading() {
        ProgressBar progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        ProgressBar progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showRetry() {}

    @Override
    public void hideRetry() {}
}
