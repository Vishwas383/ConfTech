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
import com.example.conftech.adapters.FavEventItemAdapter;
import com.example.conftech.models.EventModel;
import com.example.conftech.models.FavEventModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class AttendeeFavFragment extends Fragment {

    RecyclerView eventRecycler;

    List<FavEventModel> favEventModelList;

    private FirebaseFirestore db;

    FavEventItemAdapter favEventItemAdapter;

    ProgressDialog progressDialog;


    public AttendeeFavFragment() {
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
        View view = inflater.inflate(R.layout.fragment_attendee_fav, container, false);

        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


        favEventModelList = new ArrayList<>();

        fetchDataFromFirebase();

        eventRecycler=view.findViewById(R.id.eventRecycler);
        eventRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        favEventItemAdapter = new FavEventItemAdapter(getContext(),favEventModelList);
        eventRecycler.setAdapter(favEventItemAdapter);

        return  view;
    }

    void fetchDataFromFirebase(){
        progressDialog.show();

        db.collection("fav-events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            favEventModelList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                FavEventModel favEventModel = document.toObject(FavEventModel.class);
                                favEventModelList.add(favEventModel);

                            }
                            Toast.makeText(getContext(), "data fetched suceessfully.", Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();

                            favEventItemAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getContext(), "failed to fetch data.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }
                });
    }
}