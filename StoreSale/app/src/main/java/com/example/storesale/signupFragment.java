package com.example.storesale;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.util.Arrays;


public class signupFragment extends Fragment {
    private EditText edtSPass, edtSPassCheck, edtSemail, edtSid, edtSNickname;
    private Button btnSProceed;
    private Context context;

    public signupFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialize
        View i = inflater.inflate(R.layout.fragment_signup, container, false);
        edtSid = i.findViewById(R.id.edtSid);
        edtSPass = i.findViewById(R.id.edtSPass);
        edtSPassCheck = i.findViewById(R.id.edtSPassCheck);
        edtSemail = i.findViewById(R.id.edtSemail);
        edtSNickname = i.findViewById(R.id.edtSNickname);
        btnSProceed = i.findViewById(R.id.btnSProceed);
        context = container.getContext();
        //회원가입 진행 버튼을 클릭하였을 때.
        btnSProceed.setOnClickListener(v -> {
            Boolean checkEmpty[] = new Boolean[5];
            Arrays.fill(checkEmpty, false);
            String values[] = new String[5];
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
            proceedSignup(count, checkEmpty, values);
        });
        return i;
    }

    public void makeToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void checkAccount(String[] array) //회원가입이 가능한지 확인함.
    {
        System.out.println(Arrays.toString(array));
        //서버와의 통신
        //중복 체크
        //아이디 중복
        //이메일 중복
        //닉네임 중복

        //중복되는 항목이 없다면, 회원가입 진행
        //쿼리를 보내는 함수를 적용한다.
    }

    private void proceedSignup(int count, Boolean[] checkEmpty, String[] values) //회원가입 루틴
    {
        if (count == 0) {
            if (values[4].equals(values[3]))//비밀번호 확인과 같은 경우
            {
                values[3] = ((MainActivity) getActivity()).sha256(values[3]); //암호화 진행
                checkAccount(values);
                makeToast("회원가입 성공");
            } else {
                makeToast("비밀번호 확인이 일치하지 않습니다.");
            }
        }
        else if (count == 5) makeToast("정보를 입력하신 후 진행해 주세요.");
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
}