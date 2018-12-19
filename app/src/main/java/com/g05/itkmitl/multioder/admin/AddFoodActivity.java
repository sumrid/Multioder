package com.g05.itkmitl.multioder.admin;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.food.Food;
import com.g05.itkmitl.multioder.restaurant.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class AddFoodActivity extends AppCompatActivity {
    private static final String TAG = "Add Food Activity";

    private Food mFood;

    private ImageView foodImage;
    private EditText foodName;
    private EditText foodDescription;
    private EditText foodPrice;
    private Button foodAddButton;
    private ProgressBar progressBar;

    private Restaurant current;
    private String currentLogin;
    private SharedPreferences shared;

    private Uri imageUri;
    private boolean haveImage = false;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood);

        foodImage = findViewById(R.id.food_image);
        foodName = findViewById(R.id.res_name);
        foodDescription = findViewById(R.id.res_telephone);
        foodPrice = findViewById(R.id.food_item_price);
        foodAddButton = findViewById(R.id.food_add_button);
        progressBar = findViewById(R.id.progressBar);

        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectImageWindow();
            }
        });
        foodAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoading(true);
                uploadImage();
            }
        });

        final Toolbar toolbar = (Toolbar) findViewById(R.id.orderlist_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        if (getFoodBundle() != null) {
            showLog("start activity for edit food");

            mFood = getFoodBundle();
            setDisplayFood(mFood);

            haveImage = true;
        } else {
            showLog("start activity for add new food");

            String id = "food_" + System.currentTimeMillis();
            mFood = new Food();
            mFood.setUid(id);

            shared = getSharedPreferences("user_data", Context.MODE_PRIVATE);
            currentLogin = shared.getString("current_user", null);

            if (currentLogin == null) {
                getUserData();

            } else {

                Gson gson = new Gson();
                current = gson.fromJson(currentLogin, Restaurant.class);

            }

            mFood.setRestaurantID(current.getId());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUserData() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("restaurant").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();


                    Restaurant cur = new Restaurant(documentSnapshot.getString("name"),
                            documentSnapshot.getString("url"), mAuth.getCurrentUser().getUid(),
                            documentSnapshot.getString("telephone"));

                    current = cur;
                    Gson gson = new Gson();
                    String json = gson.toJson(cur);
                    shared.edit().putString("current_user", json).commit();

                }
            }
        });

    }


    /*
     *    Image Upload
     *    Reference : https://codinginflow.com/tutorials/android/firebase-storage-upload-and-retrieve-images/part-3-image-upload
     */
    private void uploadImage() {

        if (imageUri != null) {
            StorageReference FolderRef = storage.getReference("food_images/");  // << folder
            StorageReference fileRef = FolderRef.child(mFood.getUid() +"." + getFileExtension(imageUri));     // << file name

            fileRef.putFile(imageUri) // upload image file
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            getImageUrl(mFood.getUid());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showLog("Upload Fail! " + e.toString());
                    showToast("Upload Fail! " + e.toString());
                    setLoading(false);
                }
            });
        } else if (haveImage){
            saveFood(mFood.getUrl());
        } else {
            showToast("Select Image !!");
            setLoading(false);
        }
    }

    private void getImageUrl(String uid) {
        StorageReference fileUrl = storage.getReference("food_images/" + uid + "." + getFileExtension(imageUri));
        fileUrl.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        saveFood(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showLog("get Url Fail! " + e.toString());
                setLoading(false);
            }
        });
    }

    private void saveFood(String imageUrl) {
        mFood.setName(foodName.getText().toString());
        mFood.setDescription(foodDescription.getText().toString());
        mFood.setPrice(Double.parseDouble(foodPrice.getText().toString()));
        mFood.setUrl(imageUrl);

        firestore.collection("restaurant")
                .document(mFood.getRestaurantID())
                .collection("food")
                .document(mFood.getUid())
                .set(mFood)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showLog("Finish Activity");
                        finish();
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openSelectImageWindow() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(data.getData()).into(foodImage);
            haveImage = false;
        }
    }

    private Food getFoodBundle() {
        Food food = (Food) getIntent().getSerializableExtra("food");
        return food;
    }

    private void setDisplayFood(Food food) {
        Picasso.get().load(food.getUrl()).fit().centerCrop().into(foodImage);
        foodName.setText(food.getName());
        foodDescription.setText(food.getDescription());
        foodPrice.setText(food.getPrice() + "");
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            foodAddButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            foodAddButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void showLog(String text) {
        Log.d(TAG, text);
    }
}
