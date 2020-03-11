package com.example.homework9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SimilarItemsFragment extends Fragment {
    private SimilarItemsAdapter adapter;

    private static Comparator<JSONObject> priceComparator = new Comparator<JSONObject>() {
        @Override
        public int compare(JSONObject o1, JSONObject o2) {
            double priceo1 = 0.0;
            double priceo2 = 0.0;
            try {
                priceo1 = Double.parseDouble(o1.getJSONObject("buyItNowPrice").getString("__value__"));
                priceo2 = Double.parseDouble(o2.getJSONObject("buyItNowPrice").getString("__value__"));;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (priceo1 < priceo2)
            {
                return -1;
            }
            else if (priceo1 > priceo2)
            {
                return 1;
            }
            else { return 0; }
        }
    };

    private static Comparator<JSONObject> daysComparator = new Comparator<JSONObject>() {
        @Override
        public int compare(JSONObject o1, JSONObject o2) {
            int dayso1 = 0;
            int dayso2 = 0;

            try {
                dayso1 = Integer.parseInt(o1.getString("timeLeft").substring(1, o1.getString("timeLeft").indexOf("D")));
                dayso2 = Integer.parseInt(o2.getString("timeLeft").substring(1, o2.getString("timeLeft").indexOf("D")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return dayso1 - dayso2;
        }
    };

    private static Comparator<JSONObject> nameComparator = new Comparator<JSONObject>() {
        @Override
        public int compare(JSONObject o1, JSONObject o2) {
            String nameo1 = "";
            String nameo2 = "";

            try {
                nameo1 = o1.getString("title");
                nameo2 = o2.getString("title");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return nameo1.compareTo(nameo2);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_similar_items, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)  {
        super.onActivityCreated(savedInstanceState);

        getActivity().findViewById(R.id.ad).setEnabled(false);
        Spinner sortBy = getActivity().findViewById(R.id.sort_by);
        Spinner ad = getActivity().findViewById(R.id.ad);

        try {
            if (((ItemActivity) getActivity()).getSimilarObj() == null || ((ItemActivity) getActivity()).getSimilarObj().getJSONObject("getSimilarItemsResponse").getJSONObject("itemRecommendations").getJSONArray("item").length() == 0) {
                getActivity().findViewById(R.id.sort_by).setEnabled(false);
                getActivity().findViewById(R.id.similar_item_recycler).setVisibility(View.GONE);
                getActivity().findViewById(R.id.no_similar_items).setVisibility(View.VISIBLE);

                return;
            }
        }
        catch(Exception e)
        {
            getActivity().findViewById(R.id.sort_by).setEnabled(false);
            getActivity().findViewById(R.id.similar_item_recycler).setVisibility(View.GONE);
            getActivity().findViewById(R.id.no_similar_items).setVisibility(View.VISIBLE);

            return;
        }


        AdapterView.OnItemSelectedListener sortListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String sortBy = ((Spinner)getActivity().findViewById(R.id.sort_by)).getSelectedItem().toString();
                String ad = ((Spinner)getActivity().findViewById(R.id.ad)).getSelectedItem().toString();

                ((Spinner)getActivity().findViewById(R.id.ad)).setEnabled(!sortBy.equals("Default"));

                ArrayList<JSONObject> al = new ArrayList<>();

                JSONArray similarItems = null;
                try {
                    similarItems = ((ItemActivity)getActivity()).getSimilarObj().getJSONObject("getSimilarItemsResponse").getJSONObject("itemRecommendations").getJSONArray("item");

                    for (int i = 0; i < similarItems.length(); i++)
                    {
                        al.add(similarItems.getJSONObject(i));
                    }

                    if (sortBy.equals("Default"))
                        ((Spinner)getActivity().findViewById(R.id.ad)).setSelection(0);

                    if (sortBy.equals("Name"))
                        Collections.sort(al, nameComparator);

                    if (sortBy.equals("Price"))
                        Collections.sort(al, priceComparator);

                    if (sortBy.equals("Days"))
                        Collections.sort(al, daysComparator);

                    if (!sortBy.equals("Default") && ad.equals("Descending"))
                        Collections.reverse(al);

                    adapter.setData(new JSONArray(al.toString()));
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(sortBy + " " + ad);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        System.out.println(((ItemActivity)getActivity()).getSimilarObj());

        RecyclerView recyclerView = ((RecyclerView)getActivity().findViewById(R.id.similar_item_recycler));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setItemViewCacheSize(20);

        try {
            adapter = new SimilarItemsAdapter(getContext(), ((ItemActivity)getActivity()).getSimilarObj().getJSONObject("getSimilarItemsResponse").getJSONObject("itemRecommendations").getJSONArray("item"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(adapter);

        sortBy.setOnItemSelectedListener(sortListener);
        ad.setOnItemSelectedListener(sortListener);
    }

    protected void onSortChange()
    {
        System.out.println("Sort!");
    }
}
