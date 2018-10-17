package com.g05.itkmitl.multioder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class TestActivityLogged  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_logged);

        if(haveCurrentUser()){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Log.d("USER", "YES");
            Log.d("USER_NAME",auth.getCurrentUser().getEmail().toString());

        }
        else Log.d("USER", "NO");

    }


    private boolean haveCurrentUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            return true;
        }
        return false;
    }
}
