package com.g05.itkmitl.multioder.admin;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.restaurant.Restaurant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddRestaurantActivity extends AppCompatActivity {
    private static final String TAG = "Add Restaurant Activity";

    private ImageView resImage;
    private EditText resName, resAddress, resPhone;
    private Button resAddButton;
    private ProgressBar resProgress;

    private Uri imageUri;

    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        resImage = findViewById(R.id.add_res_image);
        resName = findViewById(R.id.add_res_name);
        resAddress = findViewById(R.id.add_res_address);
        resPhone = findViewById(R.id.add_res_telephone);
        resAddButton = findViewById(R.id.add_res_button);
        resProgress = findViewById(R.id.add_res_progress);

        resImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectImageWindow();
            }
        });
        resAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputFill()) {
                    setLoading(true);
                    uploadImage();
                }
            }
        });
    }



    /*
    *   Upload and save data to firebase
    */
    private void uploadImage() {
        if (imageUri != null) {
            final String resID = "res_" + System.currentTimeMillis();      // <<<< "res_11681..."
            final String fileName = "mainImage." + getFileExtension(imageUri);

            StorageReference FolderRef = storage.getReference("RestaurantImages/" + resID);  // << folder
            StorageReference fileRef = FolderRef.child(fileName); // << file name

            fileRef.putFile(imageUri) // upload image file
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String path = "RestaurantImages/" + resID + "/" + fileName;
                            getImageUrl(path, resID);
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

    private void getImageUrl(String path, final String id) {
        StorageReference fileUrl = storage.getReference(path);
        fileUrl.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        addToFirebase(uri.toString(), id);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showLog("get Url Fail! " + e.toString());
                setLoading(false);
            }
        });
    }

    private void addToFirebase(String url, String id) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setName(resName.getText().toString());
        restaurant.setUrl(url);
        restaurant.setAddress(resAddress.getText().toString());
        restaurant.setTelephone(resPhone.getText().toString());

        firestore.collection("restaurant")
                .document(id)
                .set(restaurant)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setLoading(false);
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
            Picasso.get().load(data.getData()).into(resImage);
        }
    }

    private boolean checkInputFill(){
        if(resName.getText().toString().isEmpty()) {
            resName.setError("กรอกข้อมูล");
            return false;
        } else if (resAddress.getText().toString().isEmpty()) {
            resAddress.setError("กรอกข้อมูล");
            return false;
        } else if(resPhone.getText().toString().isEmpty()){
            resPhone.setError("กรอกข้อมูล");
            return false;
        } else {
            return true;
        }
    }
    // =====================================================

    private Restaurant getBundle() {
        Restaurant restaurant = (Restaurant) getIntent().getSerializableExtra("res");
        return restaurant;
    }

    private void setLoading(boolean isLoading){
        if(isLoading) {
            resAddButton.setVisibility(View.GONE);
            resProgress.setVisibility(View.VISIBLE);
        } else {
            resAddButton.setVisibility(View.VISIBLE);
            resProgress.setVisibility(View.GONE);
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void showLog(String text) {
        Log.d(TAG, text);
    }
}
