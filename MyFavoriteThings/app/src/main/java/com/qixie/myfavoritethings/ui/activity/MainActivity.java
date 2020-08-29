package com.qixie.myfavoritethings.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qixie.myfavoritethings.R;
import com.qixie.myfavoritethings.adapter.FavListAdapter;
import com.qixie.myfavoritethings.bean.FavThingItem;
import com.qixie.myfavoritethings.db.MyFavDBHelper;

import java.util.List;


/**
 * Main activity: Display all the favorite items in database
 * @author Qi Xie
 */
public class MainActivity extends AppCompatActivity {
    private Button btn;
    private RecyclerView favList;
    private TextView emptyText;
    private List<FavThingItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.btn = (Button) findViewById(R.id.new_btn);
        this.emptyText = (TextView) findViewById(R.id.emtpy_text);
        this.favList = (RecyclerView) findViewById(R.id.fav_list);


        //set button onclick listener: start the AddItemActivity
        btn.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                                       startActivity(intent);
                                       finish();

                                   }
                               }

        );

        MyFavDBHelper dbhelper = new MyFavDBHelper(MainActivity.this);
        items = dbhelper.getData();

        favList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        FavListAdapter adapter = new FavListAdapter(MainActivity.this, items);

        /**
         * Set adapter item onClick listener:
         * When item is clicked, pass its rowId to ItemDetailActivity
         * ItemDetailActivity will display the details of the item with the rowId
         */
        adapter.setOnItemClickListener(new FavListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(MainActivity.this,"Item "+position+"Clicked!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
                FavThingItem curritem = items.get(position);
                intent.putExtra("item_rowid", curritem.getRowid());
                startActivity(intent);


            }
        });


        favList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        /**
         * If favorite list is empty, show the empty text
         */
        if (items.size() == 0) {
            favList.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        }


    }
}

