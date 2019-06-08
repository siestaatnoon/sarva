package com.cccdlabs.sarva.presentation.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cccdlabs.sarva.R;
import com.cccdlabs.sarva.presentation.di.HasComponent;
import com.cccdlabs.sarva.presentation.di.components.AppComponent;
import com.cccdlabs.sarva.presentation.di.components.DaggerMainComponent;
import com.cccdlabs.sarva.presentation.di.components.MainComponent;
import com.cccdlabs.sarva.presentation.presenters.MainPresenter;
import com.cccdlabs.sarva.presentation.ui.activities.base.BaseAppCompatActivity;
import com.cccdlabs.sarva.presentation.views.MainView;

import javax.inject.Inject;

import timber.log.Timber;

public class MainActivity extends BaseAppCompatActivity implements MainView, HasComponent<MainComponent> {

    @Inject MainPresenter mPresenter;

    private MainComponent mMainComponent;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppComponent appComponent = getAppComponent();
        appComponent.inject(appComponent.partnerMapper());
        appComponent.inject(appComponent.partnerRepository());

        mMainComponent = DaggerMainComponent.builder()
                .appComponent(appComponent)
                .activityModule(getActivityModule())
                .build();

        mMainComponent.inject(this);
        mMainComponent.inject(mPresenter);
        mMainComponent.inject(mMainComponent.partnerBroadcastUseCase());
        mContext = appComponent.context();
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
        Timber.e(message);
        showMessage(message);
    }

    @Override
    public MainComponent getComponent() {
        return mMainComponent;
    }

    protected void showMessage(String message) {
        if ( ! TextUtils.isEmpty(message)) {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }
}
