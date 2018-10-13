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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initsignupBtn();
        inittoLogin();
        inittestSignout(); // DELETE DELETE THIS (TEST)
    }

    void inittestSignout() {  // DELETE DELETE THIS (TEST)
        TextView testSignout = getView().findViewById(R.id.testSignout);
        testSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                getFragmentManager().popBackStack();
            }
        });

    }

    void inittoLogin() {
        TextView toLogin = getView().findViewById(R.id.toLogin);
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginFragment();
            }
        });
    }

    void initsignupBtn() {
        Button signupBtn = getView().findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameReg = getView().findViewById(R.id.name_reg);
                EditText emailReg = getView().findViewById(R.id.email_reg);
                EditText passwordReg = getView().findViewById(R.id.passwor_reg);
                EditText repasswordReg = getView().findViewById(R.id.repassword_reg);

                String nameStr = nameReg.getText().toString();
                String emailStr = emailReg.getText().toString();
                String passwordStr = passwordReg.getText().toString();
                String repasswordStr = repasswordReg.getText().toString();

                if (nameStr.isEmpty() || emailStr.isEmpty() || passwordStr.isEmpty() || repasswordStr.isEmpty()){
                    Toast.makeText(getActivity(), "Please enter your informations", Toast.LENGTH_SHORT).show();
                }else if (!passwordStr.equals(repasswordStr)){
                    Toast.makeText(getActivity(), "Password is not match", Toast.LENGTH_SHORT).show();
                }else{
                    createAccount(emailStr, passwordStr);
                }

            }
        });
    }

    void createAccount(String user, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(user, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();
                sendVerifiedEmail(curUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void sendVerifiedEmail(final FirebaseUser _user) {
        _user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener < Void > () {
            @Override public void onSuccess(Void aVoid) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Toast.makeText(getActivity(), "Please verify your email", Toast.LENGTH_SHORT).show();
                goToLoginFragment();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void goToLoginFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.main_view, new LoginFragment())
                .addToBackStack(null).commit();
    }
}
