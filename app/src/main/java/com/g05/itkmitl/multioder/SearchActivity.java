package com.g05.itkmitl.multioder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.g05.itkmitl.multioder.food.Food;
import com.g05.itkmitl.multioder.food.FoodAdapter;
import com.g05.itkmitl.multioder.utils.DividerItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText textSearch;
    private RecyclerView mRecyclerView;
    private FoodAdapter mAdapter;
    private List<Food> mFoods;
    private List<Food> mSerachResult;
    private List<String> resID;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        loadRestaurantID();
        mFoods = new ArrayList<>();
        mSerachResult = new ArrayList<>();
        mAdapter = new FoodAdapter(this, mSerachResult);

        mRecyclerView = findViewById(R.id.search_list);
        mRecyclerView.addItemDecoration(new DividerItem(this, LinearLayoutManager.VERTICAL, 10));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        textSearch = findViewById(R.id.text_search);
        textSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    filter(textSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });
        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(textSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadRestaurantID() {
        resID = new ArrayList<>();
        firestore.collection("restaurant")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        resID.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            resID.add(document.getString("id"));
                        }
                        loadFood(resID);
                    }
                });
    }

    private void loadFood(List<String> resID) {
        mFoods.clear();
        for (String id : resID) {
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
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
        mAdapter.notifyDataSetChanged();
    }

    private void filter(String keyword) {
        Log.d("SearchActivity", "keyword: " + keyword);
        mSerachResult.clear();
        for(Food food : mFoods) {
            if(food.getName().contains(keyword) && !keyword.isEmpty()) {
                mSerachResult.add(food);
                mAdapter.notifyDataSetChanged();
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
