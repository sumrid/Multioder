package com.g05.itkmitl.multioder;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.g05.itkmitl.multioder.cart.CartAdapter;
import com.g05.itkmitl.multioder.cart.CartItem;
import com.g05.itkmitl.multioder.food.Food;
import com.g05.itkmitl.multioder.food.FoodAdapter;
import com.g05.itkmitl.multioder.food.FoodListFragment;
import com.g05.itkmitl.multioder.map.LatLng;
import com.g05.itkmitl.multioder.map.MapsActivity;
import com.g05.itkmitl.multioder.order_user.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.security.CryptoPrimitive;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    private ListView listView;
    private CartAdapter cartAdapter;
    private TextView totalTextView,cartSizeText;
    public static TextView locationText;
    private Button comfirmButton;

    private double total;
    private List<CartItem> cartItems;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private SwipeMenuListView swipeListView;

    public static LatLng location;

    public static boolean checktNew;



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


        MainActivity.mTitle.setText("ตะกร้า");
        checktNew=true;

        swipeListView = getView().findViewById(R.id.cart_listView);

        total = 0;
        cartItems = new ArrayList<>();
        totalTextView = getActivity().findViewById(R.id.cart_total);
        cartSizeText = getActivity().findViewById(R.id.cartsize_text);
        listView = getActivity().findViewById(R.id.cart_listView);
        comfirmButton = getActivity().findViewById(R.id.button_confirm);
        locationText = getActivity().findViewById(R.id.location_text);
        locationText.setTextColor(getActivity().getColor(R.color.pinky));

        cartAdapter = new CartAdapter(getContext(), R.layout.fragment_cart_item, cartItems);

        setSwipeListView();

        listView.setAdapter(cartAdapter);
        getFoods();

        final LinearLayout btn_location = getView().findViewById(R.id.link_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "กรุณาเลือกพื้นที่จัดส่ง", Toast.LENGTH_SHORT).show();
                getActivity().startActivity(new Intent(getActivity(), MapsActivity.class));
            }
        });

        comfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cartItems.isEmpty()) {
                    if(location == null || checktNew) {
                        Toast.makeText(getContext(), "กรุณาเลือกพื้นที่จัดส่ง", Toast.LENGTH_SHORT).show();
                    }
                    else createOrder();
                } else {
                    Toast.makeText(getContext(), "กรุณาเลือกอาหารก่อน", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set this for POP-UP menu when long click on item
//        registerForContextMenu(listView);

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
                            cartItems.add(document.toObject(CartItem.class));
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


    private void deleteAll(long position) {
        CartItem item = cartItems.get((int) position);
        firestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("cart")
                .document(item.getUid())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                calculatePrice();
            }
        });
        reloadFragment();
    }

    private void updateTotalPrice(double price) {
        totalTextView.setText(String.format("%.0f", price));
        cartSizeText.setText("คุณมีรายการอาหารในตะกร้าทั้งหมด "+getCartSize());
        MainActivity.CountCart = getCartSize();
        total = 0;
    }

    public int getCartSize(){
        return cartItems.size();
    }

    private void reloadFragment(){
        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.main_view);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(currentFragment);
        fragmentTransaction.attach(currentFragment);
        fragmentTransaction.commit();
    }

    private void createOrder() {
        // TODO : create orders history
        for(CartItem item : cartItems) {
            item.setLocation(location);
        }

        Order order = new Order();
        order.setId("my_order_" + System.currentTimeMillis());
        order.setDate(new Date());
        order.setCartItems(cartItems);
        order.setTotal(total);
        firestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("orders_history")
                .document(order.getId())
                .set(order);

        firestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                User user = documentSnapshot.toObject(User.class);

                for(CartItem item : cartItems) {
                    String uid = "order_" + System.currentTimeMillis();
                    String deleteKey = item.getUid();

                    item.setUser(user);
                    item.setUid(uid);

                    // create order to restaurant
                    firestore.collection("restaurant")
                            .document(item.getFood().getRestaurantID())
                            .collection("orders")
                            .document(uid)
                            .set(item);

                    // delete from user cart
                    firestore.collection("Users")
                            .document(auth.getCurrentUser().getUid())
                            .collection("cart")
                            .document(deleteKey).delete();
                }

                Toast.makeText(getContext(), "สั่งซื้อเรียบร้อย!", Toast.LENGTH_SHORT).show();

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view,new FoodListFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
