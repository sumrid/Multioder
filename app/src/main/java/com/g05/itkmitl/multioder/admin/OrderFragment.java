package com.g05.itkmitl.multioder.admin;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.admin.adapter.OrderAdapter;
import com.g05.itkmitl.multioder.cart.CartItem;
import com.g05.itkmitl.multioder.restaurant.Restaurant;
import com.g05.itkmitl.multioder.utils.DividerItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private OrderAdapter adapter;

    private List<CartItem> mCarts;
    private Restaurant current;
    private String currentLogin;
    private SharedPreferences shared;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCarts = new ArrayList<>();
        adapter = new OrderAdapter(getActivity(), mCarts);

        mRecyclerView = getActivity().findViewById(R.id.order_list);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItem(getActivity(), LinearLayoutManager.VERTICAL, 10));
        mRecyclerView.setAdapter(adapter);

        shared = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        currentLogin = shared.getString("current_user", null);


        if (currentLogin == null) {
            getUserData();
        } else {

            Gson gson = new Gson();
            current = gson.fromJson(currentLogin, Restaurant.class);

        }



        // TODO : get res id
        loadOrders(current.getId());
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

    private void loadOrders(String resId) {
        firestore.collection("restaurant")
                .document(resId)
                .collection("orders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        mCarts.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            mCarts.add(document.toObject(CartItem.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

    }
}
