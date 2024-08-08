package com.example.conftech.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.conftech.R;
import com.example.conftech.adapters.EventItemAdapter;
import com.example.conftech.models.EventModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttendeeUpcoming extends Fragment {


    RecyclerView eventRecycler;

    List<EventModel> eventModelList;

    private FirebaseFirestore db;

    EventItemAdapter eventItemAdapter;

    ProgressDialog progressDialog;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendee_upcoming, container, false);

        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


        eventModelList = new ArrayList<>();

        fetchDataFromFirebase();

        eventRecycler=view.findViewById(R.id.eventRecycler);
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
}