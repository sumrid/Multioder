package com.g05.itkmitl.multioder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.g05.itkmitl.multioder.managerestaurant.OrderListActivity;
import com.g05.itkmitl.multioder.managerestaurant.RestaurantMainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RestauLoginActivity extends AppCompatActivity {

    private SharedPreferences shared;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_restaurant);

        shared = getSharedPreferences("sp_data", Context.MODE_PRIVATE);

        final EditText emailLogin = (EditText) findViewById(R.id.res_email);
        final EditText passwordLogin = (EditText) findViewById(R.id.res_password);
        final Button btnLogin = (Button) findViewById(R.id.btn_login);



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userEmail = emailLogin.getText().toString();
                String userPass = passwordLogin.getText().toString();

                if (userEmail.isEmpty() || userPass.isEmpty()){
                    Toast.makeText(RestauLoginActivity.this, "Please enter your informations", Toast.LENGTH_SHORT).show();
                }else{
                    signIn(userEmail, userPass);
                    Toast.makeText(RestauLoginActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void signIn(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RestauLoginActivity.this, "Fail : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                loginState();
            }
        });
    }

    private void loginState() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            SharedPreferences.Editor editor = shared.edit();
            editor.putBoolean("isRestaurant", true);
            editor.commit();
            Intent intent = new Intent(RestauLoginActivity.this, CheckLoginActivity.class);
            startActivity(intent);
        }
    }
}
