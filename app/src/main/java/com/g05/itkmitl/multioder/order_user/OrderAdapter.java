package com.g05.itkmitl.multioder.order_user;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.g05.itkmitl.multioder.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context mContext;
    private List<Order> mOrders;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView orderId, orderDate;
        public Button orderDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.order_id);
            orderDate = itemView.findViewById(R.id.order_date);
            orderDetail = itemView.findViewById(R.id.order_detail_button);
        }
    }

    public OrderAdapter(Context context, List<Order> dataSet) {
        this.mContext = context;
        this.mOrders = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_orders_history_item,viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Order order = mOrders.get(i);

        viewHolder.orderId.setText(order.getId());
        viewHolder.orderDate.setText(order.getDate().toString());
        viewHolder.orderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OrdersDetailActivity.class);
                intent.putExtra("order", order);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }
}
