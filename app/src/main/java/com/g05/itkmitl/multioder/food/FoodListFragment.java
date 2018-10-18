package com.g05.itkmitl.multioder.food;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.g05.itkmitl.multioder.LoginFragment;
import com.g05.itkmitl.multioder.MainActivity;
import com.g05.itkmitl.multioder.ProfileFragment;
import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.RegisterFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.http.Url;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoodListFragment extends Fragment {
    ListView foodList;
    ArrayList<Food> foods = new ArrayList<>();
    FoodAdapter foodAdapter;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Uri imageUri;

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

        testUpload(); // for test !!!
        initLogout(); // for test !!!
        initGotoProfile(); // TEST DELETE THIS !!
        setFoods();
        loadFoodData(getRestaurantName());
        foodList = getView().findViewById(R.id.food_list);
        foodAdapter = new FoodAdapter(getActivity(), R.layout.fragment_food_item, foods);
        foodList.setAdapter(foodAdapter);
        initOnClickItem();
        foods.clear();
    }

    private void initGotoProfile() { // \\\\\\\\\TEST//////////
        Button gotoProfile = getView().findViewById(R.id.gotoProfile);
        gotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new ProfileFragment())
                        .addToBackStack(null).commit();
            }
        });
    }

    private void initOnClickItem(){
        foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), FoodDetailActivity.class);
                intent.putExtra("food", foods.get(position));
                getActivity().startActivity(intent);
            }
        });
    }

    private void initLogout(){
        Button logoutBtn = getActivity().findViewById(R.id.logout_test);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
//                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation_button);
//                bottomNavigationView.setVisibility(View.GONE);
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.main_view, new LoginFragment()).commit();
            }
        });
    }

    // TEST DATA !!! //
    private void setFoods() {
        Log.d("Test Data", foods.size() + " in set data");

//        for(Food food : foods){
//            firebaseFirestore.collection("restaurant")
//                    .document("name")
//                    .set(food).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//
//                }
//            });
//        }
    }

    private void loadFoodData(String restaurantName) {
        firebaseFirestore.collection("restaurant")
                .document(restaurantName)
                .collection("food")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        foods.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Food food = document.toObject(Food.class);
                            food.setKey(document.getId());
                            foods.add(food);
                            Log.d("Load Food Data", food.getKey());
                        }
                        foodAdapter.notifyDataSetChanged();
                    }
                });
    }

    // THIS FOR TEST UPLOAD !!! //
    private void testUpload() {
        // ปุ่มเลือกรูป
        Button pickFile = getActivity().findViewById(R.id.pick_file);
        pickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // สร้างหน้าต่างเลือกรูป
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        // ปุ่มอัพโหลด
        final Button upload = getActivity().findViewById(R.id.upload_button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String fileName = System.currentTimeMillis() + ".jpg";
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("upload"); // << folder
                StorageReference fileRef = storageRef.child(fileName); // << file name

                // Upload image file //
                fileRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                testUri(fileName);
                            }
                        });
            }
        });

        pickFile.setVisibility(View.GONE);
        upload.setVisibility(View.GONE);
    }

    // THIS FOR TEST UPLOAD !!! //
    private void testUri(final String fileName){
        StorageReference fimeUrl = FirebaseStorage.getInstance().getReference("upload/" + fileName);
        fimeUrl.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("Uri", "uri = " + uri.toString());
                Food food = new Food(fileName, "", 405, uri.toString());
                firebaseFirestore.collection("food")
                        .document(food.getName())
                        .set(food);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Uri", "uri = fail");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Toast.makeText(this.getContext(), imageUri + "", Toast.LENGTH_LONG).show();
        }
    }

    private String getRestaurantName(){
        Bundle bundle =  getArguments();
        if(bundle != null){
            return bundle.getString("restaurant");
        }
        return "null";
    }
}
