package com.g05.itkmitl.multioder;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameReg, emailReg, passwordReg, repasswordReg, phoneReg;
    private TextInputLayout inpLayoutName, inpLayoutEmail, inpLayoutPassword,
            inpLayoutRepass, inpLayoutPhone, inpLayoutAddress;
    private String nameStr, emailStr, passStr, repassStr, phoneStr, addressStr;

    Button sigupBtn;
    private ProgressBar progressBar;


    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.parseColor("#00000000"));
//        }


        final Toolbar toolbar = (Toolbar) findViewById(R.id.regis_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        nameReg = (EditText) findViewById(R.id.inp_name);
        emailReg = (EditText) findViewById(R.id.inp_email);
        passwordReg = (EditText) findViewById(R.id.inp_password);
        repasswordReg = (EditText) findViewById(R.id.inp_repass);
        phoneReg = (EditText) findViewById(R.id.inp_phone);

        passwordReg.setTransformationMethod(new PasswordTransformationMethod());
        repasswordReg.setTransformationMethod(new PasswordTransformationMethod());

        inpLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inpLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inpLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_pass);
        inpLayoutRepass = (TextInputLayout) findViewById(R.id.input_layout_repass);
        inpLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);

        progressBar = findViewById(R.id.progressBar);

        nameReg.addTextChangedListener(new MyTextWatcher(nameReg));
        emailReg.addTextChangedListener(new MyTextWatcher(emailReg));
        passwordReg.addTextChangedListener(new MyTextWatcher(passwordReg));
        repasswordReg.addTextChangedListener(new MyTextWatcher(repasswordReg));
        phoneReg.addTextChangedListener(new MyTextWatcher(phoneReg));
        final ImageView appLogo = (ImageView) findViewById(R.id.app_logo);

        sigupBtn = (Button) findViewById(R.id.button_register);
        sigupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateName() || !validateEmail() || !validatePassword() || !validateRePassword()) {
                    return;
                }


                phoneStr = phoneReg.getText().toString();
                createAccount(emailStr, passStr, phoneStr, nameStr);
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


    private void saveUserInfo(String name, String phone){
        User user = new User(name, phone);
        firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser()
                .getUid()).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("saveUserInfo", "Success!!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void createAccount(String email, String password, final String phone, final String name) {
        setLoading(true);

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {

            @Override
            public void onSuccess(AuthResult authResult) {
                saveUserInfo(name, phone);
                Toast.makeText(getApplicationContext(), "สมัครสมาชิกสำเร็จ", Toast.LENGTH_LONG).show();
                mAuth.getCurrentUser();
                mAuth.signOut();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("RegisterResult", e.getMessage());
                setLoading(false);
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }


    private boolean validateName() {
        nameStr = nameReg.getText().toString();
        if (nameStr.trim().isEmpty()) {
            inpLayoutName.setError("กรอกชื่อ-นามสกุล");
            requestFocus(nameReg);
            return false;
        } else {
            inpLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        passStr = passwordReg.getText().toString();
        if (passStr.trim().isEmpty()) {
            inpLayoutPassword.setError("กรอกรหัสผ่าน");
            requestFocus(passwordReg);
            return false;
        } else if (passStr.length() < 6) {
            inpLayoutPassword.setError("รหัสผ่านต้องมากกว่า 5 ตัวอักษร");
            requestFocus(passwordReg);
        } else {
            inpLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateRePassword() {
        repassStr = repasswordReg.getText().toString();
        if (repassStr.trim().isEmpty()) {
            inpLayoutRepass.setError("กรอกรหัสผ่านยืนยัน");
            requestFocus(repasswordReg);
            return false;
        } else if (passStr == null || !passStr.equals(repassStr)) {
            inpLayoutRepass.setError("กรอกรหัสผ่านไม่ตรงกัน");
            requestFocus(repasswordReg);
            return false;
        } else {
            inpLayoutRepass.setErrorEnabled(false);
        }
        return true;

    }

    private boolean validateEmail() {
        emailStr = emailReg.getText().toString();
        if (emailStr.isEmpty() || !isValidEmail(emailStr)) {
            inpLayoutEmail.setError("กรอกอีเมล์ไม่ถูกต้อง");
            requestFocus(emailReg);
            return false;
        } else {
            inpLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            sigupBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            sigupBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.inp_name:
                    validateName();
                    break;
                case R.id.inp_email:
                    validateEmail();
                    break;
                case R.id.inp_password:
                    validatePassword();
                    break;
                case R.id.inp_repass:
                    validateRePassword();
                    break;
            }
        }
    }
}
