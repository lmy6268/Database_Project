package com.example.storesale;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.regex.Pattern;


public class signupFragment extends Fragment implements View.OnClickListener {
    private EditText edtSPass, edtSPassCheck, edtSemail, edtSid, edtSNickname;
    private Button[] btnArray;
    private int[] idArray = {R.id.btnIdcheck, R.id.btnEmailcheck, R.id.btnNNcheck, R.id.btnSProceed};
    private Context context;
    private Boolean error[] = new Boolean[idArray.length];

    public signupFragment() {
        Arrays.fill(error, false);
    }

    public void checkDup(int a,Boolean check) {
        if (check){
            if(!error[a]){
                error[a]=true;
            }
        }
        else if(!check){
            if(error[a]){
                error[a]=false;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialize
        View i = inflater.inflate(R.layout.fragment_signup, container, false);
        btnArray = new Button[idArray.length];


        edtSid = i.findViewById(R.id.edtSid);
        edtSPass = i.findViewById(R.id.edtSPass);
        edtSPassCheck = i.findViewById(R.id.edtSPassCheck);
        edtSemail = i.findViewById(R.id.edtSemail);
        edtSNickname = i.findViewById(R.id.edtSNickname);
        context = container.getContext();
        for (int j = 0; j < idArray.length; j++) {
            btnArray[j] = i.findViewById(idArray[j]);
            btnArray[j].setOnClickListener(this);

        }


        return i;
    }


    @Override
    public void onClick(View view) {
        Button clickedBtn = (Button) view; // 입력된 버튼 요소값
        for (int i = 0; i < btnArray.length; i++) {
            if (btnArray[i] == clickedBtn) {
                if (i == 3) {
                    try {
                        proceedSignup();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        checkAccount(i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }


        }
    }


    public void makeToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public void sendRequest(String URL, int num) throws IOException {

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                String[] keyArray = {"아이디", "이메일", "닉네임"};
                Bundle bun = msg.getData();
                String data = null;
                String key = "";
                int index =0;
                for ( int i=0;i<keyArray.length;i++){
                    if (bun.containsKey(keyArray[i])) {
                        data = bun.getString(keyArray[i]);
                        key = keyArray[i];
                        break;
                    }
                }

                if (data.equals("OK")) {
                    makeToast(String.format("사용가능한 %s입니다", key));
                    checkDup(index,true);
                } else {
                    checkDup(index,false);
                    makeToast(String.format("중복된 %s입니다", key));
                }
            }
        };
        new Thread() {
            public void run() {
                String key = null;
                switch (num) {
                    case 0:
                        key = "아이디";
                        break;
                    case 1:
                        key = "이메일";
                        break;
                    case 2:
                        key = "닉네임";
                        break;

                }
                ;
                DB db = new DB(URL, key, handler);
                db.connectDB();

            }
        }.start();
    }


    private void checkAccount(int num) throws IOException //회원가입이 가능한지 확인함.
    {


        //서버와의 통신
        //중복 체크
        String URL = "";

        if (num == 0) {
            if (edtSid.getText().toString().equals("")) {
                makeToast("아이디를 입력해주세요");
                return;
            } else
                URL = String.format("http://193.122.126.186:3000/duplicate?id_check=%s", edtSid.getText().toString());
        } else if (num == 1) {
            if (edtSemail.getText().toString().equals("")) {
                makeToast("이메일를 입력해주세요");
                return;
            } else
                URL = String.format("http://193.122.126.186:3000/duplicate?email_check=%s", edtSemail.getText().toString());
        } else if (num == 2) {
            if (edtSNickname.getText().toString().equals("")) {
                makeToast("닉네임를 입력해주세요");
                return;
            } else
                URL = String.format("http://193.122.126.186:3000/duplicate?nickname_check=%s", edtSNickname.getText().toString());
        }

        sendRequest(URL, num);

    }
//이메일 중복
//닉네임 중복

//중복되는 항목이 없다면, 회원가입 진행
//쿼리를 보내는 함수를 적용한다.

    private void proceedSignup() throws JSONException, IOException //회원가입 루틴
    {
        Boolean checkEmpty[] = new Boolean[5];
        Arrays.fill(checkEmpty, false);
        String values[] = new String[5];
        JSONObject jsonObject = new JSONObject();
        int count = 0;
        values[0] = edtSid.getText().toString();
        values[1] = edtSemail.getText().toString();
        values[2] = edtSNickname.getText().toString();
        values[3] = edtSPass.getText().toString();
        values[4] = edtSPassCheck.getText().toString();

        for (int i1 = 0; i1 < values.length; i1++) {
            if (values[i1].equals("")) {
                checkEmpty[i1] = true;
                count++;
            }
        }

        if (count == 0) {
            if (values[4].equals(values[3]))//비밀번호 확인과 같은 경우
            {
                Boolean c=false;

                for(int i=0;i<3;i++){
                    checkAccount(i);
                }

                for(Boolean i :error){
                    if (i){
                        c=true;
                        break;
                    }
                }


                if (!c){
                    System.out.println(c);
                values[3] = ((MainActivity) getActivity()).sha256(values[3]); //암호화 진행
                jsonObject.put("id", values[0]);
                jsonObject.put("em", values[1]);
                jsonObject.put("pw", values[3]);
                jsonObject.put("nn", values[2]);
                sendSignup(jsonObject.toString());}
                else if(c){
                    makeToast("중복된 값이 하나이상 있습니다.");
                }else{
                    makeToast("이메일 형식이 올바르지 않습니다.");
                }

            } else {
                makeToast("비밀번호 확인이 일치하지 않습니다.");
            }
        } else if (count == 5) makeToast("정보를 입력하신 후 진행해 주세요.");
        else {
            for (int i = 0; i < 5; i++) {
                if (checkEmpty[i]) {
                    switch (i) {
                        case 0:
                            makeToast("아이디를 입력해주세요");
                            break;
                        case 1:
                            makeToast("이메일 주소를 입력해주세요");
                            break;
                        case 2:
                            makeToast("닉네임을 입력해주세요");
                            break;
                        case 3:
                            makeToast("비밀번호를 입력해주세요");
                            break;
                        case 4:
                            makeToast("비밀번호 확인도 입력해주세요");
                            break;
                    }
                    break;
                }
            }
        }
    }

    //    실제 회원가입 쿼리를 보내는 공간
    private void sendSignup(String json) {
        Activity activity = (MainActivity) this.getActivity();
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                Bundle bun = msg.getData();
                String data = null;
                data = bun.getString("data");
                if (data.equals("OK")) {
                    makeToast("회원가입을 성공하였습니다");
                    ((MainActivity) getActivity()).onChange(1);
                } else makeToast("에러입니다");


            }
        };
        new Thread() {
            public void run() {
                    String l = "http://193.122.126.186:3000/signup"; //회원가입 루트
                    DB db=new DB("data",l,handler);
                    db.connectDB();
            }
        }.start();
    }
    public boolean isEmail(String str) //이메일인지 검사
    {
        return Pattern.matches("^[a-z0-9A-Z._-]*@[a-z0-9A-Z]*.[a-zA-Z.]*$", str);
    }
}