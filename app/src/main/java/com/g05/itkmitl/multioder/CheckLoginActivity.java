package com.g05.itkmitl.multioder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.g05.itkmitl.multioder.managerestaurant.RestaurantMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CheckLoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklogin);

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final SharedPreferences shared = getSharedPreferences("user_data", Context.MODE_PRIVATE);

        //        !shared.getString("uid",null).equals(mAuth.getCurrentUser().getUid())
        if (!haveCurrentUser()) {
            shared.edit().clear().commit();
            mAuth.signOut();
            Intent intent = new Intent(CheckLoginActivity.this, SelectLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            firestore.collection("restaurant").document(mAuth.getCurrentUser().getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    shared.edit().putBoolean("isRestaurant", true).commit();
                                    startActivity(new Intent(CheckLoginActivity.this, RestaurantMainActivity.class));
                                } else {
                                    shared.edit().putBoolean("isRestaurant", false).commit();
                                    startActivity(new Intent(CheckLoginActivity.this, MainActivity.class));
                                }
                            } else {

                            }

                        }
                    });

        }

    }

    private boolean haveCurrentUser() {
        if (mAuth.getCurrentUser() != null) {
            return true;
        }
        return false;
    }

}
