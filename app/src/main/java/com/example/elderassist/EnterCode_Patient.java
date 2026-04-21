package com.example.elderassist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class EnterCode_Patient extends AppCompatActivity {
    String patientCode;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.enter_code);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText enterCode = findViewById(R.id.enterCode);
        Button submitCode = findViewById(R.id.submitCode);
        patientCode = enterCode.getText().toString();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        submitCode.setOnClickListener(v -> {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fStore.collection("patients").document(patientCode);
        DocumentReference addUser = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> user = new HashMap<>();
                user.put("patientCode", documentSnapshot.getString("patientCode"));
                user.put("birth", documentSnapshot.getString("birth"));
                user.put("gender", documentSnapshot.getString("gender"));
                user.put("name", documentSnapshot.getString("name"));
                user.put("profile", documentSnapshot.getString("profile"));
                user.put("caregiverID", documentSnapshot.getString("caregiverID"));
                user.put("role", "patient");
                if (documentSnapshot.getString("claimStatus").equals("unclaimed")) {
                    documentReference.update("claimStatus", "claimed");
                    addUser.set(user);
                    Toast.makeText(EnterCode_Patient.this, "Patient claimed successfully.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EnterCode_Patient.this, ToDoList.class));
                    finish();
                } else {
                    Toast.makeText(EnterCode_Patient.this, "Patient already claimed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        });
    }
}