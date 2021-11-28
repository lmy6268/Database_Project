package com.example.storesale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements  loginFragment.OnloginSuccessListener{
    FragmentManager fragmentManager;
    loadingFragment loadingFragment;
    loginFragment loginFragment;
    signupFragment signupFragment;
    Runnable mTicker;
    Intent intent;
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);
//    @Override
//    public void onBackPressed() {
//        backKeyHandler.onBackPressed(2);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        Handler mHandler = new Handler(Looper.getMainLooper());
        loadingFragment=new loadingFragment();
        loginFragment=new loginFragment();
        signupFragment=new signupFragment();
        intent=new Intent(this,SubActivity.class);

        mTicker = new Runnable() {
            public void run() {
                onChange(1);
            }
        };
        mHandler.postDelayed(mTicker, 3000);
        //로딩화면 보여줌
        onChange(2);

    }

    public void onChange(int num) {
        switch (num) {
            case 1: //로그인 메뉴
                fragmentManager.beginTransaction().replace(R.id.container1, loginFragment).commitAllowingStateLoss();
                break;
            case 2: //로딩메뉴
                fragmentManager.beginTransaction().replace(R.id.container1, loadingFragment).commitAllowingStateLoss();
                break;
            case 3: //회원가입 메뉴
                fragmentManager.beginTransaction().replace(R.id.container1, signupFragment).addToBackStack(null).commitAllowingStateLoss();
                break;
        }
    }

    @Override
    public void loginSuccess(Boolean check) //로그인이 된 이후에만 액티비티 변경
    {
        if (check==true) {
            startActivity(intent); //인텐트를 통한 액티비티전환
            finish();
        }
    }
    public static String sha256(String str) //SHA256암호화를 위한 메소드
    {
        String SHA = "";
        try{
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for(int i = 0 ; i < byteData.length ; i++)
                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
            SHA = sb.toString();
        }catch(NoSuchAlgorithmException e) { e.printStackTrace(); SHA = null; }
        return SHA;
    }
}