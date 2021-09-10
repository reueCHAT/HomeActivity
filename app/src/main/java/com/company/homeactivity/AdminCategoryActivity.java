package com.company.homeactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.company.phpnew.R;

public class AdminCategoryActivity extends AppCompatActivity {
    private ImageView scalp,syringe,micro,clamp,bandage,surgery_technics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_category);
        init();
        onClickIcon();
    }
    public void init(){
        scalp=findViewById(R.id.scalpel);
        syringe=findViewById(R.id.syringe);
        micro=findViewById(R.id.micro);
        clamp=findViewById(R.id.clamp);
        bandage=findViewById(R.id.bandages);
        surgery_technics=findViewById(R.id.surgery_technics);

    }
    public void onClickIcon(){
        scalp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","scalp");
                startActivity(intent);
            }
        });
        syringe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","syringe");
                startActivity(intent);
            }
        });
        micro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","micro");
                startActivity(intent);
            }
        });
        clamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","clamp");
                startActivity(intent);
            }
        });
        bandage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","bandage");
                startActivity(intent);
            }
        });
        surgery_technics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","surgery_technics");
                startActivity(intent);
            }
        });

    }
}