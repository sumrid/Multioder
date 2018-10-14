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
import java.util.List;

public class FoodAdapter extends ArrayAdapter<Food> {
    List<Food> foods;
    Context context;

    TextView foodName;
    TextView foodPrice;
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
        foodPrice = foodListView.findViewById(R.id.food_item_price);
        foodImage = foodListView.findViewById(R.id.food_item_image);

        Food food = foods.get(position);
        foodName.setText(food.getName());
        foodPrice.setText(food.getPrice()+"");

        return foodListView;
    }
}
