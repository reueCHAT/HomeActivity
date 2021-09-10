package com.company.homeactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.company.phpnew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegistActivity extends AppCompatActivity {

    private Button registrationButton;
    private EditText name,phone,pass;
    private ProgressDialog loadingBar;
    private DatabaseReference RootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_regist);
        init();
        ButtonListener();
    }
    public void init(){
        registrationButton=findViewById(R.id.button_registration);
        name=findViewById(R.id.Name);
        phone=findViewById(R.id.PhoneRegistration);
        pass=findViewById(R.id.PassRegistration);
        loadingBar=new ProgressDialog(this);
        RootRef = FirebaseDatabase.getInstance().getReference();

    }
    public void ButtonListener(){

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String username=name.getText().toString();
        String phoneinput=phone.getText().toString();
        String passinput=pass.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Введите имя",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneinput))
        {
            Toast.makeText(this,"Введите Номер телефона",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(passinput))
        {
            Toast.makeText(this,"Введите пароль",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Создание аккаунта");
            loadingBar.setMessage("Пожалуйста подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            CheckData(username,phoneinput,passinput);
        }


    }
    private void CheckData(String username,String phoneinput,String passinput){



        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child("Users").child(phoneinput).exists())){

                    HashMap<String,Object> userdataMap= new HashMap<>();
                    userdataMap.put("phone",phoneinput);
                    userdataMap.put("name",username);
                    userdataMap.put("pass",passinput);

                    RootRef.child("Users").child(phoneinput).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                loadingBar.dismiss();

                                Toast.makeText(RegistActivity.this,"Регистрация прошла успешно",Toast.LENGTH_SHORT).show();

                                Intent login_intent=new Intent(RegistActivity.this,LoginActivity.class);
                                startActivity(login_intent);
                            }
                            else{

                                loadingBar.dismiss();

                                Toast.makeText(RegistActivity.this,"Ошибка,попробуйте позже",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
                else {
                    loadingBar.dismiss();
                    Toast.makeText(RegistActivity.this,"Номер "+phoneinput + "уже зарегестрирован",Toast.LENGTH_SHORT).show();

                    Intent login_intent=new Intent(RegistActivity.this,LoginActivity.class);
                    startActivity(login_intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}