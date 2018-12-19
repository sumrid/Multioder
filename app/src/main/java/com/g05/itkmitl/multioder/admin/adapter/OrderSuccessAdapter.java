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

public class OrderSuccessAdapter extends RecyclerView.Adapter<OrderSuccessAdapter.ViewHolder> {
    private Context mContext;
    private List<CartItem> mCartItem;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, amount,orderID;
        public ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.order_success_img);
            name = itemView.findViewById(R.id.order_success_name);
            amount = itemView.findViewById(R.id.order_success_amount);
            orderID = itemView.findViewById(R.id.order_id_item);
        }
    }

    public OrderSuccessAdapter(Context context, List<CartItem> dataSet) {
        this.mContext = context;
        this.mCartItem = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.restaurant_order_success_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final CartItem item = mCartItem.get(i);

        Picasso.get().load(item.getFood().getUrl()).fit().centerCrop().into(viewHolder.image);
        viewHolder.orderID.setText("เลขใบสั่งซื้อ: "+item.getUid().replaceAll("order_","").substring(0,6));
        viewHolder.name.setText(item.getFood().getName());
        viewHolder.amount.setText(String.format("%d ชิ้น", item.getAmount()));

    }

    @Override
    public int getItemCount() {
        return mCartItem.size();
    }

}
