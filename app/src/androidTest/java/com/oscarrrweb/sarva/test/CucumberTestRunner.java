package com.oscarrrweb.sarva.test;

import com.squareup.rx2.idler.Rx2Idler;

import cucumber.api.CucumberOptions;
import cucumber.api.android.CucumberAndroidJUnitRunner;
import io.reactivex.plugins.RxJavaPlugins;

@CucumberOptions(
        features = "features",
        glue = "com.oscarrrweb.tddboilerplate",
        tags = {"@e2e"}
)
public class CucumberTestRunner extends CucumberAndroidJUnitRunner {

    @Override
    public void onStart() {
        RxJavaPlugins.setInitIoSchedulerHandler(Rx2Idler.create("IOThread"));
        RxJavaPlugins.setInitComputationSchedulerHandler(Rx2Idler.create("ProcessThread"));
        super.onStart();
    }
}
