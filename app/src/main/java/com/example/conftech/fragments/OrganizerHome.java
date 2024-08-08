package com.example.conftech.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.conftech.R;
import com.example.conftech.activities.SignInActivity;
import com.example.conftech.activities.SignUpActivity;
import com.example.conftech.adapters.EventItemAdapter;
import com.example.conftech.models.EventModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;


public class OrganizerHome extends Fragment {

    FloatingActionButton fabAddEvents;
    BottomSheetDialog dialogView;

    TextView txtDate,txtTime;

    SharedPreferences sharedPreferences;

    String userId,title,desc,date,time,type,price,location,name;

    Spinner spnEvent;

    String[] eventType={"Online","Offline"};

    private FirebaseFirestore db;

    RecyclerView eventRecycler;

    List<EventModel> eventModelList;

    EventItemAdapter eventItemAdapter;

    ProgressDialog progressDialog;
    public OrganizerHome() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_home, container, false);

        sharedPreferences = getActivity().getSharedPreferences("Signin", Context.MODE_PRIVATE);

        fabAddEvents = view.findViewById(R.id.fabAddEvents);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


        db = FirebaseFirestore.getInstance();


        if(sharedPreferences.getBoolean("Issignin",false))
        {
            userId=sharedPreferences.getString("userId","");
            name=sharedPreferences.getString("fname","")+" "+sharedPreferences.getString("lname","");
        }

        fabAddEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddSeminarDialog();
            }
        });

        eventModelList = new ArrayList<>();

        fetchDataFromFirebase();

        eventRecycler=view.findViewById(R.id.eventRecycler1);
        eventRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        eventItemAdapter = new EventItemAdapter(getContext(),eventModelList);
        eventRecycler.setAdapter(eventItemAdapter);


        return view;
    }

    void fetchDataFromFirebase(){
        progressDialog.show();

        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            eventModelList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                EventModel eventModel = document.toObject(EventModel.class);
                                eventModelList.add(eventModel);

                            }
                            Toast.makeText(getContext(), "data fetched suceessfully.", Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();

                            eventItemAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getContext(), "failed to fetch data.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }
                });
    }

    private void showAddSeminarDialog() {
        dialogView = new BottomSheetDialog(requireContext());
        dialogView.setContentView(R.layout.event_add_dialog);

        TextInputEditText edtTitle = dialogView.findViewById(R.id.edtTitle);
        TextInputEditText edtDesc = dialogView.findViewById(R.id.edtDesc);
         txtDate = dialogView.findViewById(R.id.txtDate);
         txtTime = dialogView.findViewById(R.id.txtTime);
        TextInputEditText edtPrice = dialogView.findViewById(R.id.edtPrice);
        TextInputEditText edtLocation = dialogView.findViewById(R.id.edtLocation);
        spnEvent = dialogView.findViewById(R.id.spnEvent);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,eventType);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spnEvent.setAdapter(adapter);
        spnEvent.setSelection(0);


        txtTime.setOnClickListener(v -> showTimePicker(txtTime));
        txtDate.setOnClickListener(v -> showDatePicker(txtDate));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                title = edtTitle.getText().toString();
                desc = edtDesc.getText().toString();
                price = edtPrice.getText().toString();
                type = spnEvent.getSelectedItem().toString();
                date = txtDate.getText().toString();
                time = txtTime.getText().toString();
                location=edtLocation.getText().toString();

                if (title.isEmpty() || desc.isEmpty() || price.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty())
                {
                    Toast.makeText(getContext(), "please fill the required fields.", Toast.LENGTH_SHORT).show();
                }else{

                    Random random = new Random();

                    long meetId = 1000000000L + (long)(random.nextDouble() * (9999999999L - 1000000000L));

                    String meetingId = String.valueOf(meetId);

                    String eventId = UUID.randomUUID().toString();

                    EventModel eventModel = new EventModel(eventId,title,desc,date,time,price,type,meetingId,userId,location,name);

                    db.collection("events").document(eventId)
                            .set(eventModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "events added successfully.", Toast.LENGTH_SHORT).show();
                                    dialogView.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "failed to add event.", Toast.LENGTH_SHORT).show();
                                    dialogView.dismiss();
                                }
                            });
                }
            }
        });
        dialogView.show();
    }

    private void showDatePicker(TextView txtDate) {
        Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear);
                    txtDate.setText(formattedDate);

                    txtTime.setText(""); // Clear the time TextView

                },
                year,
                month,
                day
        );

        // Set minimum date to current date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();

    }

    private void showTimePicker(TextView txtTime) {
        if (TextUtils.isEmpty(txtDate.getText().toString())) {
            Snackbar.make(dialogView.findViewById(android.R.id.content), "Please select a date first", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Get the selected date
        String selectedDateStr = txtDate.getText().toString();
        Calendar selectedDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            selectedDate.setTime(dateFormat.parse(selectedDateStr));
        } catch (ParseException e) {
            e.printStackTrace();
            Snackbar.make(dialogView.findViewById(android.R.id.content), "Error parsing date", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Check if the selected date is today
        Calendar currentDate = Calendar.getInstance();
        boolean isToday = selectedDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                selectedDate.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH);

        Calendar currentTime = Calendar.getInstance();
        int hour, minute;

        if (isToday) {
            // If the selected date is today, set the initial time to the current time
            hour = currentTime.get(Calendar.HOUR_OF_DAY);
            minute = currentTime.get(Calendar.MINUTE);
        } else {
            // If the selected date is not today, set the initial time to the previously selected time or current time
            String selectedTimeStr = txtTime.getText().toString();
            if (!TextUtils.isEmpty(selectedTimeStr)) {
                // Parse the previously selected time
                try {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    Date selectedTimeDate = timeFormat.parse(selectedTimeStr);
                    Calendar selectedTimeCal = Calendar.getInstance();
                    selectedTimeCal.setTime(selectedTimeDate);
                    hour = selectedTimeCal.get(Calendar.HOUR_OF_DAY);
                    minute = selectedTimeCal.get(Calendar.MINUTE);
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Use current time if parsing fails
                    hour = currentTime.get(Calendar.HOUR_OF_DAY);
                    minute = currentTime.get(Calendar.MINUTE);
                }
            } else {
                // Use current time if no previously selected time
                hour = currentTime.get(Calendar.HOUR_OF_DAY);
                minute = currentTime.get(Calendar.MINUTE);
            }
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireActivity(),
                (view, hourOfDay, minuteOfDay) -> {
                    if (isToday && (hourOfDay < currentTime.get(Calendar.HOUR_OF_DAY) ||
                            (hourOfDay == currentTime.get(Calendar.HOUR_OF_DAY) && minuteOfDay <= currentTime.get(Calendar.MINUTE)))) {
                        // Selected time is in the past
                        Snackbar.make(dialogView.findViewById(android.R.id.content), "Cannot select past times", Snackbar.LENGTH_SHORT).show();
                    } else {
                        // Format the selected time and update the TextView
                        String amPm;
                        if (hourOfDay < 12) {
                            amPm = "AM";
                        } else {
                            amPm = "PM";
                            if (hourOfDay > 12) {
                                hourOfDay -= 12;
                            }
                        }

                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay, minuteOfDay, amPm);
                        txtTime.setText(formattedTime);
                    }
                },
                hour,
                minute,
                false // 24-hour format
        );

        timePickerDialog.show();
    }
}