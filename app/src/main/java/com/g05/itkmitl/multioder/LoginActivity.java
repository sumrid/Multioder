package com.g05.itkmitl.multioder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        } else {
            loginState();
        }




        final EditText emailLogin = (EditText) findViewById(R.id.username);
        final EditText passwordLogin = (EditText) findViewById(R.id.password);
        final Button btnLogin = (Button) findViewById(R.id.button_login);
        final TextView register_link = (TextView) findViewById(R.id.register_link);
        final ImageView appLogo = (ImageView) findViewById(R.id.app_logo);



        Animation fromTop = AnimationUtils.loadAnimation(this, R.anim.fromtopdown);
        Animation fromDown = AnimationUtils.loadAnimation(this, R.anim.fromdownup);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);


        emailLogin.setAnimation(fadeIn);
        passwordLogin.setAnimation(fadeIn);
        appLogo.setAnimation(fromTop);
        btnLogin.setAnimation(fromDown);
        register_link.setAnimation(fadeIn);

        register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userEmail = emailLogin.getText().toString();
                String userPass = passwordLogin.getText().toString();

                if (userEmail.isEmpty() || userPass.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter your informations", Toast.LENGTH_SHORT).show();
                }else{
                    signIn(userEmail, userPass);
                    Toast.makeText(LoginActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    void signIn(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Fail : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                loginState();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void loginState() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }


}
