package com.company.homeactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import com.company.phpnew.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private Button login_button,register_button;
    private DatabaseReference RootRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Paper.init(this);
        init();

    }
    public void init(){
        register_button=findViewById(R.id.button);
        login_button=findViewById(R.id.button2);

        loadingBar=new ProgressDialog(this);


        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(login_intent);
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registration_intent=new Intent(MainActivity.this,RegistActivity.class);
                startActivity(registration_intent);
            }
        });

        String UserPhoneKey=Paper.book().read(CreateUser.User_Phone_Key);
        String UserPassKey=Paper.book().read(CreateUser.User_Pass_Key);

        if(UserPhoneKey !="" && UserPassKey !=""){
            if(!TextUtils.isEmpty(UserPhoneKey)&& !TextUtils.isEmpty(UserPassKey)){
                AutoLog(UserPhoneKey,UserPassKey);

                loadingBar.setTitle("Вход в аккаунт");
                loadingBar.setMessage("Пожалуйста подождите...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

            }
        }


    }

    private void AutoLog(String phoneinput, String passinput) {

        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(phoneinput).exists()){

                    Users userData=snapshot.child("Users").child(phoneinput).getValue(Users.class);

                    Log.d(TAG,"Переменная номера из базы данных"+userData.getPhone());
                    Log.d(TAG,"Переменная пароля из бд"+userData.getPass());

                    if(userData.getPhone().equals(phoneinput)){

                        if(userData.getPass().equals(passinput)){

                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this,"Успешный вход",Toast.LENGTH_SHORT).show();

                            Intent home_intent=new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(home_intent);

                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this,"Неверный пароль",Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else{

                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this,"Аккаунт с номером "+phoneinput+" не найден",Toast.LENGTH_SHORT).show();

                    Intent register_intent=new Intent(MainActivity.this,RegistActivity.class);
                    startActivity(register_intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}