package com.oscarrrweb.sarva.presentation.ui.activities;

import android.app.Activity;
import android.content.Intent;

import com.oscarrrweb.sarva.R;

import org.junit.Rule;

import androidx.test.espresso.NoActivityResumedException;
import androidx.test.rule.ActivityTestRule;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class SettingsActivitySteps {

    @Rule
    ActivityTestRule<SettingsActivity> activityTestRule = new ActivityTestRule<>(SettingsActivity.class);

    private Activity activity;

    @Before
    public void setUp() throws Exception {
        activityTestRule.launchActivity(new Intent());
        activity = activityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        getInstrumentation().waitForIdleSync();
        activityTestRule.finishActivity();
        activity = null;
    }

    @Given("^I am in the the Settings screen$")
    public void iAmInThSettingsScreen() throws Exception {
        assertNotNull(activity);
    }

    @And("^I click the back home arrow$")
    public void iClickTheBackHomeArrow() throws Exception {
        try {
            onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        } catch (NoActivityResumedException e) {
            fail(e.getMessage());
        }
    }

    @Then("^I should return to the main screen$")
    public void iShouldReturnToTheMainScreen() throws Exception {
        assertTrue(activity.isFinishing());
    }
}
