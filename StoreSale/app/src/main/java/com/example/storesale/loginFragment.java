package com.example.storesale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class loginFragment extends Fragment {
    private Button btnSignup, btnLogin;//회원가입 버튼, 로그인 버튼
    private EditText edtLid, edtLpass; //아이디,비밀번호 입력
    private Activity activity;

    public interface OnloginSuccessListener //로그인이 되면, 화면을 전환함.
    {
        public void loginSuccess(Boolean check);
    }
    public loginFragment(){};//생성자
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            this.activity = (Activity)context;
        }
    }
    //문자열 필터링
    protected InputFilter filter= new InputFilter() {

        public CharSequence filter(CharSequence source, int start, int end,

                                   Spanned dest, int dstart, int dend) {



            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");

            if (!ps.matcher(source).matches()) {

                return "";

            }
            return null;

        }

    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View i = inflater.inflate(R.layout.fragment_login, container, false); //프레그먼트의 각 요소를 사용하기 위해 View 클래스로 담아줌.
        //initialize
        btnSignup = i.findViewById(R.id.btnSignup);
        btnLogin = i.findViewById(R.id.btnLogin);
        edtLid = i.findViewById(R.id.edtLid);
        edtLpass = i.findViewById(R.id.edtLpass);
        Context context = container.getContext();

        //아이디 비밀번호를 입력한 상태에서만 로그인 버튼을 누를 수 있도록 함.
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String iD = edtLid.getText().toString(); //입력받은 아이디값
                String pW = edtLpass.getText().toString(); //입력받은 비밀번호값
                if (pW.equals("") && iD.equals("")) {
                    Toast.makeText(context, "아이디와 비밀번호를 입력하여주세요", Toast.LENGTH_SHORT).show(); // 둘다 비었을 경우
                } else if (iD.equals(""))
                    Toast.makeText(context, "아이디를 입력하여주세요", Toast.LENGTH_SHORT).show(); // 아이디 필드가 비었을 경우
                else if (pW.equals(""))
                    Toast.makeText(context, "비밀번호를 입력하여주세요", Toast.LENGTH_SHORT).show(); // 비밀번호 필드가 비었을 경우
                else // 비어있는 필드가 없을 경우 => 로그인 수행
                {
                    String pass=((MainActivity) getActivity()).sha256(edtLpass.getText().toString());
                    JSONObject jsonObject=new JSONObject();
                    try {
                        Log.d("id",edtLid.getText().toString());
                        jsonObject.put("id",edtLid.getText().toString() );
                        jsonObject.put("pass", pass);
                        Handler handler = new Handler() {
                            public void handleMessage(Message msg) {

                                Bundle bun = msg.getData();
                                String data = bun.getString("login");

                                if (data.equals("OK")) {
                                    Toast.makeText(getContext(),"로그인을 성공하였습니다",Toast.LENGTH_SHORT).show();
                                    edtLid.setText("");
                                    edtLpass.setText("");
                                    ((MainActivity) getActivity()).loginSuccess(true);
                                } else {
                                    Toast.makeText(getContext(),"다시 시도해주세요",Toast.LENGTH_SHORT).show();
                                }
                            }
                        };
                        new Thread() {
                            public void run() {
                                String key = "login";
                                String where="http://193.122.126.186:3000/login";
                                DB db = new DB(where, key, handler);
                                Log.d("data",jsonObject.toString());
                                db.connectDB(jsonObject.toString(),"POST");
                            }
                        }.start();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //계정정보 저장 버튼도 구현하는게 좋겠다.
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).onChange(3);
            }
        });

        return i;
    }
}