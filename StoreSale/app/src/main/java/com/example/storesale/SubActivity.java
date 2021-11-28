package com.example.storesale;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SubActivity extends AppCompatActivity {
    FragmentManager fragmentManager; //프래그먼트 전환을 위한 매니저
    searchFragment searchfragment;
    mapFragment mapfragment;
    setFragment setfragment;
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        searchfragment=new searchFragment();
        setfragment=new setFragment();
        mapfragment=new mapFragment();
        fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container1, mapfragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnItemSelectedListener
                (new NavigationBarView.OnItemSelectedListener() {
                     @Override
                     public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                         switch (item.getItemId()) {
                             case R.id.tab_map:
                                 getSupportFragmentManager().beginTransaction().show(mapfragment).commit();
                                 if(setfragment!=null) getSupportFragmentManager().beginTransaction().hide(setfragment).commit();
                                 if(searchfragment!=null)getSupportFragmentManager().beginTransaction().hide(searchfragment).commit();

                             case R.id.tab_search:
                                 if (searchfragment==null) fragmentManager.beginTransaction().replace(R.id.container1, searchfragment).commit();

                                 if(setfragment!=null) getSupportFragmentManager().beginTransaction().hide(setfragment).commit();
                                 if(searchfragment!=null)getSupportFragmentManager().beginTransaction().hide(searchfragment).commit();
                                 return true;

                             case R.id.tab_setting:
                                 getSupportFragmentManager().beginTransaction().replace(R.id.container1, setfragment).commit();
                                 return true;
                         }
                         return false;
                     }
                 });
    }

    public void onBackPressed() {
        backKeyHandler.onBackPressed(2);
    }

    public void changeTab(int num) { //탭을 이동하는 메소드
        switch (num) {

        }
    }

}

