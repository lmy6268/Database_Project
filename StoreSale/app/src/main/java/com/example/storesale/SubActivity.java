package com.example.storesale;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SubActivity extends AppCompatActivity {
    FragmentManager fragmentManager; //프래그먼트 전환을 위한 매니저
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

    }
    public void onBackPressed() {
        backKeyHandler.onBackPressed(2);
    }
    public void changeTab(int num) {
        switch (num) {

        }
    }

}

