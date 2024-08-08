package com.example.conftech.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conftech.R;
import com.example.conftech.activities.EventInDetails;
import com.example.conftech.models.EventModel;
import com.example.conftech.models.FavEventModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.UUID;

public class EventItemAdapter extends RecyclerView.Adapter<EventItemAdapter.EventViewHolder>{

    FirebaseFirestore db;

    private List<EventModel> eventModelList;
    Context context;

    SharedPreferences sharedPreferences;


    ProgressDialog progressDialog;

    public EventItemAdapter(Context context, List<EventModel> eventModelList) {
        this.eventModelList = eventModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventModel eventModel = eventModelList.get(position);
        holder.bind(eventModel);

        sharedPreferences = context.getSharedPreferences("Signin",Context.MODE_PRIVATE);


        if(sharedPreferences.getBoolean("Issignin",false))
        {
            if(sharedPreferences.getString("role","").equalsIgnoreCase("Organizer"))
            {
                holder.imgHeart.setVisibility(View.INVISIBLE);
            }else{
                holder.imgHeart.setVisibility(View.VISIBLE);
            }
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventInDetails.class);
                intent.putExtra("eventId", eventModel.getEventId().toString());
                context.startActivity(intent);
            }
        });

        holder.imgHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();


                String favId= UUID.randomUUID().toString();
                String userId = eventModel.getUserId().toString();
                String eventId = eventModel.getEventId().toString();
                String title = eventModel.getTitle().toString();
                String desc = eventModel.getDesc().toString();
                String date = eventModel.getDate().toString();
                String time = eventModel.getTime().toString();
                String type = eventModel.getType().toString();
                String price = eventModel.getPrice().toString();
                String location = eventModel.getLocation().toString();
                String meetingId = eventModel.getMeetingId().toString();
                String name=eventModel.getName().toString();

                FavEventModel favEventModel = new FavEventModel(favId,userId,eventId,title,desc,date,time,type,price,location,meetingId,name);

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);



                db.collection("fav-events").document(favId)
                        .set(favEventModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "fav added successfully.", Toast.LENGTH_SHORT).show();
                                holder.imgHeart.setImageResource(R.drawable.fill_heart);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "failed to add fav.", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }

    @Override
    public int getItemCount() {
        return eventModelList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle,txtDateTime,txtLocation,txtPrice,txtName;
        ImageView imageView,imgHeart;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDateTime = itemView.findViewById(R.id.txtDateTime);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imageView = itemView.findViewById(R.id.imageView);
            imgHeart = itemView.findViewById(R.id.imgHeart);
            txtName = itemView.findViewById(R.id.txtName);

        }

        public void bind(EventModel eventModel) {
            txtTitle.setText(txtTitle.getText().toString()+eventModel.getTitle().toString());
            txtDateTime.setText(txtDateTime.getText().toString()+eventModel.getDate().toString()+" "+eventModel.getTime().toString());
            txtLocation.setText(txtLocation.getText().toString()+eventModel.getLocation().toString());
            txtPrice.setText(txtPrice.getText().toString()+eventModel.getPrice().toString());
            txtName.setText(txtName.getText().toString()+eventModel.getName().toString());


        }
    }
}
