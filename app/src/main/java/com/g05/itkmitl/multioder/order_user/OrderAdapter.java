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
import com.g05.itkmitl.multioder.cart.CartItem;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context mContext;
    private List<Order> mOrders;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        public TextView orderId, orderDate, orderUnit, orderTotal;
        public Button orderDetail;
        private ClickListener clickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.res_name);
            orderDate = itemView.findViewById(R.id.res_telephone);
            orderUnit = itemView.findViewById(R.id.order_unit);
            orderTotal = itemView.findViewById(R.id.order_total);
//            orderDetail = itemView.findViewById(R.id.order_detail_button);
            itemView.setOnClickListener(this);
        }

        public void setOnItemClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }


    public OrderAdapter(Context context, List<Order> dataSet) {
        this.mContext = context;
        this.mOrders = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_orderhistory_item,viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Order order = mOrders.get(i);

        viewHolder.orderId.setText(order.getId());
        viewHolder.orderTotal.setText(String.format("ยอดรวม %.0f บาท", order.getTotal()));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
        viewHolder.orderDate.setText(sdf.format(order.getDate()));

        viewHolder.setOnItemClickListener(new ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(mContext, OrdersDetailActivity.class);
                intent.putExtra("order", order);
                mContext.startActivity(intent);
            }
        });

        int amount = 0;
        for(CartItem item : order.getCartItems()) {
            amount += item.getAmount();
        }
        viewHolder.orderUnit.setText(String.format("จำนวน %s รายการ", amount));
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }
}
