package com.g05.itkmitl.multioder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private User curUser;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;
    private Button btn_save;
    FirebaseUser firebaseUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        curUser = (User) getIntent().getSerializableExtra("curUser");


        final TextView pro_headername = (TextView) findViewById(R.id.profile_headername);
        final EditText pro_name = (EditText) findViewById(R.id.profile_name);
        final TextView pro_email = (TextView) findViewById(R.id.profile_email);
        final EditText pro_phone = (EditText) findViewById(R.id.profile_phone);
        btn_save = (Button) findViewById(R.id.btn_profile_save);
        progressBar = findViewById(R.id.progressBar);


        pro_headername.setText(curUser.name);
        pro_name.setText(curUser.name);
        pro_email.setText(mAuth.getCurrentUser().getEmail().toString());
        pro_phone.setText(curUser.phone);

        final LinearLayout link_passChange = findViewById(R.id.link_changepass);
        link_passChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassChangeDialog();
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = pro_name.getText().toString();
                String phoneStr = pro_phone.getText().toString();

                if (nameStr.isEmpty() || phoneStr.isEmpty()){
                    Toast.makeText(getApplicationContext(),"กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_LONG).show();
                }else{
                    setLoading(true);
                    User user = new User(nameStr, phoneStr);
                    mFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                            .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_LONG).show();
                                finish();
                            }else{
                                setLoading(false);
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });

    }


    private void setLoading(boolean isLoading) {
        if (isLoading) {
            btn_save.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btn_save.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showPassChangeDialog(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.confirm_pass_dialogbox, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText oldPassText = (EditText) mView.findViewById(R.id.inp_oldpass);
        final EditText newPassText = (EditText) mView.findViewById(R.id.inp_newpass);
        final EditText newRePassText = (EditText) mView.findViewById(R.id.inp_re_newpass);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                    }
                })
                .setNegativeButton("ยกเลิก",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });


        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

        alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(newPassText.length() < 6) {
                    Toast.makeText(getApplicationContext(), "รหัสผ่านใหม่ต้องยาวมากกว่า 5 ตัวอักษร", Toast.LENGTH_SHORT).show();
                    return;

                }else if (!(newPassText.getText().toString()).equals(newRePassText.getText().toString())){
                    Toast.makeText(getApplicationContext(), "รหัสผ่านไม่ตรงกัน", Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    String oldPass = oldPassText.getText().toString();
                    final String newPass = newPassText.getText().toString();

                    // ChangePass Process
                    firebaseUser = mAuth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(firebaseUser.getEmail(), oldPass);
                    firebaseUser.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("PassResult", "Password updated");
                                        Toast.makeText(getApplicationContext(), "เปลี่ยนรหัสผ่านเรียบร้อย", Toast.LENGTH_SHORT).show();
                                        alertDialogAndroid.dismiss();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "เปลี่ยนรหัสผ่านไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                                        Log.d("PassResult", "Password update Error!!");
                                        alertDialogAndroid.dismiss();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"กรอกรหัสผ่านเก่าไม่ถูกต้อง", Toast.LENGTH_LONG).show();
                            alertDialogAndroid.dismiss();
                        }
                    });

                }
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
