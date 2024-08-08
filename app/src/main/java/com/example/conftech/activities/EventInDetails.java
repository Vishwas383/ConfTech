package com.example.conftech.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.conftech.databinding.ActivityEventInDetailsBinding;
import com.example.conftech.models.EventModel;
import com.example.conftech.models.TicketModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class EventInDetails extends AppCompatActivity implements PaymentResultListener {

    ActivityEventInDetailsBinding binding;

    EventModel eventModel;

    Intent intent;

    String eventId,userId,name,email,price,title,location,date,time;
    private FirebaseFirestore db;

    ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventInDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent=getIntent();
        eventId=intent.getStringExtra("eventId");


        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        fetchDataFromFirebase();

        sharedPreferences = getSharedPreferences("Signin",MODE_PRIVATE);

        if(sharedPreferences.getBoolean("Issignin",false))
        {
            userId=sharedPreferences.getString("userId","");
            email=sharedPreferences.getString("email","");
            name=sharedPreferences.getString("fname","")+ " "+sharedPreferences.getString("lname","");
        }

        binding.btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRazorpayPayment();
            }
        });

        binding.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }

    private void redirectToBillGeneratedActivity() {

    }

    private void startRazorpayPayment() {

        Double amountInPaisa = (Double.parseDouble(price) * 100);


        if (amountInPaisa < 100) {
            Toast.makeText(this, "invalid event price.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize Razorpay Checkout
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_XRlrZ7yCa9RTyJ");

        // Create an order request with necessary details
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Conftech");
            options.put("description", "Event Ticket");
            options.put("currency", "INR");
            options.put("amount", amountInPaisa);
            options.put("prefill.email", email);
//            options.put("prefill.contact", "7048777558");
            checkout.open(this, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String s) {
        binding.btnBuy.setVisibility(View.GONE);
        binding.btnDownload.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Payment Successful.", Toast.LENGTH_SHORT).show();

        // Create a string containing user details
        String userDetails = String.format(
                "EventTitle: %s\nUserID: %s\nEventLocation: %s\nEventPrice: %s\nEventDate: %s\nEventTime: %s",
                title,
                userId,
                location,
                price,
                date,
                time
        );

        // Generate a QR code with user details
        Bitmap qrCodeBitmap = generateQrCode(userDetails);

        // Save the bitmap as a JPEG file
        String fileName = "qr_code_" + System.currentTimeMillis() + ".jpeg";
        File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);

        try {
            FileOutputStream out = new FileOutputStream(filePath);
            qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            Toast.makeText(this, "ticker qr code generated successfully.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "failed to generate ticker qr code.", Toast.LENGTH_SHORT).show();
        }



        saveTicketDataInFirebase();
    }

    private void saveTicketDataInFirebase() {

        String ticketId= UUID.randomUUID().toString();

        TicketModel ticketModel = new TicketModel(ticketId,userId,eventId);

        db.collection("tickets").document(ticketId)
                .set(ticketModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EventInDetails.this, "Ticket data saved successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EventInDetails.this, "failed to save ticket data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPaymentError(int errorCode, String errorMessage) {
        Toast.makeText(this, "payment failed.", Toast.LENGTH_SHORT).show();
    }

    private Bitmap generateQrCode(String data) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 300, 300);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void fetchDataFromFirebase() {

        progressDialog.show();


        DocumentReference docRef = db.collection("events").document(eventId);

        // Fetch the document
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        eventModel = document.toObject(EventModel.class);

                        if(eventModel!=null)
                        {

                            binding.txtName.setText(eventModel.getName().toString());
                            binding.txtLocation.setText(binding.txtLocation.getText().toString()+eventModel.getLocation().toString());
                            binding.txtDate.setText(eventModel.getDate().toString());
                            binding.txtTime.setText(eventModel.getTime().toString());
                            binding.txtTitle.setText(eventModel.getTitle().toString());
                            binding.txtDesc.setText(eventModel.getDesc().toString());
                            binding.txtPrice.setText(binding.txtPrice.getText().toString()+eventModel.getPrice().toString());

                            price=eventModel.getPrice().toString();
                            location=eventModel.getLocation().toString();
                            date=eventModel.getDate().toString();
                            time=eventModel.getTime().toString();
                            title=eventModel.getTitle().toString();

                            progressDialog.dismiss();

                        }
                    } else {
                        Toast.makeText(EventInDetails.this, "no data exists.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(EventInDetails.this, "failed to fetch data.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

    }
}