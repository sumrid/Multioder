package com.g05.itkmitl.multioder.admin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.cart.CartItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context mContext;
    private List<CartItem> mCartItem;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, amount;
        public ImageView image;
        public ImageView checkButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.cart_item_image);
            name = itemView.findViewById(R.id.food_name_order);
            amount = itemView.findViewById(R.id.food_item_price);
            checkButton = itemView.findViewById(R.id.cart_check);
        }
    }

    public OrderAdapter(Context context, List<CartItem> dataSet) {
        this.mContext = context;
        this.mCartItem = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.restaurant_order_page_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final CartItem item = mCartItem.get(i);

        Picasso.get().load(item.getFood().getUrl()).fit().centerCrop().into(viewHolder.image);
        viewHolder.name.setText(item.getFood().getName());
        viewHolder.amount.setText(String.format("%d ชิ้น", item.getAmount()));
        viewHolder.checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatusOrder(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCartItem.size();
    }

    private void changeStatusOrder(CartItem item) {
        firestore.collection("restaurant")
                .document(item.getFood().getRestaurantID())
                .collection("orders_history")
                .document(item.getUid())
                .set(item);

        firestore.collection("restaurant")
                .document(item.getFood().getRestaurantID())
                .collection("orders")
                .document(item.getUid())
                .delete();
    }
}
