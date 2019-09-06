package com.cccdlabs.sarva.presentation.ui.activities.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cccdlabs.sarva.App;
import com.cccdlabs.sarva.presentation.di.components.AppComponent;
import com.cccdlabs.sarva.presentation.di.modules.ActivityModule;

/**
 * Activity abstraction that extends {@link AppCompatActivity} and performs the dependency
 * injection for it. Also methods to retrieve the App DI component (for {@link android.content.Context}
 * and DI module.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
abstract public class BaseAppCompatActivity extends AppCompatActivity {

    public BaseAppCompatActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getAppComponent().inject(this);
    }

    protected AppComponent getAppComponent() {
        return ((App) getApplication()).getAppComponent();
    }

    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    protected void showMessage(String message) {
        if ( ! TextUtils.isEmpty(message)) {
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
