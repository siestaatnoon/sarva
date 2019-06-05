package com.cccdlabs.sarva.presentation.ui.activities;

import android.app.Activity;
import android.content.Intent;

import com.cccdlabs.sarva.R;

import org.junit.Rule;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.allOf;


public class MainActivitySteps {

    @Rule
    ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    private Activity activity;

    @Before
    public void setUp() throws Exception {
        activityTestRule.launchActivity(new Intent());
        Intents.init();
        activity = activityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        getInstrumentation().waitForIdleSync();
        activityTestRule.finishActivity();
        Intents.release();
        activity = null;
    }

    @Given("^I start the application$")
    public void iStartTheApplication() throws Exception {
        // SettingsActivity is launched as well (?) above MainActivity
        // from SettingsActivitySteps so have to click back to return
        // to MainActivity
        Espresso.pressBack();

        assertNotNull("activity is null", activity);
    }

    @And("^I see the Gizmos list is not empty$")
    public void iSeeTheGizmosListIsNotEmpty() throws Exception {
        onView(withId(R.id.gizmo_list)).check(matches(isDisplayed()));
    }

    @And("^I click on the first Gizmo$")
    public void iClickOnTheFirstGizmo() throws Exception {
        onView(withId(R.id.gizmo_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Then("^I should see a list of Widgets$")
    public void iShouldSeeAListOfWidgets() throws Exception {
        onView(allOf(withId(R.id.widget_items), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).check(matches(isDisplayed()));
    }

    @Given("^I am on the main screen$")
    public void iamOnTheMainScreen() throws Exception {
        // SettingsActivity is launched as well (?) above MainActivity
        // from SettingsActivitySteps so have to click back to return
        // to MainActivity
        Espresso.pressBack();

        assertNotNull(activity);
    }

    @And("^I click on the menu button$")
    public void iClickOnTheMenuButton() throws Exception {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    }

    @And("^I select the About menu item$")
    public void iSelectTheAboutMenuItem() throws Exception {
        onView(withText(R.string.menu_action_about)).perform(click());
    }

    @Then("^I should see the About screen$")
    public void iShouldSeeTheAboutScreen() throws Exception {
        intended(hasComponent(AboutActivity.class.getName()));
    }

    @And("^I select the Settings menu item$")
    public void iSelectTheSettingsMenuItem() throws Exception {
        onView(withText(R.string.menu_action_settings)).perform(click());
    }

    @Then("^I should see the Settings screen$")
    public void iShouldSeeTheSettingsScreen() throws Exception {
        // SettingsActivity is already launched in SettingsActivitySteps
        // from an intent so need to check if second SettingsActivity intent
        // is launched
        intended(hasComponent(SettingsActivity.class.getName()), times(2));
    }

    @And("^I select the Close menu item$")
    public void iSelectTheCloseMenuItem() throws Exception {
        onView(withText(R.string.menu_action_close)).perform(click());
    }

    @Then("^I should see the application close$")
    public void iShouldSeeTheApplicationClose() throws Exception {
        assertTrue(activity.isFinishing());
    }
}
