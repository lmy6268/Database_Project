package com.example.storesale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

//회원정보를 보여주는 프레그먼트
public class setFragment extends Fragment {

    public setFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View i = inflater.inflate(R.layout.fragment_set, container, false);
//        i.findViewById(R.id.);
        return i;
    }
}