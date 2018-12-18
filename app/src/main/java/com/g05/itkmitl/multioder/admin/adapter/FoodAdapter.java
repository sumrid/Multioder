package com.g05.itkmitl.multioder.admin.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.admin.AddFoodActivity;
import com.g05.itkmitl.multioder.food.Food;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private Context mContext;
    private List<Food> mFoods;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodName;
        public TextView foodDescrip;
        public TextView foodPrice;
        public ImageView foodImage;
        public ImageView editButton;
        public ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.food_name_order);
            foodDescrip = itemView.findViewById(R.id.food_item_description);
            foodPrice = itemView.findViewById(R.id.food_item_price);
            foodImage = itemView.findViewById(R.id.cart_item_image);
            editButton = itemView.findViewById(R.id.edit_btn);
            deleteButton = itemView.findViewById(R.id.remove_btn);
        }
    }

    public FoodAdapter(Context context, List<Food> dataSet) {
        this.mContext = context;
        this.mFoods = dataSet;
    }

    @NonNull
    @Override
    public FoodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mangefood_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.ViewHolder viewHolder, int i) {
        final Food food = mFoods.get(i);

        Picasso.get().load(food.getUrl()).fit().centerCrop().into(viewHolder.foodImage);
        viewHolder.foodName.setText(food.getName());
        viewHolder.foodDescrip.setText(food.getDescription());
        viewHolder.foodPrice.setText(String.format("%.0f บาท", food.getPrice()));

        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddFoodActivity.class);
                intent.putExtra("food", food);
                mContext.startActivity(intent);
            }
        });

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFood(food);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFoods.size();
    }

    private void deleteFood(Food food) {
        storage.getReferenceFromUrl(food.getUrl()).delete();
        firestore.collection("restaurant")
                .document(food.getRestaurantID())
                .collection("food")
                .document(food.getUid())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                notifyDataSetChanged();
            }
        });
    }
}
