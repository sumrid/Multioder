package com.g05.itkmitl.multioder.map;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.restaurant.Restaurant;
import com.g05.itkmitl.multioder.restaurant.RestaurantMapsAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMapsFragment extends Fragment implements OnMapReadyCallback {
    public static GoogleMap map;
    private MapView mapView;

    private RecyclerView mRecyclerView;
    private RestaurantMapsAdapter adapter;
    private List<Restaurant> mRestaurants;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_maps, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRestaurants = new ArrayList<>();
        adapter = new RestaurantMapsAdapter(getActivity(), mRestaurants);

        mRecyclerView = getActivity().findViewById(R.id.restaurant_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
    }

    private void loadRestaurant() {
        firestore.collection("restaurant")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot query, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        mRestaurants.clear();
                        for(DocumentSnapshot document : query) {
                            mRestaurants.add(document.toObject(Restaurant.class));
                        }
                        adapter.notifyDataSetChanged();
                        markLocationToMap();
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        loadRestaurant();
    }

    private void markLocationToMap() {
        for(Restaurant item : mRestaurants) {
            LatLng location = item.getLocation().getGoogleLatLng();

            Marker marker = map.addMarker(new MarkerOptions().position(location).title(item.getName()));
            marker.showInfoWindow();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
