package com.qixie.myfavoritethings.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.qixie.myfavoritethings.R;
import com.qixie.myfavoritethings.bean.FavThingItem;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Test class of database helper
 */

@RunWith(AndroidJUnit4.class)
public class MyFavDBHelperTest extends TestCase {
    Context context = ApplicationProvider.getApplicationContext();
    private MyFavDBHelper helper = new MyFavDBHelper(context);
    private FavThingItem testitem = new FavThingItem();

    @Before
    public void createItem(){
        Bitmap image = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.default_pic);
        testitem.setTimeStamp();
        testitem.setTitle("test title");
        testitem.setImage(image);
        testitem.setDescription("test description");
        helper.addData(testitem);
    }


    /**
     * Test the creation of the table：check if the required table exists
     */
    @Test
    public void testCreate(){
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='MYFAVTHINGS' ";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToNext();
        assertEquals(1,cursor.getInt(0));


    }


    /**
     * Test adding data to the database
     */
    @Test
    public void testAddData() {

        SQLiteDatabase db =helper.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select count(*) from MYFAVTHINGS where timestamp = '"+testitem.getTimeStamp()+"'", null);
        cursor.moveToNext();
        assertEquals(cursor.getInt(0),1);

    }

    /**
     * Test getting all records from database
     */
    @Test
    public void testGetData() {
        List<FavThingItem> items = helper.getData();
        SQLiteDatabase db =helper.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select count(*) from MYFAVTHINGS", null);
        cursor.moveToNext();
        assertEquals(cursor.getInt(0),items.size());


    }


    /**
     * Test getting single item with rowid
     */
    @Test
    public void testGetSingleItem() {
        FavThingItem itemGot = helper.getSingleItem(1);
        SQLiteDatabase db =helper.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select rowid,* from MYFAVTHINGS where rowid = 1", null);
        cursor.moveToNext();
        assertEquals(cursor.getInt(0),itemGot.getRowid());

    }

    @After
    public void clearTestItem(){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("MYFAVTHINGS","TIMESTAMP=?",new String[]{testitem.getTimeStamp()});


    }


}