package com.quockhanhng.training.insta.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quockhanhng.training.insta.R;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUsername, edtFullName, edtEmail, edtPassword;
    Button btnRegister;
    TextView txtLogin;

    FirebaseAuth auth;
    DatabaseReference ref;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtRegisterUsername);
        edtFullName = findViewById(R.id.edtRegisterFullName);
        edtEmail = findViewById(R.id.edtRegisterEmail);
        edtPassword = findViewById(R.id.edtRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        auth = FirebaseAuth.getInstance();

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                String username = edtUsername.getText().toString();
                String fullName = edtFullName.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "All fields required", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                } else if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must have at least 6 characters ", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                } else {
                    register(username, fullName, email, password);
                }
            }
        });
    }

    private void register(final String username, final String fullName, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            String uid = user.getUid();
                            ref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", uid);
                            hashMap.put("username", username);
                            hashMap.put("fullName", fullName);
                            hashMap.put("bio", "");
                            hashMap.put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/insta-af499.appspot.com/o/icon.png?alt=media&token=1c34fcc7-5361-4a4f-b4d5-b18dae11a3bb");

                            ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
