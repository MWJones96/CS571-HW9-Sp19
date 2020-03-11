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

public class PhotoFragment extends Fragment {
    ItemPhotoAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (((ItemActivity)getActivity()).getPhotosObj() == null || ((ItemActivity)getActivity()).getPhotosObj().length() == 0)
        {
            getActivity().findViewById(R.id.no_photos).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.item_photos).setVisibility(View.GONE);
            return;
        }

        RecyclerView recyclerView = ((RecyclerView)getActivity().findViewById(R.id.item_photos));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setItemViewCacheSize(8);

        adapter = new ItemPhotoAdapter(getContext(), ((ItemActivity)getActivity()).getPhotosObj());
        recyclerView.setAdapter(adapter);
    }
}
