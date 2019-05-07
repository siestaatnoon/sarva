package com.oscarrrweb.sarva.presentation.ui.activities;

import android.app.Activity;
import android.content.Intent;

import com.oscarrrweb.sarva.R;

import org.junit.Rule;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoActivityResumedException;
import androidx.test.rule.ActivityTestRule;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class AboutActivitySteps {

    @Rule
    ActivityTestRule<AboutActivity> activityTestRule = new ActivityTestRule<>(AboutActivity.class);

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

    @Given("^I am in the the About screen$")
    public void iAmInTheAboutScreen() throws Exception {
        assertNotNull(activity);
    }

    @And("^I click the back arrow$")
    public void iClickTheBackArrow() throws Exception {
        // MainActivity and SettingsActivity are launched as well (?) above
        // AboutActivity from MainActivitySteps and SettingsActivitySteps
        // so have to click back twice to return to AboutActivity
        Espresso.pressBack();
        Espresso.pressBack();

        try {
            onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        } catch (NoActivityResumedException e) {
            fail(e.getMessage());
        }
    }

    @Then("^I should be back in the main screen$")
    public void iShouldBeBackInTheMainScreen() throws Exception {
        assertTrue(activity.isFinishing());
    }

    @And("^I click the Email button$")
    public void iClickOnTheEmailButton() throws Exception {
        // MainActivity and SettingsActivity are launched as well (?) above
        // AboutActivity from MainActivitySteps and SettingsActivitySteps
        // so have to click back twice to return to AboutActivity
        Espresso.pressBack();
        Espresso.pressBack();

        onView(withId(R.id.fab)).perform(click());
    }

    @Then("^The email client should open$")
    public void theEmailClientShouldOpen() throws Exception {
        // The floating action button in AboutActivity launches an
        // intent with action Intent.ACTION_SENDTO but gets bundled
        // into an intent with action Intent.ACTION_CHOOSER
        intended(hasAction(Intent.ACTION_CHOOSER));
    }
}
