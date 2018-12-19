package com.g05.itkmitl.multioder.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.admin.adapter.FoodAdapter;
import com.g05.itkmitl.multioder.admin.adapter.OrderAdapter;
import com.g05.itkmitl.multioder.cart.CartItem;
import com.g05.itkmitl.multioder.food.Food;
import com.g05.itkmitl.multioder.restaurant.Restaurant;
import com.g05.itkmitl.multioder.utils.DividerItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class EditFoodListActivity extends AppCompatActivity {
    private RecyclerView mRecylerView;
    private FloatingActionButton addFoodButton;

    private List<Food> mFoods;
    private FoodAdapter adapter;


    private Restaurant current;
    private String currentLogin;
    private SharedPreferences shared;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managefood);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.orderlist_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        mFoods = new ArrayList<>();
        adapter = new FoodAdapter(getApplicationContext(), mFoods);

        mRecylerView = findViewById(R.id.food_list_recyclerView);
        addFoodButton = findViewById(R.id.add_food);

        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecylerView.addItemDecoration(new DividerItem(getApplicationContext(), LinearLayoutManager.VERTICAL, 10));
        mRecylerView.setAdapter(adapter);

        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddFoodActivity.class));
            }
        });


        shared = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        currentLogin = shared.getString("current_user", null);


        if (currentLogin == null) {
            getUserData();

        } else {

            Gson gson = new Gson();
            current = gson.fromJson(currentLogin, Restaurant.class);

        }

        // TODO :  get res id when login
        loadDataSet(current.getId());
    }

    private void getUserData() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("restaurant").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();


                    Restaurant cur = new Restaurant(documentSnapshot.getString("name"),
                            documentSnapshot.getString("url"), mAuth.getCurrentUser().getUid(),
                            documentSnapshot.getString("telephone"));

                    current = cur;
                    Gson gson = new Gson();
                    String json = gson.toJson(cur);
                    shared.edit().putString("current_user", json).commit();

                }
            }
        });

    }


    private void loadDataSet(String restaurantId) {
        firestore.collection("restaurant")
                .document(restaurantId)
                .collection("food")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        mFoods.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Food food = document.toObject(Food.class);
                            food.setUid(document.getId());
                            mFoods.add(food);
                            Log.d("Load Food Data", food.getUid());
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
