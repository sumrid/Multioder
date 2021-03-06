package com.g05.itkmitl.multioder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.g05.itkmitl.multioder.admin.OrderFragment;
import com.g05.itkmitl.multioder.food.FoodListFragment;
import com.g05.itkmitl.multioder.map.RestaurantMapsFragment;
import com.g05.itkmitl.multioder.notification.NotificationCountSetClass;
import com.g05.itkmitl.multioder.order_user.OrderHistoryFragment;
import com.g05.itkmitl.multioder.restaurant.RestaurantFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private boolean doubleBackToExitPressedOnce = false;
    public static boolean GoToCartFragment = false;

    String userEmail;
    String userFullName;
    String userAddress;
    String userPhone;
    User curUser;
    public static TextView mTitle;

    public static int CountCart;

    void getUserData() {
        mFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();
                    userEmail = mAuth.getCurrentUser().getEmail();
                    userFullName = documentSnapshot.getString("name");
                    userPhone = documentSnapshot.getString("phone");

                    curUser = new User(userFullName, userPhone);

                    TextView navName = (TextView) findViewById(R.id.nvName);
                    TextView navEmail = (TextView) findViewById(R.id.nvEmail);

                    navName.setText(userFullName);
                    navEmail.setText(userEmail);
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!haveCurrentUser()) {
            finish();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }


        getCountCart();
        getUserData();
        changeFragment(new FoodListFragment());



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setTitle("รายการอาหาร");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_view);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (f instanceof FoodListFragment) {
            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(MainActivity.this, CheckLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "กดอีกครั้งเพื่อออก", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Get the notifications MenuItem and
        // its LayerDrawable (layer-list)
        MenuItem item = menu.findItem(R.id.action_cart);
        NotificationCountSetClass.setAddToCart(MainActivity.this, item, CountCart);
        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
        }
        if (id == R.id.action_cart) {
            setTitle("ตะกร้า");
            changeFragment(new CartFragment());
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getUserData();
        if (GoToCartFragment) {
            changeFragment(new CartFragment());
            GoToCartFragment = false;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setTitle("รายการอาหาร");
            changeFragment(new FoodListFragment());
        } else if (id == R.id.nav_list) {
            setTitle("ร้านอาหาร");
            changeFragment(new RestaurantMapsFragment());
        } else if (id == R.id.nav_cart) {
            setTitle("ตะกร้า");
            changeFragment(new CartFragment());
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra("curUser", curUser);
            startActivity(intent);
        } else if (id == R.id.nav_signout) {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_orders_history) {
            setTitle("ประวัติการสั่งซื้อ");
            changeFragment(new OrderHistoryFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean haveCurrentUser() {
        if (mAuth.getCurrentUser() != null) {
            return true;
        }
        return false;
    }

    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_view, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void getCountCart() {

        mFirestore.collection("Users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("cart")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        CountCart = 0;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            CountCart++;
                        }
                    }
                });
    }

    public void setTitle(String name) {
        mTitle.setText(name);
    }
}
