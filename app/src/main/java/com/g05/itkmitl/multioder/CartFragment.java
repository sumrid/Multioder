package com.g05.itkmitl.multioder;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.g05.itkmitl.multioder.cart.CartAdapter;
import com.g05.itkmitl.multioder.cart.CartItem;
import com.g05.itkmitl.multioder.food.Food;
import com.g05.itkmitl.multioder.food.FoodAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.CryptoPrimitive;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    private ListView listView;
    private CartAdapter cartAdapter;
    private TextView totalTextView,cartSizeText;
    private double total;
    private List<CartItem> cartItems;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private SwipeMenuListView swipeListView;

    public CartFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        swipeListView = getView().findViewById(R.id.cart_listView);

        total = 0;
        cartItems = new ArrayList<>();
        totalTextView = getActivity().findViewById(R.id.cart_total);
        cartSizeText = getActivity().findViewById(R.id.cartsize_text);
        listView = getActivity().findViewById(R.id.cart_listView);
        cartAdapter = new CartAdapter(getContext(), R.layout.fragment_cart_item, cartItems);

        setSwipeListView();

        listView.setAdapter(cartAdapter);
        getFoods();

        // set this for POP-UP menu when long click on item
        registerForContextMenu(listView);



    }


    private void setSwipeListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red_primary)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_bin);
                // set item title
                deleteItem.setTitle("ลบ");
                // set item title fontsize
                deleteItem.setTitleSize(14);
                // set item title font color
                deleteItem.setTitleColor(getResources().getColor(R.color.white));
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        swipeListView.setMenuCreator(creator);
        swipeListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                deleteAll(position);
                return false;
            }
        });
    }

        private void getFoods() {
        firestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("cart")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        cartItems.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            addFoodToCartItem(document.toObject(Food.class));
                        }
                        cartAdapter.notifyDataSetChanged();
                        calculatePrice();
                    }
                });
    }

    private void calculatePrice() {
        for (CartItem item : cartItems) {
            total = total + item.getTotal();
        }
        updateTotalPrice(total);
    }

    private void addFoodToCartItem(Food food) {
        boolean added = false;
        int index = -1;
        if (!cartItems.isEmpty()) {
            for (CartItem item : cartItems) {
                if (item.getUid().equals(food.getUid())) {
                    added = true;
                    index = cartItems.indexOf(item);
                }
            }
            if (added) {
                CartItem item = cartItems.get(index);
                item.setAmount(item.getAmount() + 1);
                item.addDeleteKey(food.getDeleteKey());
            } else {
                cartItems.add(new CartItem(food, 1));
            }
        } else {
            cartItems.add(new CartItem(food, 1));
        }
    }


    /*************************************************************************************************************
     *   refer : https://www.mikeplate.com/2010/01/21/show-a-context-menu-for-long-clicks-in-an-android-listview/
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.add(Menu.NONE, 1, 1, "ลดจำนวน");
        menu.add(Menu.NONE, 2, 2, "Delete All");
        Log.d("Context Menu", "cart item : " + info.id);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Log.d("Context Menu", "Menu ID : " + item.getItemId() + " || Item : " + info.id);
        if (item.getItemId() == 1) deleteFood(info.id);
        else deleteAll(info.id);
        return true;
    }

    private void deleteFood(long position) {
        CartItem item = cartItems.get((int) position);
        firestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("cart")
                .document(item.getDeleteKeys().get(0))
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                calculatePrice();
            }
        });
    }

    private void deleteAll(long position) {
        CartItem item = cartItems.get((int) position);
        for (String key : item.getDeleteKeys()) {
            firestore.collection("Users")
                    .document(auth.getCurrentUser().getUid())
                    .collection("cart")
                    .document(key)
                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    calculatePrice();
                }
            });
        }
    }

    private void updateTotalPrice(double price) {
        totalTextView.setText("" + price);
        cartSizeText.setText("คุณมีรายการอาหารในตะกร้าทั้งหมด "+getCartSize());
        MainActivity.CountCart = getCartSize();
        total = 0;
    }

    public int getCartSize(){
        return cartItems.size();
    }
}
