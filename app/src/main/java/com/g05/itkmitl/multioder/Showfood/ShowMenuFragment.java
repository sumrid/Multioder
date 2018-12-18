package com.g05.itkmitl.multioder.Showfood;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.g05.itkmitl.multioder.MainActivity;
import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.food.Food;
import com.g05.itkmitl.multioder.food.FoodAdapter;
import com.g05.itkmitl.multioder.utils.DividerItem;

import java.util.ArrayList;

public class ShowMenuFragment extends Fragment {
    ArrayList<TestFood> foods = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_showfood_menu, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).setTitle("รายการอาหาร");

        foods.add(new TestFood("กระเพรา","ทดสอบพิมพ์ดู",35,
                "http://4.bp.blogspot.com/-b8PQN2ye6bw/UxR-5fy7SCI/AAAAAAAAAFg/_hlFlQZcWks/s1600/%E0%B8%81%E0%B8%A3%E0%B8%B0%E0%B9%80%E0%B8%9E%E0%B8%A3%E0%B8%B2%E0%B8%B0%E0%B9%84%E0%B8%81%E0%B9%88.jpg"));

        foods.add(new TestFood("กระเพรา","ทดสอบพิมพ์ดู",35,
                "http://4.bp.blogspot.com/-b8PQN2ye6bw/UxR-5fy7SCI/AAAAAAAAAFg/_hlFlQZcWks/s1600/%E0%B8%81%E0%B8%A3%E0%B8%B0%E0%B9%80%E0%B8%9E%E0%B8%A3%E0%B8%B2%E0%B8%B0%E0%B9%84%E0%B8%81%E0%B9%88.jpg"));

        mRecyclerView = getView().findViewById(R.id.main_showfood);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItem(getActivity(), LinearLayoutManager.VERTICAL, 10));
        mAdapter = new TestFoodAdapter(getActivity(),foods);
        mRecyclerView.setAdapter(mAdapter);


    }



}
