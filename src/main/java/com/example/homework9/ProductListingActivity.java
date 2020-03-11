package com.example.homework9;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

public class ProductListingActivity extends AppCompatActivity {

    static ProductViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_listing);

        String keyword = "";

        try {
            keyword = URLEncoder.encode(getIntent().getStringExtra("kwd"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String kwd = keyword;
        final String rawKwd = getIntent().getStringExtra("kwd");

        final String category = getIntent().getStringExtra("category");

        final String newBtn = (getIntent().getBooleanExtra("condNew", true)) ? "true" : "false";
        final String usedBtn = (getIntent().getBooleanExtra("condUsed", true)) ? "true" : "false";
        final String unspecBtn = (getIntent().getBooleanExtra("condUnspec", true)) ? "true" : "false";

        final String local = (getIntent().getBooleanExtra("localPickup", true)) ? "true" : "false";
        final String free = (getIntent().getBooleanExtra("freeShipping", true)) ? "true" : "false";

        final String distance = (getIntent().getBooleanExtra("enableNearby", true)) ? getIntent().getStringExtra("miles") : "0";

        final String here = (getIntent().getBooleanExtra("zipSelection", true)) ? "true" : "false";
        final String zip = getIntent().getStringExtra("zipField");

        RequestQueue queue = Volley.newRequestQueue(this);

        System.out.println(category);

        String categoryStr = "";
        if (category.equals("All")) {
            categoryStr = "all";
        }
        if (category.equals("Art")) {
            categoryStr = "550";
        }
        if (category.equals("Baby")) {
            categoryStr = "2984";
        }
        if (category.equals("Books")) {
            categoryStr = "267";
        }
        if (category.equals("Clothing, Shoes & Accessories")) {
            categoryStr = "11450";
        }
        if (category.equals("Computers, Tablets & Networking")) {
            categoryStr = "58058";
        }
        if (category.equals("Health & Beauty")) {
            categoryStr = "26395";
        }
        if (category.equals("Music")) {
            categoryStr = "11233";
        }
        if (category.equals("Video Games & Consoles")) {
            categoryStr = "1249";
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String provider = lm.getBestProvider(new Criteria(), true);

        Location loc = lm.getLastKnownLocation(provider);

        double lat = loc != null ? loc.getLatitude() : 34.022350;
        double lon = loc != null ? loc.getLongitude(): -118.285118;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String userLocZip = addresses != null ? addresses.get(0).getPostalCode() : "90089";
        System.out.println(userLocZip);

        //Calling the Homework 8 backend to get results JSON
        final String url = "http://homework-8-xcvtdfedsfd.appspot.com/json?Keyword=" + kwd + "&Category=" + categoryStr +
                "&ConditionNew=" + newBtn + "&ConditionUsed=" + usedBtn + "&ConditionUnspec=" + unspecBtn +
                "&LocalPickup=" + local + "&FreeShipping=" + free + "&Distance=" + distance + "&Here=" + here + "&" +
                "ZipActive=false&Zip=" + zip + "&UserLocZip=" + userLocZip;

        System.out.println("Getting " + url);
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("Response", response.toString());
                        findViewById(R.id.loading_products).setVisibility(View.GONE);
                        findViewById(R.id.loading_products_label).setVisibility(View.GONE);

                        if (response.length() == 0)
                        {
                            findViewById(R.id.no_results).setVisibility(View.VISIBLE);
                            return;
                        }

                        String resultsHeading = "<strong>Showing <span style=\"color: #FC5830\">" + response.length() +
                                "</span> result" + ((response.length() != 1) ? "s" : "") + " for <span style=\"color: #FC5830\">" + rawKwd + "</span></strong>";

                        ((TextView)findViewById(R.id.results_heading)).setText(Html.fromHtml(resultsHeading));

                        RecyclerView recyclerView = findViewById(R.id.itemLayout);
                        recyclerView.setLayoutManager(new GridLayoutManager(ProductListingActivity.this, 2));
                        adapter = new ProductViewAdapter(ProductListingActivity.this, response);
                        recyclerView.setAdapter(adapter);

                        findViewById(R.id.itemLayout).setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.toString().contains("ParseError"))
                        {
                            findViewById(R.id.loading_products).setVisibility(View.GONE);
                            findViewById(R.id.loading_products_label).setVisibility(View.GONE);
                            findViewById(R.id.no_results).setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
        );

        queue.add(getRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
