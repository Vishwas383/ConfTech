package com.example.conftech.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conftech.R;
import com.example.conftech.activities.EventInDetails;
import com.example.conftech.models.FavEventModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FavEventItemAdapter extends RecyclerView.Adapter<FavEventItemAdapter.FavEventViewHolder>{

    FirebaseFirestore db;

    private List<FavEventModel> favEventModelList;
    Context context;


    ProgressDialog progressDialog;

    public FavEventItemAdapter(Context context, List<FavEventModel> favEventModelList) {
        this.favEventModelList = favEventModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public FavEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new FavEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavEventViewHolder holder, int position) {
        FavEventModel favEventModel = favEventModelList.get(position);
        holder.bind(favEventModel);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventInDetails.class);
                intent.putExtra("eventId", favEventModel.getEventId().toString());
                context.startActivity(intent);
            }
        });

//        holder.imgHeart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                db = FirebaseFirestore.getInstance();
//
//
//                String favId= UUID.randomUUID().toString();
//
//
//                progressDialog = new ProgressDialog(context);
//                progressDialog.setMessage("Loading...");
//                progressDialog.setCancelable(false);
//
//                db.collection("fav-events").document(favId)
//                        .delete()
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(context, "fav deleted.", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(context, "failed to remove fav.", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return favEventModelList.size();
    }

    public static class FavEventViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle,txtDateTime,txtLocation,txtPrice,txtName;
        ImageView imageView,imgHeart;

        public FavEventViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDateTime = itemView.findViewById(R.id.txtDateTime);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imageView = itemView.findViewById(R.id.imageView);
            imgHeart = itemView.findViewById(R.id.imgHeart);
            txtName = itemView.findViewById(R.id.txtName);


        }

        public void bind(FavEventModel FavEventModel) {
            txtTitle.setText(txtTitle.getText().toString()+FavEventModel.getTitle().toString());
            txtDateTime.setText(txtDateTime.getText().toString()+FavEventModel.getDate().toString()+" "+FavEventModel.getTime().toString());
            txtLocation.setText(txtLocation.getText().toString()+FavEventModel.getLocation().toString());
            txtPrice.setText(txtPrice.getText().toString()+FavEventModel.getPrice().toString());
            txtName.setText(txtName.getText().toString()+FavEventModel.getName().toString());

            imgHeart.setImageResource(R.drawable.fill_heart);
        }
    }
}

