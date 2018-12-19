package com.g05.itkmitl.multioder.cart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.g05.itkmitl.multioder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends ArrayAdapter<CartItem> {
    Context context;
    List<CartItem> cartItems;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public CartAdapter(@NonNull Context context, int resource, @NonNull List<CartItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.cartItems = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View CartListView = LayoutInflater
                .from(context)
                .inflate(R.layout.fragment_cart_item, parent, false);

        ImageView imageView = CartListView.findViewById(R.id.res_image);
        TextView name = CartListView.findViewById(R.id.res_name);
        final TextView amount = CartListView.findViewById(R.id.food_item_price);
        final TextView total = CartListView.findViewById(R.id.cart_item_total);
        final TextView detail = CartListView.findViewById(R.id.food_item_restaname);
        ImageView reduceButton = CartListView.findViewById(R.id.cart_item_reduce);
        ImageView addButton = CartListView.findViewById(R.id.cart_item_add);

        final CartItem item = cartItems.get(position);
        Log.d("Cart Adapter", item.toString());
        name.setText(item.getFood().getName());
        amount.setText("" + item.getAmount());
        detail.setText(""+ item.getFood().getDescription());
        total.setText(String.format("฿ %.0f", item.getTotal()));

        Picasso.get().load(item.getFood().getUrl()).fit().centerCrop().into(imageView);


        reduceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                    amount.setText("" + item.getAmount());
                    total.setText(String.format("฿ %.0f", item.getTotal()));
                    updateItem(item);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setAmount(item.getAmount() + 1);
                amount.setText("" + item.getAmount());
                total.setText(String.format("฿ %.0f", item.getTotal()));
                updateItem(item);
            }
        });

        return CartListView;
    }

    private void updateItem(CartItem cartItem) {
        firestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("cart")
                .document(cartItem.getUid())
                .set(cartItem);
    }

}
