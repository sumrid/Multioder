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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

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

    }

    private void inittoLogin() {
        TextView toLogin = getView().findViewById(R.id.toLogin);
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginFragment();
            }
        });
    }

    private void initsignupBtn() {
        Button signupBtn = getView().findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameReg = getView().findViewById(R.id.name_reg);
                EditText emailReg = getView().findViewById(R.id.email_reg);
                EditText passwordReg = getView().findViewById(R.id.passwor_reg);
                EditText repasswordReg = getView().findViewById(R.id.repassword_reg);
                EditText phoneReg = getView().findViewById(R.id.phone_reg);
                EditText addressReg = getView().findViewById(R.id.address_reg);

                String nameStr = nameReg.getText().toString();
                String emailStr = emailReg.getText().toString();
                String passwordStr = passwordReg.getText().toString();
                String repasswordStr = repasswordReg.getText().toString();
                String phoneStr = phoneReg.getText().toString();
                String addressStr = addressReg.getText().toString();

                if (nameStr.isEmpty() || emailStr.isEmpty() || passwordStr.isEmpty() || repasswordStr.isEmpty()
                        || phoneStr.isEmpty() || addressStr.isEmpty()){
                    Toast.makeText(getActivity(), "Please enter your informations", Toast.LENGTH_SHORT).show();
                }else if (!passwordStr.equals(repasswordStr)){
                    Toast.makeText(getActivity(), "Password is not match", Toast.LENGTH_SHORT).show();
                }else{
                    createAccount(emailStr, passwordStr, phoneStr, addressStr, nameStr);
                }

            }
        });
    }

    private void createAccount(String email, String password, final String phone, final String address, final String name) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getActivity(),"Please wait...", Toast.LENGTH_LONG).show();
                    User user = new User(name, phone, address);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getActivity(),"Register Complete", Toast.LENGTH_LONG).show();
                                goToLoginFragment();
                            }else{
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void goToLoginFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.main_view, new LoginFragment())
                .addToBackStack(null).commit();
    }
}
