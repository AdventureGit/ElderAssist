package com.example.elderassist;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreatePatient extends AppCompatActivity {
    FirebaseAuth mAuth;
    String genderChoice;
    RadioButton radioButton;
    FirebaseFirestore fStore;
    StorageReference storageRef;
    private Uri selectedImage;
    private DatePickerDialog datePick;
    String patientID;

    public String generatePatientCode() {
        String tempPatientID = "";
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 6; i++) {
            int randomIndex = (int) (Math.random() * chars.length());
            tempPatientID += chars.charAt(randomIndex);
        }
        return tempPatientID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_patient);

        // INITIALIZE FIREBASE INSTANCES
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText patientName = findViewById(R.id.patientName);
        EditText patientBirth = findViewById(R.id.patientBirth);
        Button profilePic = findViewById(R.id.profilePic);
        RadioGroup radioGender = findViewById(R.id.radioGender);
        Button submit = findViewById(R.id.submitBtn);

        patientBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                datePick = new DatePickerDialog(CreatePatient.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        patientBirth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePick.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patientID = generatePatientCode();
                int radioID = radioGender.getCheckedRadioButtonId();
                radioButton = findViewById(radioID);
                genderChoice = radioButton.getText().toString();

                DocumentReference documentReference = fStore.collection("patients").document(patientID);
                Map<String, Object> patient = new HashMap<>();
                String givenName = patientName.getText().toString();
                String givenBirth = patientBirth.getText().toString();
                
                patient.put("role", "patient");
                if (givenName.isEmpty()) {
                    patientName.setError("Name is required.");
                    return;
                }
                patient.put("name", givenName);
                if (givenBirth.isEmpty()) {
                    patientBirth.setError("Birth date is required.");
                    return;
                }
                patient.put("birth", givenBirth);
                if (genderChoice.isEmpty()) {
                    Toast.makeText(CreatePatient.this, "Please select a gender.", Toast.LENGTH_SHORT).show();
                }
                patient.put("gender", genderChoice);
                patient.put("profile", "NA");

                documentReference.set(patient).addOnSuccessListener(unused -> {
                    Toast.makeText(CreatePatient.this, "Profile created successfully.", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to the previous screen
                }).addOnFailureListener(e -> {
                    Toast.makeText(CreatePatient.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}