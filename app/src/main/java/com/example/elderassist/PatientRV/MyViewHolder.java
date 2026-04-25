package com.example.elderassist.PatientRV;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderassist.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView patientImage;
    TextView patientName;
    TextView patientCodeDisplay;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        patientImage = itemView.findViewById(R.id.patientImage);
        patientName = itemView.findViewById(R.id.patientName);
        patientCodeDisplay = itemView.findViewById(R.id.patientCodeDisplay);
    }
}
