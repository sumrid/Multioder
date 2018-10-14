package com.g05.itkmitl.multioder.food;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.g05.itkmitl.multioder.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodAdapter extends ArrayAdapter<Food> {
    List<Food> foods;
    Context context;

    TextView foodName;
    TextView foodPrice;
    TextView foodDescrip;
    ImageView foodImage;

    public FoodAdapter(@NonNull Context context, int resource, @NonNull List<Food> objects) {
        super(context, resource, objects);
        this.foods = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View foodListView = LayoutInflater
                .from(context)
                .inflate(R.layout.fragment_food_item, parent, false);

        foodName = foodListView.findViewById(R.id.food_item_name);
        foodDescrip = foodListView.findViewById(R.id.food_item_description);
        foodPrice = foodListView.findViewById(R.id.food_item_price);
        foodImage = foodListView.findViewById(R.id.food_item_image);

        Food food = foods.get(position);
        foodName.setText(food.getName());
        foodDescrip.setText(food.getDescription());
        foodPrice.setText(food.getPrice()+"");

        Picasso.get().load(food.getUrl()).resize(300,300).centerCrop().placeholder(R.drawable.ic_launcher_foreground).into(foodImage);

        return foodListView;
    }
}
