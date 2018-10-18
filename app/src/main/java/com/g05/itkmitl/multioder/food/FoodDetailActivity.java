package com.g05.itkmitl.multioder.food;

import android.support.design.internal.BottomNavigationMenuView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.g05.itkmitl.multioder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class FoodDetailActivity extends AppCompatActivity {
    Food food;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Food Detail");
        }

        food = (Food) getIntent().getSerializableExtra("food");
        ImageView foodImage = findViewById(R.id.food_detail_image);
        TextView foodName = findViewById(R.id.food_detail_name);
        TextView foodDes = findViewById(R.id.food_detail_descrip);
        TextView foodPrice = findViewById(R.id.food_detail_price);
        foodName.setText(food.getName());
        foodDes.setText(food.getDescription());
        foodPrice.setText(food.getPrice()+" Baht");
        Picasso.get().load(food.getUrl()).fit().centerCrop().into(foodImage);

        initAddToCartButton();
    }

    private void initAddToCartButton() {
        Button addToCart = findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Added", Toast.LENGTH_SHORT).show();
                addFoodToCart();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addFoodToCart(){
        food.setKey(System.currentTimeMillis()+"");
        auth.getCurrentUser().getUid();
        firebaseFirestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("cart")
                .document(food.getKey())
                .set(food);
    }
}
