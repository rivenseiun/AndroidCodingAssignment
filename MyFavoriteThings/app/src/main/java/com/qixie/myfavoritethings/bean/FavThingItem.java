package com.qixie.myfavoritethings.bean;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * The item read from database or the item need to be stored into database
 */

public class FavThingItem implements Serializable {
    private int rowid;
    private String title;
    private String timestamp;
    private byte[] image;
    private String description;


    public int getRowid() {
        return rowid;
    }

    public void setRowid(int rowid) {
        this.rowid = rowid;
    }

    public byte[] getImage() {
        return image;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {

        return title;
    }


    public void setTimeStamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Set timestamp with current timestamp
     */
    public void setTimeStamp(){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(ts);
        this.timestamp = timestamp;


    }


    /**
     * Set image with bitmap
     * @param photo
     */
    public void setImage(Bitmap photo) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] imageByteArray = stream.toByteArray();
        this.image = imageByteArray;
    }

    /**
     * Set image with byte array
     * @param imageByteArray
     */
    public void setImage(byte[] imageByteArray){
        this.image = imageByteArray;



    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
