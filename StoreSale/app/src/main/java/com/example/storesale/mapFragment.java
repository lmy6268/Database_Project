package com.example.storesale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class mapFragment extends Fragment {
    EditText edtMtext;
    TextView tvMap;
    Button btnHi;
    public mapFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View i = inflater.inflate(R.layout.fragment_map, container, false);
        edtMtext= i.findViewById(R.id.edtMtext);
        tvMap= i.findViewById(R.id.tvMap);
        btnHi=i.findViewById(R.id.btnhi);
        btnHi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvMap.setText(edtMtext.getText().toString());
            }
        });

        return i;
    }
}