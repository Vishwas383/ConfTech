package com.example.conftech.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.ContextThemeWrapper;


import com.bumptech.glide.Glide;
import com.example.conftech.R;
import com.example.conftech.activities.SignInActivity;
import com.example.conftech.activities.SignUpActivity;
import com.example.conftech.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttendeeProfileFragment extends Fragment {

    String[] roleList={"Attendee","Organizer"};


    Spinner spinner;

    SharedPreferences sharedPreferences;

    FirebaseFirestore db;

    String userId,fname,lname,email,role,photo,password;

    TextInputEditText edtFname,edtLname,edtEmail;

    Button btnUpdate,btnLogout;

    CircleImageView profilePhoto;

    int SELECT_PICTURE = 100;

    Uri imageUri;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CROP = 3;

    ProgressDialog progressDialog;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    public AttendeeProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendee_profile, container, false);

        sharedPreferences = getActivity().getSharedPreferences("Signin", Context.MODE_PRIVATE);

        spinner=view.findViewById(R.id.spnRole);
        edtFname=view.findViewById(R.id.edtFname);
        edtLname=view.findViewById(R.id.edtLname);
        edtEmail=view.findViewById(R.id.edtEmail);
        btnUpdate=view.findViewById(R.id.btnUpdate);
        btnLogout=view.findViewById(R.id.btnLogout);
        profilePhoto=view.findViewById(R.id.profile_image);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().clear().apply();
                startActivity(new Intent(getContext(), SignInActivity.class));
                requireActivity().finish();
            }
        });

        ArrayAdapter adapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,roleList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        if(sharedPreferences.getBoolean("Issignin",false))
        {
            userId=sharedPreferences.getString("userId","");
        }

        db = FirebaseFirestore.getInstance();

        fetchUserDataFromFirebase(userId);

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_PICTURE);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fname=edtFname.getText().toString();
                lname=edtLname.getText().toString();

                if(fname.isEmpty() || lname.isEmpty())
                {
                    Toast.makeText(getContext(), "please fill the required fields.", Toast.LENGTH_SHORT).show();
                }else{
                    if (!fname.matches("^[a-zA-Z]{2,8}$")) {
                        Toast.makeText(getContext(), "invalid first name.", Toast.LENGTH_SHORT).show();

                    } else if (!lname.matches("^[a-zA-Z]{2,8}$")) {
                        Toast.makeText(getContext(), "invalid last name.", Toast.LENGTH_SHORT).show();

                    }else{

                        progressDialog.show();

                        if (imageUri != null) {

                            firebaseStorage = FirebaseStorage.getInstance();
                            storageReference = firebaseStorage.getReference().child("images/"+System.currentTimeMillis()+".jpg");

                            storageReference
                                    .putFile(imageUri)
                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(getContext(), "Images Upload Successfully.", Toast.LENGTH_SHORT).show();

                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Map<String, Object> user = new HashMap<>();
                                                        user.put("photo",uri.toString());
                                                        user.put("fname", fname);
                                                        user.put("lname", lname);

                                                        db.collection("users")
                                                                .document(userId)
                                                                .update(user)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(getContext(), "Profile Updated Successfully !!", Toast.LENGTH_SHORT).show();
                                                                            progressDialog.dismiss();

                                                                            sharedPreferences.edit().putString("fname",fname).apply();
                                                                            sharedPreferences.edit().putString("lname",lname).apply();
                                                                        } else {
                                                                            Toast.makeText(getContext(), "Failed To Update Profile !!", Toast.LENGTH_SHORT).show();
                                                                            progressDialog.dismiss();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }else{

                            Map<String, Object> user = new HashMap<>();
                            user.put("fname", fname);
                            user.put("lname", lname);

                            db.collection("users")
                                    .document(userId)
                                    .update(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Profile Updated Successfully !!", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();

                                                sharedPreferences.edit().putString("fname",fname).apply();
                                                sharedPreferences.edit().putString("lname",lname).apply();
                                            } else {
                                                Toast.makeText(getContext(), "Failed To Update Profile !!", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                        }

                    }
                }

            }
        });


        return  view;    }

    void fetchUserDataFromFirebase(String userId)
    {

        progressDialog.show();

        DocumentReference docRef = db.collection("users").document(userId);

        // Fetch data from the document
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UserModel userModel = document.toObject(UserModel.class);

                        if(userModel!=null)
                        {
                            fname=userModel.getFname().toString();
                            lname=userModel.getLname().toString();
                            email=userModel.getEmail().toString();
                            role=userModel.getRole().toString();
                            photo=userModel.getPhoto().toString();
                            password=userModel.getPassword().toString();

                            edtFname.setText(fname);
                            edtLname.setText(lname);
                            edtEmail.setText(email);

                            if(role.equalsIgnoreCase("Attendee"))
                            {
                                spinner.setSelection(0);
                                spinner.setEnabled(false);
                            }else{
                                spinner.setSelection(1);
                                spinner.setEnabled(false);
                            }

                            if (photo != null && !photo.isEmpty()) {
                                // Load the profile photo using Glide
                                Glide.with(getContext())
                                        .load(photo)
                                        .centerCrop()
                                        .into(profilePhoto);
                            } else {
                                // Set a default image when no image is found or not set
                                profilePhoto.setImageResource(R.drawable.profile);
                            }

                            progressDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(getContext(), "no data exists.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                } else {
                    Toast.makeText(getContext(), "failed to fetch data.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
                if (data != null) {
                    Uri imageUri = data.getData();

                    Intent cropIntent = new Intent("com.android.camera.action.CROP");
                    cropIntent.setDataAndType(imageUri, "image/*");
                    cropIntent.putExtra("aspectX", 1); // Set aspect ratio for the crop (optional)
                    cropIntent.putExtra("aspectY", 1); // Set aspect ratio for the crop (optional)
                    cropIntent.putExtra("outputX", 200); // Set output image width (optional)
                    cropIntent.putExtra("outputY", 200); // Set output image height (optional)
                    cropIntent.putExtra("return-data", true); // Set to true to get cropped image data

                    startActivityForResult(cropIntent, 200);
                } else {
                    Toast.makeText(getContext(), "No Image Selected.", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_OK && requestCode == 200) {
                if (data != null) {
                    profilePhoto.setImageURI(data.getData());
                    imageUri=data.getData();
                } else {
                    Toast.makeText(getContext(), "No Image Selected.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}