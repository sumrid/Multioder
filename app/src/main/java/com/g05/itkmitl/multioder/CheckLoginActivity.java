package com.g05.itkmitl.multioder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.g05.itkmitl.multioder.managerestaurant.RestaurantMainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class CheckLoginActivity extends AppCompatActivity {
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklogin);


        if (!haveCurrentUser()) {
            finish();
            Intent intent = new Intent(CheckLoginActivity.this, SelectLoginActivity.class);
            startActivity(intent);
        }

        final SharedPreferences shared = getSharedPreferences("sp_data", Context.MODE_PRIVATE);
        if(shared.getBoolean("isRestaurant",false)){
            startActivity(new Intent(CheckLoginActivity.this, RestaurantMainActivity.class));
        } else {
            startActivity(new Intent(CheckLoginActivity.this, MainActivity.class));
        }
    }

    private boolean haveCurrentUser() {
        if (mAuth.getCurrentUser() != null) {
            return true;
        }
        return false;
    }

}
