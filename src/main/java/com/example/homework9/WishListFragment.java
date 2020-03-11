package com.example.homework9;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class WishListFragment extends Fragment {

    static ProductViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_wish_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences prefs = getContext().getSharedPreferences("WishList", 0);

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

        RecyclerView recyclerView = getActivity().findViewById(R.id.wish_list_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductViewAdapter(getContext(), wishList);
        recyclerView.setAdapter(adapter);

        ((TextView)getActivity().findViewById(R.id.total_items)).setText("Wishlist total ("+ numItems +" item" + ((numItems != 1) ? "s" : "") + ")");
        ((TextView)getActivity().findViewById(R.id.total_cost)).setText("$" + cost);

        if (numItems == 0)
        {
            ((SearchActivity)getContext()).findViewById(R.id.no_wishes).setVisibility(View.VISIBLE);
            ((SearchActivity)getContext()).findViewById(R.id.wish_list_recycler).setVisibility(View.INVISIBLE);
        }
    }
}
