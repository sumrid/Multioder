package com.g05.itkmitl.multioder.managerestaurant;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.Showfood.TestFood;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderFoodAdapter extends RecyclerView.Adapter<OrderFoodAdapter.ViewHolder> {

    private List<TestFood> foods;
    private Context mContext;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodName;
        public TextView foodDescrip;
        public TextView foodPrice;
        public ImageView foodImage;


        public ViewHolder(View view) {
            super(view);

            foodName = (TextView) view.findViewById(R.id.food_name);
            foodDescrip = (TextView) view.findViewById(R.id.food_detail);
            foodPrice = (TextView) view.findViewById(R.id.food_price);
            foodImage = (ImageView) view.findViewById(R.id.order_icon);

        }
    }

    public OrderFoodAdapter(Context context, List<TestFood> dataset) {
        foods = dataset;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.fragment_showfoodmenu_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final TestFood selectFood = foods.get(position);

        viewHolder.foodName.setText(selectFood.getName());
        viewHolder.foodDescrip.setText(selectFood.getDescription());
        viewHolder.foodPrice.setText(String.format("%.0f บาท", selectFood.getPrice()));
        Picasso.get().load(selectFood.getUrl()).fit().centerCrop().placeholder(R.drawable.ic_launcher_foreground).into(viewHolder.foodImage);



    }

    @Override
    public int getItemCount() {
        return foods.size();
    }



}
