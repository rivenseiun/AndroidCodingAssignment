package com.qixie.myfavoritethings.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.qixie.myfavoritethings.R;
import com.qixie.myfavoritethings.bean.FavThingItem;
import com.qixie.myfavoritethings.db.MyFavDBHelper;

/**
 * The activity displays the details of single favorite item clicked
 * @author Qi Xie
 */
public class ItemDetailActivity extends AppCompatActivity {
    private TextView itemTitle;
    private TextView itemDescription;
    private ImageView itemImage;

    private FavThingItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        itemTitle = (TextView)findViewById(R.id.detail_title);
        itemImage = (ImageView)findViewById(R.id.detail_image);
        itemDescription = (TextView)findViewById(R.id.detail_description);

        /**
         * When this activity is started, we can get the rowId from the intent
         * We don't define an ID column in the table,so we use the rowId to select the desired item from the database
         */
        Intent intent = getIntent();
        int rowid = intent.getIntExtra("item_rowid",0);
        MyFavDBHelper helper = new MyFavDBHelper(ItemDetailActivity.this);
        item = helper.getSingleItem(rowid);

        itemTitle.setText(item.getTitle());
        itemDescription.setText(item.getDescription());
        byte[] imageArray = item.getImage();
        Bitmap bmp = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
        bmp = ThumbnailUtils.extractThumbnail(bmp, bmp.getWidth()/3, bmp.getHeight()/3);
        itemImage.setImageBitmap(bmp);



    }
}