package com.example.homework9;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemPhotoAdapter extends RecyclerView.Adapter<ItemPhotoAdapter.ViewHolder> {

    private JSONArray mData;
    private LayoutInflater mInflater;
    private Context context;

    // data is passed into the constructor
    ItemPhotoAdapter(Context context, JSONArray data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.google_img, viewGroup, false);
        return new ItemPhotoAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject photo = null;
        try
        {
            photo = (JSONObject) mData.get(position);

            ImageView googleImg = holder.itemView.findViewById(R.id.google_photo);
            Picasso.with(this.context).load(photo.getString("link"))
                    .into(googleImg);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ViewHolder(View itemView)
        {
            super(itemView);
        }

        @Override
        public void onClick(View v)
        {

        }
    }
}