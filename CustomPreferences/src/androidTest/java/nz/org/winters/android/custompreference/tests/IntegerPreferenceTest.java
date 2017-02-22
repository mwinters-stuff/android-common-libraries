package nz.org.winters.android.custompreference.tests;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nz.org.winters.android.custompreference.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.withKey;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by MWINTERS on 8/05/2015.
 */
@RunWith(AndroidJUnit4.class)
public class IntegerPreferenceTest {
    private static final String PREF_KEY = "testpref";
    @Rule
    public ActivityTestRule<SettingsActivity> testCase = new ActivityTestRule<>(SettingsActivity.class);

    public ActivityTestRule<SettingsActivity> getTestCase() {
        return testCase;
    }

    @Test
    public void testPref(){
        Context context = testCase.getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        onData(allOf(is(instanceOf(Preference.class)), withKey(PREF_KEY))).perform(click());
        onView(allOf(withId(R.id.label), withText(R.string.count))).check(matches(is(isDisplayed())));
        onView(allOf(withParent(withId(R.id.first)), withId(R.id.key_middle))).perform(click());
        onView(allOf(withParent(withId(R.id.third)), withId(R.id.key_right))).perform(click());
        onView(withId(R.id.set_button)).perform(click());
        assertThat(preferences.getInt(PREF_KEY, 0), is(29));

    }

}
