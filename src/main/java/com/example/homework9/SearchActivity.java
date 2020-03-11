package com.example.homework9;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity
{
    private MainTabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Product Search");

        viewPager = findViewById(R.id.main_tabs_pager);
        tabLayout = findViewById(R.id.main_tabs);
        adapter = new MainTabAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchFragment(), "Search");
        adapter.addFragment(new WishListFragment(), "Wish List");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    protected void toggleNearbySearch(View v)
    {
        int toggle = (((CheckBox)findViewById(R.id.enable_nearby)).isChecked()) ? View.VISIBLE : View.GONE;

        findViewById(R.id.miles_from).setVisibility(toggle);
        findViewById(R.id.from_label).setVisibility(toggle);

        findViewById(R.id.zipcode_radio_group).setVisibility(toggle);
        findViewById(R.id.zipField).setVisibility(toggle);
        findViewById(R.id.current_loc_label).setVisibility(toggle);
        findViewById(R.id.zip_error).setVisibility(View.GONE);
    }

    protected void submitForm(View v)
    {
        findViewById(R.id.kwd_error).setVisibility(View.GONE);
        findViewById(R.id.zip_error).setVisibility(View.GONE);

        boolean kwdError = ((TextView)findViewById(R.id.kwd_field)).getText().toString().trim().length() == 0;
        boolean zipError = ((CheckBox)findViewById(R.id.enable_nearby)).isChecked() && ((RadioButton)findViewById(R.id.zip_radio)).isChecked()
                && ((EditText)findViewById(R.id.zipField)).getText().toString().trim().length() == 0;

        if (kwdError)
        {
            findViewById(R.id.kwd_error).setVisibility(View.VISIBLE);
        }

        if (zipError)
        {
            findViewById(R.id.zip_error).setVisibility(View.VISIBLE);
        }

        if (kwdError || zipError)
        {
            Toast err = Toast.makeText(this, "Please fix all fields with errors", Toast.LENGTH_SHORT);
            err.show();
            return;
        }

        //Go to the search activity
        Intent intent = new Intent(this, ProductListingActivity.class);
        intent.putExtra("kwd", ((TextView)findViewById(R.id.kwd_field)).getText().toString());
        intent.putExtra("category", ((Spinner)findViewById(R.id.category_select)).getSelectedItem().toString());
        intent.putExtra("condNew", ((CheckBox)findViewById(R.id.new_btn)).isChecked());
        intent.putExtra("condUsed", ((CheckBox)findViewById(R.id.used_btn)).isChecked());
        intent.putExtra("condUnspec", ((CheckBox)findViewById(R.id.unspec_btn)).isChecked());
        intent.putExtra("localPickup", ((CheckBox)findViewById(R.id.local_pickup)).isChecked());
        intent.putExtra("freeShipping", ((CheckBox)findViewById(R.id.free_shipping)).isChecked());
        intent.putExtra("enableNearby", ((CheckBox)findViewById(R.id.enable_nearby)).isChecked());
        intent.putExtra("miles", ((TextView)findViewById(R.id.miles_from)).getText().toString());
        intent.putExtra("zipSelection", ((RadioButton)findViewById(R.id.current_loc_radio)).isChecked());
        intent.putExtra("zipField", ((TextView)findViewById(R.id.zipField)).getText().toString());

        startActivity(intent);
    }

    protected void clearForm(View v)
    {
        ((EditText)findViewById(R.id.kwd_field)).setText("");
        findViewById(R.id.kwd_error).setVisibility(View.GONE);
        findViewById(R.id.zip_error).setVisibility(View.GONE);

        ((Spinner)findViewById(R.id.category_select)).setSelection(0);

        ((CheckBox)findViewById(R.id.new_btn)).setChecked(false);
        ((CheckBox)findViewById(R.id.used_btn)).setChecked(false);
        ((CheckBox)findViewById(R.id.unspec_btn)).setChecked(false);

        ((CheckBox)findViewById(R.id.local_pickup)).setChecked(false);
        ((CheckBox)findViewById(R.id.free_shipping)).setChecked(false);

        ((CheckBox)findViewById(R.id.enable_nearby)).setChecked(false);
        ((EditText)findViewById(R.id.miles_from)).setText("");
        ((EditText)findViewById(R.id.zipField)).setText("");

        ((RadioButton)findViewById(R.id.current_loc_radio)).setChecked(true);
        ((RadioButton)findViewById(R.id.zip_radio)).setChecked(false);

        findViewById(R.id.miles_from).setVisibility(View.GONE);
        findViewById(R.id.zipField).setVisibility(View.GONE);
        findViewById(R.id.from_label).setVisibility(View.GONE);
        findViewById(R.id.current_loc_label).setVisibility(View.GONE);
        findViewById(R.id.zipcode_radio_group).setVisibility(View.GONE);
        findViewById(R.id.zipField).setEnabled(false);
    }

    public void enableZip(View view)
    {
        findViewById(R.id.zipField).setEnabled(true);
    }

    public void disableZip(View view)
    {
        findViewById(R.id.zipField).setEnabled(false);
    }
}
