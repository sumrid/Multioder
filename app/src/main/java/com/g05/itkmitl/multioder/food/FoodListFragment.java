package com.g05.itkmitl.multioder.food;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.utils.DividerItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoodListFragment extends Fragment {
    ArrayList<Food> mFoods;
    List<String> resID;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public FoodListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFoods = new ArrayList<>();
        mAdapter = new FoodAdapter(getActivity(),mFoods);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView = getView().findViewById(R.id.food_list);
        mRecyclerView.addItemDecoration(new DividerItem(getActivity(), LinearLayoutManager.VERTICAL, 10));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        loadRestaurantID();
    }

    private void loadRestaurantID() {
        resID = new ArrayList<>();
        firestore.collection("restaurant")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        resID.clear();
                        for(DocumentSnapshot document : queryDocumentSnapshots) {
                            resID.add(document.getString("id"));
                            Log.d("FoodList", resID.size() + document.getString("id"));
                        }
                        loadFood(resID);
                    }
                });
    }

    private void loadFood(List<String> resID) {
        mFoods.clear();
        for(String id : resID) {
            firestore.collection("restaurant")
                    .document(id)
                    .collection("food")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Food food = document.toObject(Food.class);
                                food.setUid(document.getId());
                                mFoods.add(food);
                                Log.d("FoodList", food.getUid());
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
        mAdapter.notifyDataSetChanged();
    }
}
