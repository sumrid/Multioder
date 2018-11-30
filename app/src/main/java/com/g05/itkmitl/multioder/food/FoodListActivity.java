package com.g05.itkmitl.multioder.food;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;

import com.g05.itkmitl.multioder.MainActivity;
import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.notification.NotificationCountSetClass;
import com.g05.itkmitl.multioder.restaurant.Restaurant;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodListActivity extends AppCompatActivity {

    ListView foodList;
    ArrayList<Food> foods = new ArrayList<>();
    FoodAdapter foodAdapter;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Uri imageUri;
    Restaurant restaurant;

    public ImageView restaurantImage;

    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodlist);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.food_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FoodAdapter(this,foods);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        loadFoodData(getRestaurantId());


        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(restaurant.getName());

        restaurantImage = (ImageView) findViewById(R.id.restaurant_header_img);
        Picasso.get().load(restaurant.getUrl()).fit().centerCrop().placeholder(R.drawable.ic_launcher_foreground).into(restaurantImage);

    }


    private void loadFoodData(String restaurantId) {
        firebaseFirestore.collection("restaurant")
                .document(restaurantId)
                .collection("food")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        foods.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Food food = document.toObject(Food.class);
                            food.setUid(document.getId());
                            foods.add(food);
                            Log.d("Load Food Data", food.getUid());
                        }

                        if (mAdapter != null) mAdapter.notifyDataSetChanged();

                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.foodview, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Get the notifications MenuItem and
        // its LayerDrawable (layer-list)
        MenuItem item = menu.findItem(R.id.action_cart);
        NotificationCountSetClass.setAddToCart(this, item,MainActivity.CountCart);
        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    private String getRestaurantId(){

        restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        if(restaurant != null){
            return restaurant.getId();
        }
        return "null";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_cart:
                MainActivity.GoToCartFragment = true;
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
