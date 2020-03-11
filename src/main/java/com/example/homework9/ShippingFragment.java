package com.example.homework9;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wssholmes.stark.circular_score.CircularScoreView;

import org.json.JSONException;
import org.json.JSONObject;

public class ShippingFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shipping, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        JSONObject pageObj = ((ItemActivity)getActivity()).getPageObj();
        final JSONObject itemObj = ((ItemActivity)getActivity()).getItemObj();

            try {
                if (itemObj.has("Seller"))
                {
                    int score = Integer.parseInt(itemObj.getJSONObject("Seller").getString("FeedbackScore"));

                    if (itemObj.has("Storefront") && itemObj.getJSONObject("Storefront").has("StoreName"))
                    {
                        String anchor = "<a href=\"" + itemObj.getJSONObject("Storefront").getString("StoreURL") + "\">" + itemObj.getJSONObject("Storefront").getString("StoreName") + "</a>";

                        ((TextView)getActivity().findViewById(R.id.store)).setText(Html.fromHtml(anchor));
                        getActivity().findViewById(R.id.store).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v){
                                try {
                                    Intent store = new Intent(Intent.ACTION_VIEW);
                                    store.setData(Uri.parse(itemObj.getJSONObject("Storefront").getString("StoreURL")));
                                    startActivity(store);
                                }
                                catch (Exception e) { e.printStackTrace(); }
                            }
                        });

                        getActivity().findViewById(R.id.store_name_label).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.store).setVisibility(View.VISIBLE);
                    }

                    ((TextView)getActivity().findViewById(R.id.score)).setText(itemObj.getJSONObject("Seller").getString("FeedbackScore"));

                    CircularScoreView circularScoreView = getActivity().findViewById(R.id.pop_score_view);
                    circularScoreView.setScore((int)Double.parseDouble(itemObj.getJSONObject("Seller").getString("PositiveFeedbackPercent")));

                    if (score >= 10) {
                        String color = itemObj.getJSONObject("Seller").getString("FeedbackRatingStar");
                        if (score >= 10000) {
                            ((ImageView) getActivity().findViewById(R.id.feedback_star)).setImageResource(R.drawable.star_circle);

                            if (color.equals("TurquoiseShooting"))
                                color = "#20BBACShooting";

                            ((ImageView) getActivity().findViewById(R.id.feedback_star)).setColorFilter(Color.parseColor(color.substring(0, color.length() - 8)));
                        } else {
                            ((ImageView) getActivity().findViewById(R.id.feedback_star)).setImageResource(R.drawable.star_circle_outline);

                            if (color.equals("Turquoise"))
                                color = "#20BBAC";

                            ((ImageView) getActivity().findViewById(R.id.feedback_star)).setColorFilter(Color.parseColor(color));
                        }

                        getActivity().findViewById(R.id.feedback_star).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.star_label).setVisibility(View.VISIBLE);
                    }
                }

                String productShippingCost = (pageObj.has("shippingInfo")) ?
                        pageObj.getJSONArray("shippingInfo").getJSONObject(0)
                                .getJSONArray("shippingServiceCost").getJSONObject(0).
                                getString("__value__").equals("0.0") ? "Free Shipping" : "$" + String.format("%.2f", Float.parseFloat(pageObj.getJSONArray("shippingInfo").getJSONObject(0)
                                .getJSONArray("shippingServiceCost").getJSONObject(0).
                                        getString("__value__"))) : "N/A";

                ((TextView)getActivity().findViewById(R.id.shipping_cost_text)).setText(productShippingCost);

                if (itemObj.has("GlobalShipping"))
                {
                    ((TextView)getActivity().findViewById(R.id.global_shipping_text)).setText(itemObj.getBoolean("GlobalShipping") ? "Yes" : "No");
                    getActivity().findViewById(R.id.global_shipping_text).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.global_shipping_label).setVisibility(View.VISIBLE);
                }

                if (itemObj.has("HandlingTime"))
                {
                    ((TextView)getActivity().findViewById(R.id.handling_time_text)).setText(itemObj.getString("HandlingTime") + ((!itemObj.getString("HandlingTime").equals("1")) ? " Days" : " Day"));
                    getActivity().findViewById(R.id.handling_time_text).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.handling_time_label).setVisibility(View.VISIBLE);
                }
                if (itemObj.has("ConditionDisplayName"))
                {
                    ((TextView)getActivity().findViewById(R.id.condition_text)).setText(itemObj.getString("ConditionDisplayName"));
                    getActivity().findViewById(R.id.condition_text).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.condition_label).setVisibility(View.VISIBLE);
                }
                    ((TextView)getActivity().findViewById(R.id.policy_text)).setText(itemObj.getJSONObject("ReturnPolicy").getString("ReturnsAccepted").equals("Returns Accepted") ? "Returns Accepted" : "Returns Not Accepted");

                    if (itemObj.getJSONObject("ReturnPolicy").getString("ReturnsAccepted").equals("Returns Accepted")) {
                        ((TextView) getActivity().findViewById(R.id.returns_within_text)).setText(itemObj.getJSONObject("ReturnPolicy").getString("ReturnsWithin"));
                        ((TextView) getActivity().findViewById(R.id.refund_mode_text)).setText(itemObj.getJSONObject("ReturnPolicy").getString("Refund"));
                        ((TextView) getActivity().findViewById(R.id.shipped_by_text)).setText(itemObj.getJSONObject("ReturnPolicy").getString("ShippingCostPaidBy"));

                        getActivity().findViewById(R.id.returns_within_label).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.refund_mode_label).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.shipped_by_label).setVisibility(View.VISIBLE);

                        getActivity().findViewById(R.id.returns_within_text).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.refund_mode_text).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.shipped_by_text).setVisibility(View.VISIBLE);
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
}
