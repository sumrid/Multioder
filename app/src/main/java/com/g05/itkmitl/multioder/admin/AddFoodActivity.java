package com.g05.itkmitl.multioder.admin;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.food.Food;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddFoodActivity extends AppCompatActivity {
    private static final String TAG = "Add Food Activity";

    private ImageView foodImage;
    private EditText foodName;
    private EditText foodDescription;
    private EditText foodPrice;
    private Button foodAddButton;
    private ProgressBar progressBar;

    private Uri imageUri;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        foodImage = findViewById(R.id.add_food_image);
        foodName = findViewById(R.id.add_food_name);
        foodDescription = findViewById(R.id.add_food_description);
        foodPrice = findViewById(R.id.add_food_price);
        foodAddButton = findViewById(R.id.add_food_button);
        progressBar = findViewById(R.id.add_food_progressbar);

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
    }


    /*
     *    Image Upload
     *    Reference : https://codinginflow.com/tutorials/android/firebase-storage-upload-and-retrieve-images/part-3-image-upload
     */
    private void uploadImage() {

        if (imageUri != null) {
            final String fileName = System.currentTimeMillis() + "." + getFileExtension(imageUri);
            showLog("file name : " + fileName);

            StorageReference FolderRef = storage.getReference("FoodImages");  // << folder = "FoodImages"
            StorageReference fileRef = FolderRef.child(fileName); // << file name

            fileRef.putFile(imageUri) // upload image file
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            getImageUrl(fileName);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showLog("Upload Fail! " + e.toString());
                    showToast("Upload Fail! " + e.toString());
                    setLoading(false);
                }
            });
        } else {
            showToast("Select Image !!");
            setLoading(false);
        }
    }

    private void getImageUrl(String fileName) {
        StorageReference fileUrl = storage.getReference("FoodImages/" + fileName);
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
        Food food = new Food();
        food.setName(foodName.getText().toString());
        food.setDescription(foodDescription.getText().toString());
        food.setPrice(Double.parseDouble(foodPrice.getText().toString()));
        food.setUrl(imageUrl);

        firestore.collection("test")
                .document()
                .set(food)
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
        }
    }

    private void setLoading(boolean isLoading){
        if(isLoading) {
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
