package com.g05.itkmitl.multioder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.g05.itkmitl.multioder.food.FoodListFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initloginBtn();
        initregisterLink();
        checkCurrentUser();
    }

    void checkCurrentUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null) {
            goToMenu();
        }
    }

    void initregisterLink() {
        TextView registerLink = getView().findViewById(R.id.register);
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new RegisterFragment())
                        .addToBackStack(null).commit();
            }
        });
    }

    void initloginBtn() {
        Button loginBtn = getView().findViewById(R.id.button_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailLogin = getView().findViewById(R.id.username);
                EditText passwordLogin = getView().findViewById(R.id.password);

                String emailStr = emailLogin.getText().toString();
                String passwordStr = passwordLogin.getText().toString();

                if (emailStr.isEmpty() || passwordStr.isEmpty()){
                    Toast.makeText(getActivity(), "Please enter your informations", Toast.LENGTH_SHORT).show();
                }else{
                    signIn(emailStr, passwordStr);
                }
            }
        });
    }

    void signIn(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Fail : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                checkVerified();
            }
        });
    }

    void checkVerified() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser().isEmailVerified()) {
            goToMenu();
        } else {
            firebaseAuth.signOut();
            Toast.makeText(getActivity(), "Please verify your email", Toast.LENGTH_SHORT).show();
        }
    }

    void goToMenu() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_view, new FoodListFragment())
                .addToBackStack(null).commit();
    }
}
