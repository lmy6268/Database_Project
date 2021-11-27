package com.example.storesale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public class mapFragment extends Fragment {

    public mapFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View i = inflater.inflate(R.layout.fragment_loading, container, false);
//        i.findViewById(R.id.);
        return i;
    }
}