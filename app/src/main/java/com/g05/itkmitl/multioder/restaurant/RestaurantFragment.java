package com.g05.itkmitl.multioder.restaurant;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.food.Food;
import com.g05.itkmitl.multioder.food.FoodListFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantFragment extends Fragment {
    GridView gridView;
    RestaurantAdapter restaurantAdapter;
    List<Restaurant> restaurants;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public RestaurantFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        restaurants = new ArrayList<>();
        loadRestaurantData();
        gridView = getActivity().findViewById(R.id.restaurant_grid_view);
        restaurantAdapter = new RestaurantAdapter(getContext(), R.layout.fragment_restaurant_item, restaurants);
        gridView.setAdapter(restaurantAdapter);

        initOnItemClick();
    }

    private void initOnItemClick(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Test","onclick = " + restaurants.get(position).getName());

                FoodListFragment fragment = new FoodListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("restaurant", restaurants.get(position).getName());
                fragment.setArguments(bundle);
                changeFragment(fragment);
            }
        });
    }

    private void changeFragment(Fragment fragment){
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_view, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadRestaurantData() {
        firebaseFirestore.collection("restaurant")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        restaurants.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            restaurants.add(document.toObject(Restaurant.class));
                        }
                        restaurantAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void setRestaurantsToFirebase(){
                for(Restaurant restaurant : restaurants){
            firebaseFirestore.collection("restaurant")
                    .document(restaurant.getName())
                    .set(restaurant)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Error", e.getMessage());
                        }
                    });
        }
    }
}
