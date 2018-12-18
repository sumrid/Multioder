package com.g05.itkmitl.multioder.order_user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.cart.CartHistoryAdapter;
import com.g05.itkmitl.multioder.map.LatLng;
import com.g05.itkmitl.multioder.map.MapsActivity;

public class OrdersDetailActivity extends AppCompatActivity {
    private ListView mListView;
    private TextView orderArea;
    private TextView orderTotalPrice;

    private CartHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_detail);

        Order order = (Order) getIntent().getSerializableExtra("order");
        final LatLng latLng = order.getCartItems().get(0).getLocation();

        mListView = findViewById(R.id.cart_item_list);
        orderArea = findViewById(R.id.order_area);
        orderTotalPrice = findViewById(R.id.order_total_price);

        adapter = new CartHistoryAdapter(this, R.layout.fragment_cart_item, order.getCartItems());
        mListView.setAdapter(adapter);

        orderTotalPrice.setText(String.format("%.0f บาท", order.getTotal()));
        orderArea.setText("พื้นที่จัดส่ง :");
        orderArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrdersDetailActivity.this, MapsActivity.class);
                intent.putExtra("location", latLng);
                startActivity(intent);
            }
        });
    }
}
