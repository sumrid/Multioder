package com.g05.itkmitl.multioder.restaurant;

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

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {
    List<Restaurant> restaurants;
    Context context;
    TextView name;
    ImageView image;
    public RestaurantAdapter(@NonNull Context context, int resource, @NonNull List<Restaurant> objects) {
        super(context, resource, objects);
        this.context = context;
        restaurants = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
        View restaurantList = LayoutInflater
                .from(context)
                .inflate(R.layout.fragment_restaurant_item, parent, false);

        Restaurant item = restaurants.get(position);
        name = restaurantList.findViewById(R.id.restaurant_name);
        image = restaurantList.findViewById(R.id.imageView);
        name.setText(item.getName());
        Picasso.get().load(item.getUrl()).fit().centerCrop().error(R.drawable.one_pot_chorizo).into(image);
        return restaurantList;
    };
}
