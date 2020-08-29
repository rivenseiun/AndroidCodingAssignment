package com.qixie.myfavoritethings.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qixie.myfavoritethings.R;
import com.qixie.myfavoritethings.bean.FavThingItem;

import java.util.List;

/**
 * Adapter of the recyclerview in MainActivity
 */
public class FavListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private List<FavThingItem> items;
    private OnItemClickListener listener;
    private Context context;


    public FavListAdapter(Context context,List<FavThingItem> items){
        this.context = context;
        this.items = items;

    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind the item layout to view holder
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.favlist_item, null);
        RecyclerView.ViewHolder holder = new FavViewHolder(item);
        item.setOnClickListener(this);

        return holder;
       
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final FavViewHolder favHolder = (FavViewHolder) holder;
        FavThingItem curritem = items.get(position);

        /**
         * convert the image byte array to bitmap
         */
        byte[] imageArray = curritem.getImage();
        Bitmap bmp = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);;
        bmp = ThumbnailUtils.extractThumbnail(bmp, 200,
               200);
        String displayTitle = curritem.getTitle();

        /**
         *  if the original title is too long: using the first 20 character as a display title
         */
        if(displayTitle.length()>20){
            displayTitle = displayTitle.substring(0,20)+"..";
        }

        favHolder.itemImage.setImageBitmap(bmp);
        favHolder.itemTitle.setText(displayTitle);
        favHolder.itemTimestamp.setText(curritem.getTimeStamp());
        favHolder.itemView.setTag(position);





    }



    @Override
    public int getItemCount() {
        return items.size();

    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onItemClick(view, (int) view.getTag());
        }

    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * ViewHolder inner class
     */
    class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        TextView itemTimestamp;
        LinearLayout itemLayout;

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemTimestamp = (TextView) itemView.findViewById(R.id.item_timestamp);
            itemLayout =(LinearLayout)itemView.findViewById(R.id.item_layout);

        }
    }
}

