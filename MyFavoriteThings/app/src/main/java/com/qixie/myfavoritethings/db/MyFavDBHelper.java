package com.qixie.myfavoritethings.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.qixie.myfavoritethings.bean.FavThingItem;

import java.util.ArrayList;
import java.util.List;

/***
 * Database helper class
 * Database operations: create table, select from table,insert new record into table...
 */
public class MyFavDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME ="myfav.db";
    private static final int DB_VERSION = 1;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS MYFAVTHINGS (" +
            "TITLE TEXT NOT NULL," +
            "TIMESTAMP DATETIME NOT NULL," +
            "IMAGE BLOB NOT NULL," +
            "DESCRIPTION TEXT NOT NULL)";


    public MyFavDBHelper(@Nullable Context context) {
        super(context,DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS MYFAVTHINGS");
        onCreate(db);
    }


    /**
     * get data from database:
     * @return A list contains all information of the favorite things stored in the database
     */

    public List<FavThingItem> getData(){
        List<FavThingItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        if(db!=null){
            Cursor cursor = db.rawQuery("select rowid,* from MYFAVTHINGS",null);
            while (cursor!=null && cursor.moveToNext()){
                FavThingItem item = new FavThingItem();
                item.setRowid(cursor.getInt(0));
                item.setTitle(cursor.getString(cursor.getColumnIndex("TITLE")));;
                item.setTimeStamp(cursor.getString(cursor.getColumnIndex("TIMESTAMP")));
                item.setImage(cursor.getBlob(cursor.getColumnIndex("IMAGE")));;
                item.setDescription(cursor.getString(cursor.getColumnIndex("DESCRIPTION")));;
                items.add(item);
            }
            if(cursor!=null){
                cursor.close();
            }
            db.close();
        }

        return items;
    }

    /**
     * Get certain record with its rowid
     * @param rowid rowid of the record
     * @return A FavThingItem contains the selection result
     */
    public FavThingItem getSingleItem(int rowid){
        SQLiteDatabase db = getReadableDatabase();
        FavThingItem item = new FavThingItem();
        if(db!=null){
            //Cursor cursor = db.rawQuery("select * from MYFAVTHINGS where title= '"+title+"'"+ "and timestamp ='"+timestamp+"'",null);

            Cursor cursor = db.rawQuery("select * from MYFAVTHINGS where rowid="+rowid,null);
            cursor.moveToNext();
            item.setRowid(rowid);
            item.setTitle(cursor.getString(cursor.getColumnIndex("TITLE")));;
            item.setTimeStamp(cursor.getString(cursor.getColumnIndex("TIMESTAMP")));
            item.setImage(cursor.getBlob(cursor.getColumnIndex("IMAGE")));;
            item.setDescription(cursor.getString(cursor.getColumnIndex("DESCRIPTION")));;

            if(cursor!=null){
                cursor.close();
            }
            db.close();
        }

        return item;




    }

    /**
     * Insert single item into the database
     * @param item the item need to insert
     */
    public void addData(FavThingItem item){
        SQLiteDatabase db = null;
        db = getWritableDatabase();
        if(db!=null){
            db.execSQL("INSERT INTO MYFAVTHINGS(TITLE,TIMESTAMP,IMAGE,DESCRIPTION) VALUES(?,?,?,?)",new Object[]{item.getTitle(),item.getTimeStamp(), item.getImage(),item.getDescription()});
            db.close();
        }
    }






}
