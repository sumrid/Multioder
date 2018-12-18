package com.g05.itkmitl.multioder.food;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.cart.CartItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> foods;
    private Context mContext;
    private ArrayList<CartItem> cartItems = new ArrayList<>();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodName;
        public TextView foodDescrip;
        public TextView foodPrice;
        public TextView foodResName;
        public ImageView foodImage;
        private Button btnAddCart;


        public ViewHolder(View view) {
            super(view);

            foodName = (TextView) view.findViewById(R.id.cart_item_name);
            foodDescrip = (TextView) view.findViewById(R.id.food_item_description);
            foodPrice = (TextView) view.findViewById(R.id.food_item_price);
            foodImage = (ImageView) view.findViewById(R.id.cart_item_image);
            btnAddCart = (Button) view.findViewById(R.id.btn_addcart);
            foodResName = view.findViewById(R.id.food_item_res_name);

        }
    }

    public FoodAdapter(Context context, List<Food> dataset) {
        foods = dataset;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.fragment_food_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final Food selectFood = foods.get(position);

        viewHolder.foodName.setText(selectFood.getName());
        viewHolder.foodDescrip.setText(selectFood.getDescription());
        viewHolder.foodPrice.setText(String.format("%.0f บาท", selectFood.getPrice()));
        Picasso.get().load(selectFood.getUrl()).fit().centerCrop().placeholder(R.drawable.ic_launcher_foreground).into(viewHolder.foodImage);

        firebaseFirestore.collection("restaurant")
                .whereEqualTo("id", selectFood.getRestaurantID())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot query) {
                viewHolder.foodResName.setText(query.getDocuments().get(0).getString("name"));
            }
        });

        loadCartItem();
        viewHolder.btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(selectFood);
//                addFoodToCart(selectFood);
//                Intent intent = new Intent(mContext, FoodDetailActivity.class);
//                intent.putExtra("food", food);
//                mContext.startActivity(intent);
            }
        });

        viewHolder.foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FoodDetailActivity.class);
                intent.putExtra("food", selectFood);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    private void addFoodToCart(Food foodToCart) {
        boolean added = false;
        int index = -1;
        CartItem currentItem;

        if (!cartItems.isEmpty()) {
            for (CartItem item : cartItems) {
                if (item.getUid().equals(foodToCart.getUid())) {
                    added = true;
                    index = cartItems.indexOf(item);
                }
            }
            if (added) {
                currentItem = cartItems.get(index);
                currentItem.setAmount(currentItem.getAmount() + 1);
            } else {
                currentItem = new CartItem(foodToCart, 1);
            }
        } else {
            currentItem = new CartItem(foodToCart, 1);
        }

        auth.getCurrentUser().getUid();
        firebaseFirestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("cart")
                .document(currentItem.getUid())
                .set(currentItem);
    }

    private void loadCartItem() {
        firebaseFirestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("cart")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        cartItems.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            cartItems.add(document.toObject(CartItem.class));
                        }
                    }
                });
        Log.d("FoodAdapter", "cart item size = " + cartItems.size());
    }

    private void showDialog(final Food selectedFood) {
        final EditText input = new EditText(mContext);
        input.setHint("เช่น เผ็ดน้อย, ไม่เอาผัก");

        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("รายละเอียดเพิ่มเติม");
        alertDialog.setView(input);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add to Cart",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String additional = input.getText().toString();
                        addFoodToCart(selectedFood);
                        Toast.makeText(mContext, "Added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
}
