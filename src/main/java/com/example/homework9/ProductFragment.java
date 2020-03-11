package com.example.homework9;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ProductFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        JSONObject itemObj = ((ItemActivity)getActivity()).getItemObj();
        JSONObject pageObj = ((ItemActivity)getActivity()).getPageObj();

        System.out.println(itemObj.toString());

        try {

            if (itemObj.getJSONArray("PictureURL").length() > 0)
            {
                LinearLayout images = getActivity().findViewById(R.id.product_images);

                for (int i = 0; i < itemObj.getJSONArray("PictureURL").length(); i++)
                {
                    ImageView productImage = new ImageView(getContext());
                    productImage.setAdjustViewBounds(true);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params.setMargins(10, 20, 10, 20);
                    productImage.setLayoutParams(params);
                    Picasso.with(getContext()).load(itemObj.getJSONArray("PictureURL").getString(i)).into(productImage);
                    images.addView(productImage);
                }
            }
            else
            {
                getActivity().findViewById(R.id.imageView).setVisibility(View.GONE);
            }

            ((TextView)getActivity().findViewById(R.id.item_title)).setText(itemObj.getString("Title"));

            String cost = String.format("%.2f", Float.parseFloat(itemObj.getJSONObject("CurrentPrice").getString("Value")));
            String shippingCost = String.format("%.2f", Float.parseFloat(pageObj.getJSONArray("shippingInfo").getJSONObject(0)
                    .getJSONArray("shippingServiceCost").getJSONObject(0).
                            getString("__value__")));

            String htmlText = "<span style=\"color: #6200EE\"><strong>$" + cost + "</strong></span> With "
                    + (shippingCost.equals("0.00") ? "Free Shipping" : "$" + shippingCost + " Shipping");

            ((TextView)getActivity().findViewById(R.id.price_and_shipping)).setText(Html.fromHtml(htmlText));

            if (itemObj.has("Subtitle"))
            {
                ((TextView)getActivity().findViewById(R.id.subtitle_text)).setText(itemObj.getString("Subtitle"));
                ((TextView)getActivity().findViewById(R.id.subtitle_text)).setVisibility(View.VISIBLE);
                ((TextView)getActivity().findViewById(R.id.subtitle_label)).setVisibility(View.VISIBLE);
            }

            ((TextView)getActivity().findViewById(R.id.hl_price_text)).setText("$" + cost);

            if (itemObj.has("ItemSpecifics")) {
                getActivity().findViewById(R.id.item_spec_bar).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.item_spec_img).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.specifics).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.spec_heading).setVisibility(View.VISIBLE);

                for (int i = 0; i < itemObj.getJSONObject("ItemSpecifics").getJSONArray("NameValueList").length(); i++) {
                    String name = itemObj.getJSONObject("ItemSpecifics").getJSONArray("NameValueList").getJSONObject(i).getString("Name");
                    if (name.equals("Brand")) {
                        ((TextView) getActivity().findViewById(R.id.brand_text)).setText(itemObj.getJSONObject("ItemSpecifics").getJSONArray("NameValueList").getJSONObject(i).getJSONArray("Value").getString(0));
                        ((TextView) getActivity().findViewById(R.id.brand_text)).setVisibility(View.VISIBLE);
                        ((TextView) getActivity().findViewById(R.id.brand_label)).setVisibility(View.VISIBLE);
                    }

                    TextView tv = new TextView(getContext());

                    String specific = itemObj.getJSONObject("ItemSpecifics").getJSONArray("NameValueList").getJSONObject(i).getJSONArray("Value").getString(0);
                    tv.setText("\u2022 " + specific.substring(0, 1).toUpperCase() + specific.substring(1));

                    if (name.equals("Brand"))
                        ((LinearLayout) getActivity().findViewById(R.id.specifics)).addView(tv, 0);
                    else
                        ((LinearLayout) getActivity().findViewById(R.id.specifics)).addView(tv);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
