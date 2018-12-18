package com.g05.itkmitl.multioder.order_user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.order_user.Order;
import com.g05.itkmitl.multioder.order_user.OrderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class OrderHistoryActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private RecyclerView mRecyclerView;
    private OrderAdapter adapter;
    private List<Order> mOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        mOrders = new ArrayList<>();
        adapter = new OrderAdapter(this, mOrders);

        mRecyclerView = findViewById(R.id.orders_history_list);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        firestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("orders_history")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        mOrders.clear();
                        for(DocumentSnapshot document : queryDocumentSnapshots) {
                            mOrders.add(document.toObject(Order.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
