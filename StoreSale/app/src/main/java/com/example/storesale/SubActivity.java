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
    nutritionFragment nutritionfragment;
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
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
                                 if (mapfragment==null) {
                                     mapfragment=new mapFragment();
                                     fragmentManager.beginTransaction().add(R.id.container1, mapfragment).commit();

                                 }
                                 if(mapfragment!=null)fragmentManager.beginTransaction().show(mapfragment).commit();
                                 if(setfragment!=null) fragmentManager.beginTransaction().hide(setfragment).commit();
                                 if(searchfragment!=null)fragmentManager.beginTransaction().hide(searchfragment).commit();
                                 return true;

                             case R.id.tab_search:
                                 if (searchfragment==null) {
                                     searchfragment=new searchFragment();
                                     fragmentManager.beginTransaction().add(R.id.container1, searchfragment).commit();

                                 }
                                 if(searchfragment!=null)fragmentManager.beginTransaction().show(searchfragment).commit();
                                 if(setfragment!=null)fragmentManager.beginTransaction().hide(setfragment).commit();
                                 if(mapfragment!=null) fragmentManager.beginTransaction().hide(mapfragment).commit();

                                 return true;

                             case R.id.tab_setting:
                                 if (setfragment==null) {
                                     setfragment=new setFragment();
                                     fragmentManager.beginTransaction().add(R.id.container1, setfragment).commit();
                                 }
                                 if(setfragment!=null)fragmentManager.beginTransaction().show(setfragment).commit();
                                 if(mapfragment!=null) fragmentManager.beginTransaction().hide(mapfragment).commit();
                                 if(searchfragment!=null)fragmentManager.beginTransaction().hide(searchfragment).commit();
                                 return true;
                         }
                         return false;
                     }
                 });
    }

    public void onBackPressed() {
        backKeyHandler.onBackPressed(2);
    }

    public void changeFrag(Product item) { //탭을 이동하는 메소드
        Bundle bundle = new Bundle();//번들을 통해 id값 전달해야 함
        bundle.putSerializable("item",item);//번들에 넘길 값 저장
        nutritionfragment=new nutritionFragment();
        nutritionfragment.setArguments(bundle);
        fragmentManager.beginTransaction().add(R.id.container1, nutritionfragment).commit();
        fragmentManager.beginTransaction().show(nutritionfragment).commit();
        fragmentManager.beginTransaction().hide(searchfragment).commit();
    }
    public void backFrag(){
        fragmentManager.beginTransaction().hide(nutritionfragment).commit();
        fragmentManager.beginTransaction().remove(nutritionfragment).commit();
        fragmentManager.beginTransaction().show(searchfragment).commit();
    }

}

