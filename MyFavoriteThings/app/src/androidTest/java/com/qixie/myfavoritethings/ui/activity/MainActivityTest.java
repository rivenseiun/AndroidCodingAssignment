package com.qixie.myfavoritethings.ui.activity;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.qixie.myfavoritethings.R;
import com.qixie.myfavoritethings.bean.FavThingItem;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

;

/**
 * Test class of MainActivity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    private static final String TAG = "ExampleInstrumentedTest";
    private FavThingItem testItem ;
    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(
            MainActivity.class);

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }


    /**
     * Test the click action on "add new favorite" button: it will start the AddItemActivity
     */
    @Test
    public void testNewButton() {
        onView(withId(R.id.new_btn)).perform(click());
        intended(hasComponent(AddItemActivity.class.getName()));
    }

    /**
     * Test the click action to the item of RecyclerView:It will start the ItemDetailActivity
     */
    @Test
    public void testRecyclerView(){

        onView(withId(R.id.fav_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        intended(allOf(hasComponent(ItemDetailActivity.class.getName())));

    }


}