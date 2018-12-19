package com.g05.itkmitl.multioder.admin;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.g05.itkmitl.multioder.admin.adapter.OrderAdapter;
import com.g05.itkmitl.multioder.cart.CartItem;
import com.g05.itkmitl.multioder.utils.DividerItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Order2Fragment extends Fragment {
    private RecyclerView mRecyclerView;
    private OrderAdapter adapter;

    private List<CartItem> mCarts;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order2, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCarts = new ArrayList<>();
        adapter = new OrderAdapter(getActivity(), mCarts);

        mRecyclerView = getActivity().findViewById(R.id.order_list2);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItem(getActivity(), LinearLayoutManager.VERTICAL, 10));
        mRecyclerView.setAdapter(adapter);

        // TODO : get res id
        SharedPreferences shared = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String id = shared.getString("resid", null);

        Log.d("OrderList", "id="+id);
        if(id != null) {
            loadAlreadyOrders(id);
        }
    }

    private void loadAlreadyOrders(String resId) {
        firestore.collection("restaurant")
                .document(resId)
                .collection("orders_history")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        mCarts.clear();
                        for(DocumentSnapshot document : queryDocumentSnapshots) {
                            mCarts.add(document.toObject(CartItem.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

    }
}
