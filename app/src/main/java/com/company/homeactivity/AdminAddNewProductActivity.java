package com.company.homeactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.company.phpnew.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, description, price, prod_name, saveCurrentDate, saveCurrentTime, productRandomKey;
    private String downloadImageUrl;
    private ImageView product_pic;
    private EditText product_name, product_description, product_price;
    private Button add_new_product;
    private static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private StorageReference productImageRef;
    private DatabaseReference ProductRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_add_new_product);

        categoryName = getIntent().getExtras().get("category").toString();

        Toast.makeText(this, "Выбрана категория " + categoryName, Toast.LENGTH_SHORT).show();

        init();
        OnClick();

    }

    public void init() {
        product_pic = findViewById(R.id.photo);
        product_name = findViewById(R.id.name_of_product);
        product_description = findViewById(R.id.description);
        product_price = findViewById(R.id.price);
        add_new_product = findViewById(R.id.add_product);
        productImageRef = FirebaseStorage.getInstance().getReference().child("ProductImages");
        ProductRef=FirebaseDatabase.getInstance().getReference().child("Products");
        loadingBar=new ProgressDialog(this);
    }

    public void OnClick() {
        product_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        add_new_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckProductData();
            }
        });
    }

    private void OpenGallery() {

        Intent gallery_intent = new Intent();
        gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
        gallery_intent.setType("image/*");
        startActivityForResult(gallery_intent, Gallery_Pick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            product_pic.setImageURI(ImageUri);
        }
    }

    private void CheckProductData() {
        prod_name = product_name.getText().toString();
        description = product_description.getText().toString();
        price = product_price.getText().toString();

        if (ImageUri == null) {
            Toast.makeText(this, "Добавьте картинку", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(prod_name)) {
            Toast.makeText(this, "Добавьте название", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Добавьте описание", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Добавьте цену", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInfo();
        }
    }

    public void StoreProductInfo() {

        loadingBar.setTitle("Загрузка картинки");
        loadingBar.setMessage("Пожалуйста подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = productImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".WebP");

        final UploadTask uploadTask = filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Изображение успешно загружено", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();

                        } else {
                            downloadImageUrl = filePath.getDownloadUrl().toString();
                            return filePath.getDownloadUrl();
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminAddNewProductActivity.this, "Фото сохранено", Toast.LENGTH_SHORT).show();


                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });



    }
    public void SaveProductInfoToDatabase(){
        HashMap<String,Object> productMap = new HashMap<>();

        productMap.put("productid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",description);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",categoryName);
        productMap.put("price",price);
        productMap.put("name",prod_name);

        ProductRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Информация о товаре добавлена", Toast.LENGTH_SHORT).show();
                    Intent cat_intent=new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);
                    startActivity(cat_intent);
                }
                else{
                    String mess =task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Ошибка: "+mess, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });

    }
}
