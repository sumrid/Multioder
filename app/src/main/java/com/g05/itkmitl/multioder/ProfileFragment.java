package com.g05.itkmitl.multioder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final EditText name = getView().findViewById(R.id.name_profile);
        final EditText address = getView().findViewById(R.id.address_profile);
        final EditText phone = getView().findViewById(R.id.phone_profile);

        mFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    name.setText(documentSnapshot.getString("name"));
                    address.setText(documentSnapshot.getString("address"));
                    phone.setText(documentSnapshot.getString("phone"));
                }else{
                }
            }
        });

        initSaveBtn();

    }

    private void initSaveBtn() {
        Button saveBtn = getView().findViewById(R.id.save_profile);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText name = getView().findViewById(R.id.name_profile);
                final EditText address = getView().findViewById(R.id.address_profile);
                final EditText phone = getView().findViewById(R.id.phone_profile);

                String nameStr = name.getText().toString();
                String addressStr = address.getText().toString();
                String phoneStr = phone.getText().toString();

                if (nameStr.isEmpty() || addressStr.isEmpty() || phoneStr.isEmpty()){
                    Toast.makeText(getActivity(),"Please enter your information", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(),"Working...", Toast.LENGTH_LONG).show();
                    User user = new User(nameStr, phoneStr, addressStr);
                    mFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                            .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getActivity(),"Saved Changes", Toast.LENGTH_LONG).show();
                                refreshPage();
                            }else{
                                Toast.makeText(getActivity(),task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }


            }
        });
    }

    private void refreshPage() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_view, new ProfileFragment())
                .addToBackStack(null).commit();
    }
}
