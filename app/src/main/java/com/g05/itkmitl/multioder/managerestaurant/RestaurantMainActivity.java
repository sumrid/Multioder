package com.g05.itkmitl.multioder.managerestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.g05.itkmitl.multioder.LoginActivity;
import com.g05.itkmitl.multioder.MainActivity;
import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.SelectLoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class RestaurantMainActivity  extends AppCompatActivity {
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private TextView mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);

        mTitle = (TextView) findViewById(R.id.resta_name);
        mTitle.setText("ชื่อร้าน");

        final Button btnLogout = (Button) findViewById(R.id.button_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getCurrentUser();
                mAuth.signOut();

                final SharedPreferences shared = getSharedPreferences("sp_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putBoolean("isRestaurant", false);
                editor.commit();

                Intent intent = new Intent(RestaurantMainActivity.this, SelectLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

    }




}
