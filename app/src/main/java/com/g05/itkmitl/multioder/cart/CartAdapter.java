package com.g05.itkmitl.multioder.cart;

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

public class CartAdapter extends ArrayAdapter<CartItem> {
    Context context;
    List<CartItem> cartItems;

    public CartAdapter(@NonNull Context context, int resource, @NonNull List<CartItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.cartItems = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View CartListView = LayoutInflater
                .from(context)
                .inflate(R.layout.fragment_cart_item, parent, false);

        ImageView imageView = CartListView.findViewById(R.id.cart_item_image);
        TextView name = CartListView.findViewById(R.id.cart_item_name);
        TextView amount = CartListView.findViewById(R.id.cart_item_amount);
        TextView total = CartListView.findViewById(R.id.cart_item_total);

        CartItem item = cartItems.get(position);
        name.setText(item.getFood().getName());
        amount.setText(""+item.getAmount());
        total.setText("฿ "+item.getTotal());
        Picasso.get().load(item.getFood().getUrl()).fit().centerCrop().into(imageView);

        return CartListView;
    }
}
