package com.example.conftech.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.conftech.R;
import com.example.conftech.databinding.ActivitySignInBinding;
import com.example.conftech.databinding.ActivitySignUpBinding;
import com.example.conftech.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    String fname,lname,email,password,cpassword,role;

    String[] roleList={"Attendee","Organizer"};

    private FirebaseFirestore db;

    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


        binding.txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });



        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,roleList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        binding.spnRole.setAdapter(adapter);
        binding.spnRole.setSelection(0);

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fname = binding.edtFname.getText().toString();
                lname = binding.edtLname.getText().toString();
                email = binding.edtEmail.getText().toString();
                password = binding.edtPassword.getText().toString();
                cpassword = binding.edtCpassword.getText().toString();
                role = binding.spnRole.getSelectedItem().toString();

                if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || password.isEmpty() || cpassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "please fill the required fields.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!fname.matches("^[a-zA-Z]{2,8}$")) {
                        Toast.makeText(SignUpActivity.this, "invalid first name.", Toast.LENGTH_SHORT).show();

                    } else if (!lname.matches("^[a-zA-Z]{2,8}$")) {
                        Toast.makeText(SignUpActivity.this, "invalid last name.", Toast.LENGTH_SHORT).show();

                    } else if (!email.matches("[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}")) {
                        Toast.makeText(SignUpActivity.this, "invalid email.", Toast.LENGTH_SHORT).show();

                    } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
                        Toast.makeText(SignUpActivity.this, "invalid password.", Toast.LENGTH_SHORT).show();

                    } else if (!password.equals(cpassword)) {
                        Toast.makeText(SignUpActivity.this, "password and confirm password must be same.", Toast.LENGTH_SHORT).show();

                    } else {

                        int[] flag = {0};

                        progressDialog.show();

                        String userId = UUID.randomUUID().toString();

                        UserModel userModel = new UserModel(userId, fname, lname, email, role, password, "");

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
                                                        if (email.equalsIgnoreCase(user.getEmail().toString())) {
                                                            Toast.makeText(SignUpActivity.this, "email already exists.", Toast.LENGTH_SHORT).show();
                                                            flag[0] = 1;
                                                            progressDialog.dismiss();
                                                            break;
                                                        } else {
                                                            flag[0] = 0;
                                                        }
                                                    }
                                                }
                                            }

                                            if (flag[0] == 0) {
                                                db.collection("users").document(userId)
                                                        .set(userModel)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(SignUpActivity.this, "signup successfully.", Toast.LENGTH_SHORT).show();

                                                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                                                finish();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(SignUpActivity.this, "failed to signup.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }else{
                                            Toast.makeText(SignUpActivity.this, "failed to fetch data.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }
}

