package com.g05.itkmitl.multioder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private User curUser;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        curUser = (User) getIntent().getSerializableExtra("curUser");

        ImageButton back_btn = (ImageButton) findViewById(R.id.btn_back_profile);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        final TextView pro_headername = (TextView) findViewById(R.id.profile_headername);
        final EditText pro_name = (EditText) findViewById(R.id.profile_name);
        final TextView pro_email = (TextView) findViewById(R.id.profile_email);
        final EditText pro_phone = (EditText) findViewById(R.id.profile_phone);
        final EditText pro_address = (EditText) findViewById(R.id.profile_address);
        final Button btn_save = (Button) findViewById(R.id.btn_profile_save);


        pro_headername.setText(curUser.name);
        pro_name.setText(curUser.name);
        pro_email.setText(mAuth.getCurrentUser().getEmail().toString());
        pro_phone.setText(curUser.phone);
        pro_address.setText(curUser.address);



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = pro_name.getText().toString();
                String addressStr = pro_address.getText().toString();
                String phoneStr = pro_phone.getText().toString();

                if (nameStr.isEmpty() || addressStr.isEmpty() || phoneStr.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter your information", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Working...", Toast.LENGTH_LONG).show();
                    User user = new User(nameStr, phoneStr, addressStr);
                    mFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                            .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Saved Changes", Toast.LENGTH_LONG).show();
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });





    }
}
