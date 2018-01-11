package com.ashishlakhmani.dit_sphere.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;
import com.ashishlakhmani.dit_sphere.adapters.SyllabusDownloadAdapter;


public class Syllabus extends Fragment {

    private RecyclerView recyclerView;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 144;

    public Syllabus() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_syllabus, container, false);
        ((HomeActivity) getActivity()).setToolbarTitle("Syllabus");


        recyclerView = view.findViewById(R.id.syllabus_download_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        SyllabusDownloadAdapter adapter = new SyllabusDownloadAdapter(getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }

}
