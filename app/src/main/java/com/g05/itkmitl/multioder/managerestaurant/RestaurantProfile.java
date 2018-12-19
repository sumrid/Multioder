package com.g05.itkmitl.multioder.managerestaurant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.User;
import com.g05.itkmitl.multioder.restaurant.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class RestaurantProfile extends AppCompatActivity {
    private SharedPreferences shared;

    String nameStr,urlStr;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore mFirestore;
    private ImageView restaImg;
    private Uri profileImageUri;
    private EditText pro_name;
    private EditText pro_phone;
    Restaurant cur;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile_restarant);

        shared = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String currentLogin = shared.getString("current_user",null);
        mAuth = FirebaseAuth.getInstance();


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        pro_name = (EditText) findViewById(R.id.profile_name);
        pro_phone = (EditText) findViewById(R.id.profile_phone);


        restaImg = findViewById(R.id.resta_image);



        if(currentLogin==null){
            getUserData();
        } else  {
            Gson gson = new Gson();
            cur = gson.fromJson(currentLogin, Restaurant.class);
            pro_name.setText(cur.getName());
            pro_phone.setText(cur.getTelephone());
            nameStr = cur.getName();
            urlStr = cur.getUrl();
            Picasso.get().load(cur.getUrl()).fit().centerCrop().into(restaImg);

        }

        final LinearLayout lnk_chagepass =  findViewById(R.id.chgpass_link);
        lnk_chagepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassChangeDialog();

            }
        });


        final Button btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFirestore = FirebaseFirestore.getInstance();
                final String phoneStr = pro_phone.getText().toString();

                if (phoneStr.isEmpty()){
                    Toast.makeText(getApplicationContext(),"กรอกข้อมูลให้ครบ", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Working...", Toast.LENGTH_LONG).show();
                    Restaurant resta = new Restaurant(nameStr, urlStr, mAuth.getUid(), phoneStr);
                    mFirestore.collection("restaurant").document(mAuth.getCurrentUser().getUid())
                            .set(resta).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                cur.setTelephone(phoneStr);
                                Gson gson = new Gson();
                                String json = gson.toJson(cur);
                                shared.edit().putString("current_user", json).commit();
                                Toast.makeText(getApplicationContext(),"Saved Changes", Toast.LENGTH_LONG).show();
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });


    }



    private void showPassChangeDialog(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.confirm_pass_dialogbox, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText oldPassText = (EditText) mView.findViewById(R.id.inp_oldpass);
        final EditText newPassText = (EditText) mView.findViewById(R.id.inp_newpass);
        final EditText newRePassText = (EditText) mView.findViewById(R.id.inp_re_newpass);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                    }
                })
                .setNegativeButton("ยกเลิก",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });


        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

        alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(newPassText.length() < 6) {
                    Toast.makeText(getApplicationContext(), "รหัสผ่านใหม่ต้องยาวมากกว่า 5 ตัวอักษร", Toast.LENGTH_SHORT).show();
                    return;

                }else if (!(newPassText.getText().toString()).equals(newRePassText.getText().toString())){
                    Toast.makeText(getApplicationContext(), "รหัสผ่านไม่ตรงกัน", Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    String oldPass = oldPassText.getText().toString();
                    final String newPass = newPassText.getText().toString();


                    mAuth = FirebaseAuth.getInstance();

                    // ChangePass Process
                    firebaseUser = mAuth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(firebaseUser.getEmail(), oldPass);
                    firebaseUser.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("PassResult", "Password updated");
                                        Toast.makeText(getApplicationContext(), "เปลี่ยนรหัสผ่านเรียบร้อย", Toast.LENGTH_SHORT).show();
                                        alertDialogAndroid.dismiss();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "เปลี่ยนรหัสผ่านไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                                        Log.d("PassResult", "Password update Error!!");
                                        alertDialogAndroid.dismiss();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"กรอกรหัสผ่านเก่าไม่ถูกต้อง", Toast.LENGTH_LONG).show();
                            alertDialogAndroid.dismiss();
                        }
                    });

                }
            }
        });
    }


    private void getUserData() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("restaurant").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    cur = new Restaurant(documentSnapshot.getString("name"),
                            documentSnapshot.getString("url"), mAuth.getCurrentUser().getUid(),
                            documentSnapshot.getString("telephone"));
                    Gson gson = new Gson();
                    String json = gson.toJson(cur);
                    shared.edit().putString("current_user", json).commit();
                    Picasso.get().load(cur.getUrl()).fit().centerCrop().into(restaImg);
                }

            }
        });

    }


}
