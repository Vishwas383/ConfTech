package com.example.conftech.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.conftech.databinding.ActivitySignInBinding;
import com.example.conftech.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;


public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    String[] roleList = {"Attendee", "Organizer"};


    String email, password;

    private FirebaseFirestore db;

    SharedPreferences sharedPreferences;

    private FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        db = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();


        sharedPreferences = getSharedPreferences("Signin", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("Issignin", false)) {
            if (sharedPreferences.getString("role", "").equalsIgnoreCase("Attendee")) {
                startActivity(new Intent(SignInActivity.this, AttendeeDashboard.class));
                finish();
            } else {
                startActivity(new Intent(SignInActivity.this, OrganizerDashboard.class));
                finish();
            }
        }


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, roleList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        binding.txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });

        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = binding.edtEmail.getText().toString();
                password = binding.edtPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "please fill the required fields.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!email.matches("[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}")) {
                        Toast.makeText(SignInActivity.this, "invalid email.", Toast.LENGTH_SHORT).show();
                    } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
                        Toast.makeText(SignInActivity.this, "invalid password.", Toast.LENGTH_SHORT).show();
                    } else {

                        progressDialog.show();

                        int[] flag = {0};

                        db.collection("users")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                if (document.exists()) {
                                                    UserModel user = document.toObject(UserModel.class);

                                                    if (user != null) {
                                                        if (email.equalsIgnoreCase(user.getEmail().toString()) && password.equalsIgnoreCase(user.getPassword().toString())) {
                                                            if (user.getRole().toString().equalsIgnoreCase("Attendee")) {

                                                                flag[0] = 1;

                                                                Toast.makeText(SignInActivity.this, "sign in successfully.", Toast.LENGTH_SHORT).show();

                                                                sharedPreferences.edit().putString("userId", user.getUserId()).apply();
                                                                sharedPreferences.edit().putString("fname", user.getFname()).apply();
                                                                sharedPreferences.edit().putString("lname", user.getLname()).apply();
                                                                sharedPreferences.edit().putString("email", user.getEmail()).apply();
                                                                sharedPreferences.edit().putString("role", user.getRole()).apply();

                                                                sharedPreferences.edit().putBoolean("Issignin", true).apply();

                                                                startActivity(new Intent(SignInActivity.this, AttendeeDashboard.class));
                                                                finish();

                                                                progressDialog.dismiss();
                                                            } else {
                                                                flag[0] = 1;

                                                                Toast.makeText(SignInActivity.this, "sign in successfully.", Toast.LENGTH_SHORT).show();

                                                                sharedPreferences.edit().putString("userId", user.getUserId()).apply();
                                                                sharedPreferences.edit().putString("fname", user.getFname()).apply();
                                                                sharedPreferences.edit().putString("lname", user.getLname()).apply();
                                                                sharedPreferences.edit().putString("email", user.getEmail()).apply();
                                                                sharedPreferences.edit().putString("role", user.getRole()).apply();

                                                                sharedPreferences.edit().putBoolean("Issignin", true).apply();

                                                                startActivity(new Intent(SignInActivity.this, OrganizerDashboard.class));
                                                                finish();


                                                                progressDialog.dismiss();

                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                        }else {
                                            Toast.makeText(SignInActivity.this, "failed to fetch data.", Toast.LENGTH_SHORT).show();

                                            progressDialog.dismiss();
                                        }

                                        if (flag[0] == 0) {
                                            Toast.makeText(SignInActivity.this, "invalid credentials.", Toast.LENGTH_SHORT).show();

                                            progressDialog.dismiss();

                                        }
                                    }
                                });
                    }
                }
            }
        });
    }
}