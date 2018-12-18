package com.g05.itkmitl.multioder.cart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.g05.itkmitl.multioder.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartHistoryAdapter extends ArrayAdapter<CartItem> {
    private Context mContext;
    private List<CartItem> mCartItems;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public CartHistoryAdapter(@NonNull Context context, int resource, @NonNull List<CartItem> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mCartItems = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orderdetail_item, parent, false);

        CartItem item = mCartItems.get(position);
        ImageView image = view.findViewById(R.id.res_image);
        TextView name = view.findViewById(R.id.res_name);
        TextView amount = view.findViewById(R.id.food_item_price);
        TextView price = view.findViewById(R.id.res_telephone);
        final TextView restaurant = view.findViewById(R.id.cart_item_res);
        TextView total = view.findViewById(R.id.cart_item_total);

        Picasso.get().load(item.getFood().getUrl()).fit().centerCrop().into(image);
        name.setText(item.getFood().getName());
        amount.setText(String.format("จำนวน %d", item.getAmount()));
        price.setText(String.format("ราคา %.0f บาท", item.getFood().getPrice()));
        total.setText(item.getTotal() + " บาท");

        firestore.collection("restaurant")
                .whereEqualTo("id", item.getFood().getRestaurantID())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot query) {
                restaurant.setText(query.getDocuments().get(0).getString("name"));
            }
        });
        return view;
    }
}
