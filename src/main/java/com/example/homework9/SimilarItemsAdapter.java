package com.example.homework9;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SimilarItemsAdapter extends RecyclerView.Adapter<SimilarItemsAdapter.ViewHolder> {

    private JSONArray mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    SimilarItemsAdapter(Context context, JSONArray data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.similar_item_card, viewGroup, false);
        return new SimilarItemsAdapter.ViewHolder(view);
    }

    public void setData(JSONArray data)
    {
        this.mData = data;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject similarItem = null;
        try
        {
            similarItem = (JSONObject) mData.get(position);

            String imageURL = similarItem.getString("imageURL");

            ImageView img = (ImageView) holder.cardView.findViewById(R.id.similar_image);

            Picasso.with(this.context).load(imageURL)
                    .into(img);

            String title = similarItem.getString("title");
            TextView titleView = (TextView) holder.cardView.findViewById(R.id.similar_title);
            titleView.setText(title);

            String price = similarItem.getJSONObject("buyItNowPrice").getString("__value__");
            TextView priceView = (TextView) holder.cardView.findViewById(R.id.similar_price);
            priceView.setText("$" + price);

            String shippingCost = similarItem.getJSONObject("shippingCost").getString("__value__");
            TextView shippingView = (TextView) holder.cardView.findViewById(R.id.shipping_cost);
            shippingView.setText((shippingCost.equals("0.00")) ? "Free Shipping" : "$" + shippingCost);

            String daysLeft = similarItem.getString("timeLeft").substring(1, similarItem.getString("timeLeft").indexOf("D"));
            TextView daysView = (TextView) holder.cardView.findViewById(R.id.days_left);
            daysView.setText(daysLeft + " days left");
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.length();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.similar_item_card);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());

            try {
                Intent similarItem = new Intent(Intent.ACTION_VIEW);
                similarItem.setData(Uri.parse(((JSONObject) mData.get(getAdapterPosition())).getString("viewItemURL")));
                context.startActivity(similarItem);
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    // convenience method for getting data at click position
    JSONObject getItem(int id) {
        try {
            return (JSONObject) mData.get(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}