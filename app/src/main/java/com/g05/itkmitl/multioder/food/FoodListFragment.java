package com.g05.itkmitl.multioder.food;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.g05.itkmitl.multioder.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoodListFragment extends Fragment {
    ListView foodList;
    ArrayList<Food> foods;

    public FoodListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setFoods();
        foodList = getView().findViewById(R.id.food_list);
        foodList.setAdapter(new FoodAdapter(getActivity(), R.layout.fragment_food_item, foods));
    }

    private void setFoods() {
        foods = new ArrayList<>();
        foods.add(new Food("A", "a", 20));
        foods.add(new Food("B", "a", 20));
        foods.add(new Food("C", "a", 20));
        foods.add(new Food("D", "a", 50));
    }
}
