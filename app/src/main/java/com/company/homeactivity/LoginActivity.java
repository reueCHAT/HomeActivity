package com.company.homeactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.company.phpnew.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import io.paperdb.Paper;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText phone,pass;
    private ProgressDialog loadingBar;
    private TextView adminlink,clientlink;

    private String parentDataBaseName="Users";

    private CheckBox checkBox_Remember_me;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        init();
        loginBut();

    }
    public void init(){
        loginButton =findViewById(R.id.button);
        phone=findViewById(R.id.Phone);
        pass=findViewById(R.id.Pass);
        checkBox_Remember_me= (CheckBox) findViewById(R.id.checkbox);
        adminlink=findViewById(R.id.adminlink);
        clientlink=findViewById(R.id.userlink);

        clientlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminlink.setVisibility(View.VISIBLE);
                clientlink.setVisibility(View.INVISIBLE);
                loginButton.setText("Вход");
                parentDataBaseName="Users";
            }
        });

        adminlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminlink.setVisibility(View.INVISIBLE);
                clientlink.setVisibility(View.VISIBLE);
                loginButton.setText("Вход для админа");
                parentDataBaseName="Admins";
            }
        });

        Paper.init(this);

        loadingBar=new ProgressDialog(this);


    }











    public void loginBut(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String phoneinput=phone.getText().toString();
        String passinput=pass.getText().toString();


        if(TextUtils.isEmpty(phoneinput))
        {
            Toast.makeText(this,"Введите Номер телефона",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(passinput))
        {
            Toast.makeText(this,"Введите пароль",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Вход в аккаунт");
            loadingBar.setMessage("Пожалуйста подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Check_User(phoneinput,passinput);
        }
    }

    private void Check_User(String phoneinput,String passinput) {

        Log.d(TAG,"Переменная номера"+phoneinput);
        Log.d(TAG,"Переменная пароля"+passinput);


        if(checkBox_Remember_me.isChecked()){
            Paper.book().write(CreateUser.User_Phone_Key,phoneinput);
            Paper.book().write(CreateUser.User_Pass_Key,passinput);
        }

        final DatabaseReference RootRef;
        RootRef=FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(parentDataBaseName).child(phoneinput).exists()){

                    Users userData=snapshot.child(parentDataBaseName).child(phoneinput).getValue(Users.class);
                    CreateUser.currentAccount=userData;

                    Log.d(TAG,"Переменная номера из базы данных! "+CreateUser.currentAccount.getPhone());
                    Log.d(TAG,"Переменная пароля из бд! "+CreateUser.currentAccount.getPass());
                    Log.d(TAG,"Переменная имени из бд! "+CreateUser.currentAccount.getName());



                    if(userData.getPhone().equals(phoneinput)){

                        if(userData.getPass().equals(passinput)){

                            if(parentDataBaseName.equals("Users")){
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this,"Успешный вход",Toast.LENGTH_SHORT).show();

                                Intent home_intent=new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(home_intent);
                            }
                            else if(parentDataBaseName.equals("Admins")){
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this,"Успешный вход",Toast.LENGTH_SHORT).show();

                                Intent home_intent=new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(home_intent);
                            }

                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this,"Неверный пароль",Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else{

                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this,"Аккаунт с номером "+phoneinput+" не найден",Toast.LENGTH_SHORT).show();

                    Intent register_intent=new Intent(LoginActivity.this,RegistActivity.class);
                    startActivity(register_intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}