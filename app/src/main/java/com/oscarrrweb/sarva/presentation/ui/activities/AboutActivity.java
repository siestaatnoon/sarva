package com.oscarrrweb.sarva.presentation.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oscarrrweb.sarva.R;
import com.oscarrrweb.sarva.presentation.ui.activities.base.BaseAppCompatActivity;

import androidx.appcompat.app.ActionBar;

public class AboutActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // let the user choose his email client
                Intent emailIntent = new Intent(
                        Intent.ACTION_SENDTO,
                        Uri.fromParts(
                            "mailto",
                                "john@desertelements.com",
                                null
                        )
                );
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
    }

}
