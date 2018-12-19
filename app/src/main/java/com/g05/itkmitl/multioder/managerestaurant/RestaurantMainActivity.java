package com.g05.itkmitl.multioder.managerestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.g05.itkmitl.multioder.LoginActivity;
import com.g05.itkmitl.multioder.MainActivity;
import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.SelectLoginActivity;
import com.g05.itkmitl.multioder.User;
import com.g05.itkmitl.multioder.admin.EditFoodListActivity;
import com.g05.itkmitl.multioder.restaurant.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class RestaurantMainActivity  extends AppCompatActivity {
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private SharedPreferences shared;
    private TextView mTitle;
    private ImageView resImage;
    private TextView orderCount;
    private TextView foodCount;
    Restaurant cur;

    public String getUserImgUrl() {
        return userImgUrl;
    }

    private String userImgUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);

        mTitle = (TextView) findViewById(R.id.resta_name);
        resImage = findViewById(R.id.resta_image);
        shared = getSharedPreferences("user_data", Context.MODE_PRIVATE);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.resta_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        String currentLogin = shared.getString("current_user",null);

        if(currentLogin==null){
            getUserData();
        } else  {
            Gson gson = new Gson();
            cur = gson.fromJson(currentLogin, Restaurant.class);
            Picasso.get().load(cur.getUrl()).fit().centerCrop().into(resImage);
            mTitle.setText(cur.getName());

        }
        shared.edit().putString("resid", mAuth.getCurrentUser().getUid()).apply();


        final ImageView linkEdit = findViewById(R.id.link_edit);
        linkEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantMainActivity.this, RestaurantProfile.class));
            }
        });


        final LinearLayout link_order = findViewById(R.id.link_order_list);
        link_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantMainActivity.this, OrderListActivity.class));
            }
        });

        final LinearLayout link_food = findViewById(R.id.link_menu_list);
        link_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantMainActivity.this, EditFoodListActivity.class));
            }
        });


        final Button btnLogout = (Button) findViewById(R.id.button_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shared.edit().clear().commit();
                mAuth.getCurrentUser();
                mAuth.signOut();


                Intent intent = new Intent(RestaurantMainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        orderCount = findViewById(R.id.order_count);
        foodCount = findViewById(R.id.food_count);
        setCount();
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
                    mTitle.setText(cur.getName());
                    Picasso.get().load(cur.getUrl()).fit().centerCrop().into(resImage);
                }

            }
        });

    }

    private void setCount() {
        FirebaseFirestore firebase = FirebaseFirestore.getInstance();
        firebase.collection("restaurant")
                .document(mAuth.getCurrentUser().getUid())
                .collection("food")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot query, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        foodCount.setText("จำนวนทั้งหมด " + query.size());
                    }
                });
        firebase.collection("restaurant")
                .document(mAuth.getCurrentUser().getUid())
                .collection("orders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot query, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        orderCount.setText("" + query.size());
                    }
                });
    }
}
