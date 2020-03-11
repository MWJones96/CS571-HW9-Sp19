package com.example.homework9;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Map;

public class ProductViewAdapter extends RecyclerView.Adapter<ProductViewAdapter.ViewHolder> {
    private JSONArray mData;
    private LayoutInflater mInflater;
    private Context context;

    ProductViewAdapter(Context context, JSONArray data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    public void updateData()
    {
        SharedPreferences prefs = context.getSharedPreferences("WishList", 0);
        Map<String,?> keys = prefs.getAll();

        JSONArray wishList = new JSONArray();

        double totalCost = 0.0;

        for(Map.Entry<String,?> entry : keys.entrySet()){
            System.out.println(entry.getKey() + ": " +
                    entry.getValue().toString());
            try {
                wishList.put(new JSONObject(entry.getValue().toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int numItems = wishList.length();

        for (int i = 0; i < numItems; i++){
            try {
                totalCost += Double.parseDouble(wishList.getJSONObject(i).getJSONArray("sellingStatus").getJSONObject(0).getJSONArray("currentPrice").getJSONObject(0).getString("__value__"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String cost = String.format("%.2f", totalCost);
        ((TextView)((SearchActivity)context).findViewById(R.id.total_items)).setText("Wishlist total ("+ numItems +" item" + ((numItems != 1) ? "s" : "") + ")");
        ((TextView)((SearchActivity)context).findViewById(R.id.total_cost)).setText("$" + cost);

        this.mData = wishList;

        if (numItems == 0)
        {
            ((SearchActivity) context).findViewById(R.id.no_wishes).setVisibility(View.VISIBLE);
            ((SearchActivity) context).findViewById(R.id.wish_list_recycler).setVisibility(View.INVISIBLE);
        }
        else
        {
            ((SearchActivity) context).findViewById(R.id.no_wishes).setVisibility(View.GONE);
            ((SearchActivity) context).findViewById(R.id.wish_list_recycler).setVisibility(View.VISIBLE);
        }
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject product = null;
        try {
            product = (JSONObject) mData.get(position);

            DecimalFormat df = new DecimalFormat("#.00");

            String productTitle = (product.has("title")) ? product.getJSONArray("title").getString(0) : "N/A";
            if (productTitle.length() > 50)
            {
                productTitle = productTitle.substring(0,50) + "...";
            }

            String productZip = (product.has("postalCode")) ? product.getJSONArray("postalCode").getString(0)  : "N/A";
            String productShippingCost = (product.has("shippingInfo")) ?
                    product.getJSONArray("shippingInfo").getJSONObject(0)
                            .getJSONArray("shippingServiceCost").getJSONObject(0).
                                    getString("__value__").equals("0.0") ? "Free Shipping" : "$" + String.format("%.2f", Float.parseFloat(product.getJSONArray("shippingInfo").getJSONObject(0)
                            .getJSONArray("shippingServiceCost").getJSONObject(0).
                                    getString("__value__"))) : "N/A";

            String condition = (product.has("condition")) ? product.getJSONArray("condition").getJSONObject(0).getJSONArray("conditionDisplayName").getString(0) : "N/A";
            System.out.println(condition);

            if (condition.endsWith("refurbished")) { condition = "Refurbished"; }

            String price = (product.has("sellingStatus")) ? "$" + String.format("%.2f", Float.parseFloat(product.getJSONArray("sellingStatus").getJSONObject(0).getJSONArray("currentPrice").getJSONObject(0).getString("__value__"))) : "N/A";


            ImageView productImg = (ImageView)holder.cardView.findViewById(R.id.product_image);
            Picasso.with(this.context).load(product.getJSONArray("galleryURL").getString(0))
                    .into(productImg);

            ((TextView)holder.cardView.findViewById(R.id.product_title)).setText(productTitle);
            ((TextView)holder.cardView.findViewById(R.id.zipAndShipping)).setText("Zip: " + productZip + "\n" + productShippingCost);
            ((TextView)holder.cardView.findViewById(R.id.condition)).setText(condition);
            ((TextView)holder.cardView.findViewById(R.id.price)).setText(price);

            SharedPreferences prefs = context.getSharedPreferences("WishList", 0);
            if (prefs.contains(product.getJSONArray("itemId").getString(0)))
            {
                ((ImageView)holder.cardView.findViewById(R.id.shopping_cart)).setImageResource(R.drawable.cart_remove);
                ((ImageView)holder.cardView.findViewById(R.id.shopping_cart)).setColorFilter(context.getResources().getColor(R.color.colorSecondary));
            }
            else
            {
                ((ImageView)holder.cardView.findViewById(R.id.shopping_cart)).setImageResource(R.drawable.cart_plus);
                ((ImageView)holder.cardView.findViewById(R.id.shopping_cart)).setColorFilter(context.getResources().getColor(R.color.colorDefault));
            }
        } catch (JSONException e) {
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
        ImageView toggleWL;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.product_card);
            toggleWL = itemView.findViewById(R.id.shopping_cart);
            cardView.setOnClickListener(this);
            toggleWL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String itemID = ((JSONObject) ProductViewAdapter.this.mData.get(getAdapterPosition())).getJSONArray("itemId").getString(0);
                        System.out.println(itemID);

                        SharedPreferences pref = context.getSharedPreferences("WishList", 0);

                        SharedPreferences.Editor editor = pref.edit();
                        if (pref.contains(itemID))
                        {
                            editor.remove(itemID);
                            editor.apply();

                            Toast.makeText(context, "Removing " + ((TextView)cardView.findViewById(R.id.product_title)).getText() + " from wishlist", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //Item is not in wishlist
                            editor.putString(itemID, ProductViewAdapter.this.mData.get(getAdapterPosition()).toString());
                            editor.commit();

                            Toast.makeText(context, "Adding " + ((TextView)cardView.findViewById(R.id.product_title)).getText() + " to wishlist", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (ProductListingActivity.adapter != null)
                        ProductListingActivity.adapter.notifyDataSetChanged();

                    if (WishListFragment.adapter != null)
                    {
                        WishListFragment.adapter.updateData();
                        WishListFragment.adapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void onClick(View view)
        {
            Intent intent = new Intent(context, ItemActivity.class);
            intent.putExtra("pageData", getItem(getAdapterPosition()).toString());
            context.startActivity(intent);
        }
    }

    JSONObject getItem(int id) {
        try {
            return (JSONObject) mData.get(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}