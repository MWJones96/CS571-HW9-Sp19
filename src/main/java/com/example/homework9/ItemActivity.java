package com.example.homework9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ItemActivity extends AppCompatActivity {

    //The JSON from the results page
    private JSONObject pageObj = new JSONObject();

    //The JSON from an API call
    private JSONObject itemObj = new JSONObject();

    private JSONArray photosObj = new JSONArray();

    private JSONObject similarObj = new JSONObject();

    private int reqCompleted = 0;

    private MainTabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public JSONObject getPageObj()
    {
        return this.pageObj;
    }

    public JSONObject getItemObj()
    {
        return this.itemObj;
    }

    public JSONArray getPhotosObj()
    {
        return this.photosObj;
    }

    public JSONObject getSimilarObj()
    {
        return this.similarObj;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        String stringified = getIntent().getStringExtra("pageData");
        try {
            pageObj = new JSONObject(stringified);
            String title = pageObj.has("title") ? pageObj.getJSONArray("title").getString(0) : "N/A";

            setTitle(title.length() > 25 ? title.substring(0,25) + "..." : title);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("WishList", 0);

        RequestQueue queue = Volley.newRequestQueue(this);

        String itemID = "";
        String title = "";

        try {
            itemID = pageObj.getJSONArray("itemId").getString(0);
            title = pageObj.getJSONArray("title").getString(0);

            if (pref.contains(pageObj.getJSONArray("itemId").getString(0)))
            {
                ((FloatingActionButton)findViewById(R.id.item_add_remove_wishlist)).setImageResource(R.drawable.cart_remove);
            }
            else
            {
                ((FloatingActionButton)findViewById(R.id.item_add_remove_wishlist)).setImageResource(R.drawable.cart_plus);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Calling the Homework 8 backend to get results JSON
        String itemURL = "http://homework-8-xcvtdfedsfd.appspot.com/jsonItem?ItemID=" + itemID;
        String photosURL = "";
        try {
            //Make Google Search Term URL-safe
            photosURL = "http://homework-8-xcvtdfedsfd.appspot.com/googleImg?SearchTerm=" + URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String similarURL = "http://homework-8-xcvtdfedsfd.appspot.com/similarItems?Item=" + itemID;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, itemURL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        ItemActivity.this.itemObj = response;
                        ItemActivity.this.reqCompleted++;

                        if (reqCompleted == 3) { showTabs(); }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ItemActivity.this, "Unable to retrieve item details", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        );

        JsonArrayRequest getRequestPhotos = new JsonArrayRequest(Request.Method.GET, photosURL, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("Response", response.toString());
                            ItemActivity.this.photosObj = response;
                            ItemActivity.this.reqCompleted++;

                            if (reqCompleted == 3) { showTabs(); }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ItemActivity.this.photosObj = null;
                        ItemActivity.this.reqCompleted++;

                        if (reqCompleted == 3) { showTabs(); }
                    }
                }
        );

        JsonObjectRequest getRequestSimilar = new JsonObjectRequest(Request.Method.GET, similarURL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        ItemActivity.this.similarObj = response;
                        ItemActivity.this.reqCompleted++;

                        if (reqCompleted == 3) { showTabs(); }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ItemActivity.this.similarObj = null;
                        ItemActivity.this.reqCompleted++;

                        if (reqCompleted == 3) { showTabs(); }
                    }
                }
        );

        queue.add(getRequest);
        queue.add(getRequestPhotos);
        queue.add(getRequestSimilar);
    }

    protected void showTabs()
    {
        viewPager = findViewById(R.id.item_tabs_pager);
        tabLayout = findViewById(R.id.item_tabs);
        adapter = new MainTabAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProductFragment(), "Product");
        adapter.addFragment(new ShippingFragment(), "Shipping");
        adapter.addFragment(new PhotoFragment(), "Photos");
        adapter.addFragment(new SimilarItemsFragment(), "Similar");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.info);
        tabLayout.getTabAt(1).setIcon(R.drawable.shipping);
        tabLayout.getTabAt(2).setIcon(R.drawable.photos);
        tabLayout.getTabAt(3).setIcon(R.drawable.similar);

        findViewById(R.id.item_tabs).setVisibility(View.VISIBLE);
        findViewById(R.id.loading_item_bar).setVisibility(View.GONE);
        findViewById(R.id.loading_item_text).setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.facebook_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void facebookShare(MenuItem item) throws JSONException, UnsupportedEncodingException
    {
        //Make sure item data is converted to a URL-safe format
        String URL = "https://www.facebook.com/dialog/share?app_id=409037563257362" +
                "&display=popup&href=" + getItemObj().getString("ViewItemURLForNaturalSearch") +
                "&quote=Buy+" + URLEncoder.encode(getItemObj().getString("Title"), "UTF-8") + "+for+$" +
                URLEncoder.encode(getItemObj().getJSONObject("CurrentPrice").getString("Value"), "UTF-8") + "+from+Ebay" +
                "&hashtag=%23CSCI571Spring2019Ebay";

        Intent fbShare = new Intent(Intent.ACTION_VIEW);
        fbShare.setData(Uri.parse(URL));
        startActivity(fbShare);
    }

    public void toggleWishlist(View view) throws JSONException {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("WishList", 0); // 0 - for private mode

        SharedPreferences.Editor editor = pref.edit();
        if (pref.contains(getItemObj().getString("ItemID")))
        {
            //Item is in wishlist
            editor.remove(getItemObj().getString("ItemID"));
            editor.apply();

            ((FloatingActionButton)findViewById(R.id.item_add_remove_wishlist)).setImageResource(R.drawable.cart_plus);

            Toast.makeText(this, "Removing " + getItemObj().getString("Title") + " from wishlist", Toast.LENGTH_LONG).show();
        }
        else
        {
            //Item is not in wishlist
            editor.putString(getItemObj().getString("ItemID"), getPageObj().toString());
            editor.commit();

            ((FloatingActionButton)findViewById(R.id.item_add_remove_wishlist)).setImageResource(R.drawable.cart_remove);

            Toast.makeText(this, "Adding " + getItemObj().getString("Title") + " to wishlist", Toast.LENGTH_LONG).show();
        }

        if (ProductListingActivity.adapter != null)
            ProductListingActivity.adapter.notifyDataSetChanged();

        if (WishListFragment.adapter != null)
        {
            WishListFragment.adapter.updateData();
            WishListFragment.adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}