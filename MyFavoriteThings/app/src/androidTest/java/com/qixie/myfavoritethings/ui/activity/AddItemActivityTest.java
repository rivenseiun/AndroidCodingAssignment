package com.qixie.myfavoritethings.ui.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.qixie.myfavoritethings.R;
import com.qixie.myfavoritethings.db.MyFavDBHelper;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;


/**
 * Test class of AddItemActivity
 */
@RunWith(AndroidJUnit4.class)
public class AddItemActivityTest extends TestCase {
    private String timestamp;
    @Rule
    public IntentsTestRule<AddItemActivity> mActivityRule = new IntentsTestRule<>(
           AddItemActivity.class);

    /**
     * Test the take photo button on the popupwindow:
     * Click to take photo and show the result in the imageView
     */
    @Test
    public void testTakePhoto(){
        onView(withId(R.id.select_image_btn)).perform(click());

        Uri uri = Uri.parse("android.resource://com.qixie.myfavoritethings/drawable/default_pic");
        mActivityRule.getActivity().setImageUri(uri);
        Log.e("uri",uri.toString());

        // Create a result intent from the camera
        Intent resIntent = new Intent();
        resIntent.putExtra("data", uri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resIntent);


        //Get the package name of camera
        PackageManager packageManager = mActivityRule.getActivity().getPackageManager();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String packageName = intent.resolveActivity(packageManager).getPackageName();

        //Send a intent to the camera application
        intending(toPackage(packageName)).respondWith(result);
        //intending(toPackage("com.huawei.camera")).respondWith(result);


        //Click the take photo button on the popupwindow
        onView(withId(R.id.take_photo_btn)).perform(click());

        // We can also validate that an intent resolving to the "camera" activity has been sent out by our app
        intended(toPackage(packageName));

    }


    /**
     * Test the select gallery button and save button:
     * click to select a default image, and type default title and description, then click to save
     */
    @Test
    public void testSelectGalleryAndSave(){
        onView(withId(R.id.select_image_btn)).perform(click());

        Uri uri = Uri.parse("android.resource://com.qixie.myfavoritethings/drawable/default_pic");
        mActivityRule.getActivity().setImageUri(uri);
        // Build a result intent to return from the gallery
        Intent resIntent = new Intent();
        resIntent.putExtra("data", uri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resIntent);

        //Get the package name of gallery
        PackageManager packageManager = mActivityRule.getActivity().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        String packageName = intent.resolveActivity(packageManager).getPackageName();

        // Send a intent to gallery and get the respond intent(resIntent) from gallery
        intending(toPackage(packageName)).respondWith(result);

        // Click on the corresponding button on the popupwindow
        onView(withId(R.id.select_gallery_btn)).perform(click());

        //Validate the intent we sent to the gallery application
        intended(toPackage(packageName));


        //Test the save button
        //set data
        onView(withId(R.id.new_fav_title)).perform(typeText("test_title1"),closeSoftKeyboard());
        onView(withId(R.id.new_fav_des)).perform(typeText("test_description1"),closeSoftKeyboard());
        onView(withId(R.id.save_btn)).perform(click());
        timestamp = mActivityRule.getActivity().getTimestamp();
        intended(allOf(hasComponent(MainActivity.class.getName())));

    }

    @After
    /**
     * Clear the item inserted to the database
     */

    public void clearItem(){
        MyFavDBHelper helper = new MyFavDBHelper(mActivityRule.getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("MYFAVTHINGS","TIMESTAMP=?",new String[]{timestamp});



    }







}