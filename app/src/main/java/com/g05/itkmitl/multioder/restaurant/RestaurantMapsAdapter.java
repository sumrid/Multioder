package com.g05.itkmitl.multioder.restaurant;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.map.RestaurantMapsFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.annotation.Nullable;

public class RestaurantMapsAdapter extends RecyclerView.Adapter<RestaurantMapsAdapter.ViewHolder> {
    private Context mContext;
    private List<Restaurant> mRestaurants;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name, telephone, queue, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.res_image);
            name = itemView.findViewById(R.id.res_name);
            telephone = itemView.findViewById(R.id.res_telephone);
            queue = itemView.findViewById(R.id.res_queue);
            status = itemView.findViewById(R.id.res_status);
        }
    }

    public RestaurantMapsAdapter(Context mContext, List<Restaurant> mRestaurants) {
        this.mContext = mContext;
        this.mRestaurants = mRestaurants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.restaurant_map_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder view, int i) {
        final Restaurant item = mRestaurants.get(i);

        Picasso.get().load(item.getUrl()).fit().centerCrop().into(view.image);
        view.name.setText(item.getName());
        view.telephone.setText("tel." + item.getTelephone());
        view.status.setText("เปิด");

        firestore.collection("restaurant")
                .document(item.getId())
                .collection("orders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot query, @Nullable FirebaseFirestoreException e) {
                        view.queue.setText("จำนวนคิวอาหาร: " + query.size());
                    }
                });

        view.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCamera(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    private void moveCamera(Restaurant item) {
        LatLng location = item.getLocation().getGoogleLatLng();
        RestaurantMapsFragment.map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }
}
