package com.example.elderassist.Authentication.CaregiverAuth;

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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.elderassist.Caregiver.PatientOverview_Caregiver;
import com.example.elderassist.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class RegisterCaregiver_Details extends AppCompatActivity {
    FirebaseAuth mAuth;
    String genderChoice;
    RadioButton radioButton;
    FirebaseFirestore fStore;
    StorageReference storageRef;
    private Uri selectedImage;
    private DatePickerDialog datePick;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.caregiver_details);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText caregiverName = findViewById(R.id.caregiverName);
        EditText caregiverBirth = findViewById(R.id.caregiverBirth);
        RadioGroup radioGender = findViewById(R.id.radioGender);
        Button selectProfile = findViewById(R.id.selectProfile);
        Button createProfileBtn = findViewById(R.id.createProfileBtn);

        ActivityResultLauncher<String> pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        selectedImage = result;
                    }
                });


        selectProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage.launch("image/*");
                Toast.makeText(RegisterCaregiver_Details.this, "Selected image.", Toast.LENGTH_SHORT).show();
            }
        });

        caregiverBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                 datePick = new DatePickerDialog(RegisterCaregiver_Details.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        caregiverBirth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePick.show();
            }
        });

        createProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID = mAuth.getCurrentUser().getUid();
                int radioID = radioGender.getCheckedRadioButtonId();
                radioButton = findViewById(radioID);
                genderChoice = radioButton.getText().toString();

                DocumentReference documentReference = fStore.collection("users").document(userID);
                Map<String, Object> user = new HashMap<>();
                String givenName = caregiverName.getText().toString();
                String givenBirth = caregiverBirth.getText().toString();

                user.put("role", "caregiver");

                if (givenName.isEmpty()) {
                    caregiverName.setError("Name is required.");
                    return;
                }
                user.put("name", givenName);

                if (givenBirth.isEmpty()) {
                    caregiverBirth.setError("Birth date is required.");
                    return;
                }
                else{
                    user.put("birth", caregiverBirth.getText().toString());
                }
                user.put("birth", givenBirth);

                if (genderChoice.isEmpty()) {
                    Toast.makeText(RegisterCaregiver_Details.this, "Please select a gender.", Toast.LENGTH_SHORT).show();
                }
                else{
                    user.put("gender", genderChoice);
                }
                if (selectedImage != null) {
                    storageRef = FirebaseStorage.getInstance().getReference("profilePhotos/" + userID + ".jpg");
                    storageRef.putFile(selectedImage).addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            user.put("profile", downloadUri.toString());
                            documentReference.set(user).addOnSuccessListener(unused -> {
                                Toast.makeText(RegisterCaregiver_Details.this, "Profile created successfully.", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        });
                    }).addOnFailureListener(e -> Toast.makeText(RegisterCaregiver_Details.this, "Image Upload Failed.", Toast.LENGTH_SHORT).show());
                } else {
                    user.put("profile", "NA");
                    documentReference.set(user).addOnSuccessListener(unused -> {
                        Toast.makeText(RegisterCaregiver_Details.this, "Profile created successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterCaregiver_Details.this, PatientOverview_Caregiver.class);
                        finish();
                    });
                }
            }
        });
    }
}