package com.example.elderassist.PatientRV;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderassist.R;
import com.example.elderassist.ToDoList;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView patientImage;
    TextView patientName;
    TextView patientCodeDisplay;
    Button viewPatientBtn;
    FirebaseFirestore fStore;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        patientImage = itemView.findViewById(R.id.patientImage);
        patientName = itemView.findViewById(R.id.patientName);
        patientCodeDisplay = itemView.findViewById(R.id.patientCodeDisplay);
        viewPatientBtn = itemView.findViewById(R.id.viewPatient);

        viewPatientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String refPatientCode = patientCodeDisplay.getText().toString();
                fStore = FirebaseFirestore.getInstance();
                String patientCode;
                fStore.collection("users")
                        .whereEqualTo("patientCode", refPatientCode)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if(!queryDocumentSnapshots.isEmpty()){
                                Intent intent = new Intent(v.getContext(), ToDoList.class);
                                String patientID = queryDocumentSnapshots.getDocuments().get(0).getId();
                                intent.putExtra("patientID", patientID);
                                intent.putExtra("patientCode", refPatientCode);
                                v.getContext().startActivity(intent);
                            }
                            else{
                                Toast.makeText(itemView.getContext(), "Patient not claimed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
