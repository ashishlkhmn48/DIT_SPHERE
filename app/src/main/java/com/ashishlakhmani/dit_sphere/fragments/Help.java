package com.ashishlakhmani.dit_sphere.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;

public class Help extends Fragment {


    public Help() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        try {
            ((HomeActivity) getActivity()).setToolbarTitle("Help");
        }catch (ClassCastException e){
            getActivity().setTitle("Help");
        }

        return view;
    }

}
