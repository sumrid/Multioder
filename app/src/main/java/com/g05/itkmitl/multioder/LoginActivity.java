package com.g05.itkmitl.multioder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.g05.itkmitl.multioder.managerestaurant.OrderListActivity;
import com.g05.itkmitl.multioder.managerestaurant.RestaurantMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {


    private boolean doubleBackToExitPressedOnce = false;
    private Button btnLogin;
    private SharedPreferences shared;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#00000000"));
        }


        final EditText emailLogin = (EditText) findViewById(R.id.username);
        final EditText passwordLogin = (EditText) findViewById(R.id.password);
        final TextView register_link = (TextView) findViewById(R.id.register_link);
        final ImageView appLogo = (ImageView) findViewById(R.id.app_logo);
        btnLogin = (Button) findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);



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
                }

            }
        });
    }

    void signIn(String email, String password) {
        setLoading(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Fail : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                setLoading(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                loginState();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            btnLogin.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnLogin.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(LoginActivity.this, CheckLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "กดอีกครั้งเพื่อออก", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void loginState() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, CheckLoginActivity.class));
            finish();
        }
    }



    private boolean haveCurrentUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            return true;
        }
        return false;
    }

}
