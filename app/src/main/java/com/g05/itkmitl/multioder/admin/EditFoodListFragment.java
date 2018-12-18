package com.g05.itkmitl.multioder.admin;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.admin.adapter.FoodAdapter;
import com.g05.itkmitl.multioder.food.Food;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EditFoodListFragment extends Fragment {
    private RecyclerView mRecylerView;
    private FloatingActionButton addFoodButton;

    private List<Food> mFoods;
    private FoodAdapter adapter;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_food_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFoods = new ArrayList<>();
        adapter = new FoodAdapter(getContext(), mFoods);

        mRecylerView = getActivity().findViewById(R.id.food_list_recyclerView);
        addFoodButton = getActivity().findViewById(R.id.add_food);

        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecylerView.setAdapter(adapter);

        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), AddFoodActivity.class));
            }
        });

        // TODO :  get res id when login
        SharedPreferences data = getActivity().getSharedPreferences("staff", Context.MODE_PRIVATE);
        data.edit().putString("res_id", "restaurant_04").commit();
        loadDataSet("restaurant_04");
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
