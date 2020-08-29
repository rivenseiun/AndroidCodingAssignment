package com.qixie.myfavoritethings.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.qixie.myfavoritethings.R;
import com.qixie.myfavoritethings.bean.FavThingItem;
import com.qixie.myfavoritethings.db.MyFavDBHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Activity used to add new favorite item
 * @author Qi Xie
 */
public class AddItemActivity extends AppCompatActivity {
    private EditText newTitle;
    private Button saveBtn;
    private Button selectBtn;
    private ImageView newImageView;
    private EditText newDes;
    private PopupWindow popWin = null;


    private Bitmap newImage = null;
    private FavThingItem newItem;
    private Uri imageUri = null;
    private String imagePath =null;
    private String timestamp;
    private int flag =1;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECT =2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        newTitle = (EditText) findViewById(R.id.new_fav_title);
        saveBtn = (Button) findViewById(R.id.save_btn);
        selectBtn = (Button) findViewById(R.id.select_image_btn);
        newImageView = (ImageView)findViewById(R.id.new_fav_image);
        newDes = (EditText) findViewById(R.id.new_fav_des);

        //Select image button onClickListener: show the popup window
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Hide the keyboard when click select image button: clear focus on both edittext and hide keyboard
                newTitle.clearFocus();
                newDes.clearFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(newTitle.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(newDes.getWindowToken(),0);


                initPopwin(view);

            }
        });

        //Save button onClickListener:get all the input, store them in a FavThingItem object, then insert it into the database
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newItem = new FavThingItem();
                String title = newTitle.getText().toString();
                String description = newDes.getText().toString();


                /**
                 * Check if all the inputs are valid:
                 * Title and description could not be empty string or string of blank spaces
                 * Image could not be null
                 * Only if the inputs are valid the MainActivity will be launched
                 */
                if(title.replaceAll("\\s+","").length()>0&&description.replaceAll("\\s+","").length()>0&&newImage!=null){
                    newItem.setTitle(title);
                    newItem.setImage(newImage);
                    newItem.setTimeStamp();
                    newItem.setDescription(description);




                    MyFavDBHelper dbHelper = new MyFavDBHelper(AddItemActivity.this);
                    dbHelper.addData(newItem);
                    timestamp = newItem.getTimeStamp();
                    Intent intent = new Intent(AddItemActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }


                /**
                 * If input is invalid, show the toast(s)
                 */
                else if(newImage==null) Toast.makeText(AddItemActivity.this,"Please add pictures of your favorite!",Toast.LENGTH_SHORT).show();
                else if(title.replaceAll("\\s+","").length()==0) Toast.makeText(AddItemActivity.this,"Please enter title!",Toast.LENGTH_SHORT).show();
                else if(description.replaceAll("\\s+","").length()==0) Toast.makeText(AddItemActivity.this,"Please enter description!",Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(AddItemActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            /**
             * case 1: Take photo from camera
             */
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode ==RESULT_OK){
                    Bitmap bitmap = null;
                    try {

                       bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        //rotate the image if it is rotated by the camera
                       bitmap = rotateImage(bitmap,getCameraRotateAngle());



                    } catch (FileNotFoundException e) {
                        //e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    newImage = bitmap;
                    newImageView.setVisibility(View.VISIBLE);
                    newImageView.setImageBitmap(bitmap);

                }

                break;
            /**
             * case 2: Select image from gallery
             */
            case REQUEST_IMAGE_SELECT:
                if(resultCode ==RESULT_OK){
                    if(imageUri ==null||flag !=1)imageUri = data.getData();
                    flag++;
                    Bitmap bitmap= null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        //rotate the image if it is rotated in the gallery
                        bitmap = rotateImage(bitmap,getGalleryRotateAngle());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    newImage = bitmap;
                    newImageView.setVisibility(View.VISIBLE);
                    newImageView.setImageBitmap(bitmap);




                }
                break;
        }
    }

    /**
     * Open camera and take a photo
     * @throws IOException
     *
     */
    private void takePhoto() throws IOException {
        Intent tpIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        /**
         * Check if there is a camera application in device
         * Only if camera application exists, the new imagefile will create and the camera will start
         */
        if(tpIntent.resolveActivity(getPackageManager())!=null){
            File imageFile = createImageFile();


            //For SDK version over Android 7.0:using a file provider
            if(imageFile !=null){
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
                    if(imageUri ==null||flag !=1)imageUri = FileProvider.getUriForFile(this,"com.qixie.myfavoritethings.fileprovider",imageFile);
                }else {
                    //For SDK version before Android 7.0:
                    if(imageUri ==null||flag !=1)imageUri = Uri.fromFile(imageFile);
                }
                flag++;
                tpIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                tpIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(tpIntent, REQUEST_IMAGE_CAPTURE);


            }
            //In case of not existing a camera application
            else Toast.makeText(this,"No Camera detected, please select image from gallery",Toast.LENGTH_SHORT);


        }




    }


    /**
     * When taking photo: create a file to store the photo.
     * @return created image file
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MYFAV_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imagePath = imageFile.getAbsolutePath();
        return imageFile;





    }

    /**
     *  Select image from gallery
     */
    private void selectFromGallery(){
        Intent sgIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(sgIntent, REQUEST_IMAGE_SELECT);
    }


    /**
     * When the select image button is clicked, show this popupwindow
     * Two buttons included in this popwindow: take photo button and select gallery button
     * @param v
     */
    private void initPopwin(View v){
        View view = LayoutInflater.from(AddItemActivity.this).inflate(R.layout.select_img_popup, null, false);
        Button takePhotoBtn = (Button) view.findViewById(R.id.take_photo_btn);
        Button selectGalleryBtn = (Button) view.findViewById(R.id.select_gallery_btn);
        popWin = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWin.setFocusable(true);
        popWin.setOutsideTouchable(true);
        popWin.setContentView(view);
        popWin.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.pop_bkg_color)));
        popWin.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        popWin.update();


        /**
         * Set the onClickListener of the two buttons
         */
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    takePhoto();
                    popWin.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        selectGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFromGallery();
                popWin.dismiss();


            }
        });


    }


    /**
     * When test on different devices, we find that some devices will return a rotated image instead of the original image
     * We can find the rotate information(orientation) of the image
     * And with the orientation value we can rotate the image back to the original image
     * The methods below are used to solve the rotate problem in different cases: image from camera, or image from gallery
     */


    /**
     * Rotate the image
     * @param bmp the bitmap need to be rotated
     * @param angle rotate angle
     * @return rotated image
     */
    private Bitmap rotateImage(Bitmap bmp,int angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

    }

    /**
     * Case 1: Image taken from camera
     * The orientation values of these images can be find in their exif data
     * @return the angle that the image need to be rotated by
     * @throws IOException
     */
    private int getCameraRotateAngle() throws IOException {
        /**
         * In takePhoto() method, we call the createImageFile() method and set the value of imagePath
         * We can use imagePath directly to get the orientation value
         */
        ExifInterface ei = new ExifInterface(imagePath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
        }

        return 0;
    }

    /** Case 2: Image selected from gallery
     * If the image is selected from gallery, the orientation value in its exif data will be undefined(we will get orientation= ExifInterface.ORIENTATION_UNDEFINED)
     * We can get its orientation value from MediaStore
     * Note that the orientation value we get from exif data and that get from MediaStore are COMPLETELY different, that's why we process them separately
     * @return Rotate angle of the image
     * @throws IOException
     */
    private int getGalleryRotateAngle() throws IOException {

        /**
         * When we select a image from gallery, the result intent will return its uri
         * And we will set imageUri and use this value to execute a query to find its orientation value stored in MediaStore
         */
        Cursor cursor = AddItemActivity.this.getContentResolver().query(imageUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if(cursor !=null){
            if (cursor.getCount() != 1) {
                cursor.close();
                return -1;
            }
            cursor.moveToFirst();
            int orientation = cursor.getInt(0);
            return orientation;


        }
        else return 0;





    }

    /***
     * methods for unit testing:
     */
    public void setNewImage(Bitmap newImage) {
        this.newImage = newImage;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public PopupWindow getPopWin() {
        return popWin;
    }

    public String getTimestamp() {
        return timestamp;
    }
}