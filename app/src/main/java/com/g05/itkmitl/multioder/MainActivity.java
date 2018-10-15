package com.g05.itkmitl.multioder;

import android.arch.lifecycle.HolderFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.g05.itkmitl.multioder.food.FoodListFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.navigation_button);

        // check current user
        if(haveCurrentUser()){
            if(savedInstanceState == null) {
                changeFragment(new FoodListFragment());
            }
        } else {
            if(savedInstanceState == null) {
                navigationView.setVisibility(View.GONE);
                changeFragment(new LoginFragment());
            }
        }

        // onItemSelected
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast.makeText(getBaseContext(), item+"", Toast.LENGTH_SHORT).show();
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        changeFragment(new FoodListFragment());
                        break;
                    case R.id.navigation_cart:
                        changeFragment(new CartFragment()); break;
                    case R.id.navigation_account:
//                        changeFragment();
                        break;
                }
                return true;
            }
        });
    }

    private boolean haveCurrentUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            return true;
        }
        return false;
    }

    private void changeFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_view, fragment)
                .commit();
    }
}
