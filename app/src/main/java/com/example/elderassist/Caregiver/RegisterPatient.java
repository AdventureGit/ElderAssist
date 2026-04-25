package com.example.elderassist.Caregiver;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.elderassist.Authentication.PatientAuth.EnterCode_Patient;
import com.example.elderassist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPatient extends AppCompatActivity {

    FirebaseAuth mAuth;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register_patient);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText emailPatient = findViewById(R.id.emailPatient);
        EditText passPatient = findViewById(R.id.passPatient);
        Button regBtn = findViewById(R.id.regBtnPatient);
        mAuth = FirebaseAuth.getInstance();
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailPatient.getText().toString();
                String password = passPatient.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterPatient.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterPatient.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                                    userID = mAuth.getCurrentUser().getUid();
                                    if (userID != null) {
                                        DocumentReference addUser = fStore.collection("users").document(userID);
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("role", "patient");
                                        addUser.set(user).addOnSuccessListener(unused -> {
                                            Toast.makeText(RegisterPatient.this, "Account created successfully.",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterPatient.this, EnterCode_Patient.class);
                                            startActivity(intent);
                                            finish();
                                        }).addOnFailureListener(e -> {
                                            Toast.makeText(RegisterPatient.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                } else {
                                    // Display the actual error message from Firebase
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Authentication failed.";
                                    Toast.makeText(RegisterPatient.this, errorMessage,
                                            Toast.LENGTH_LONG).show();
                                    Log.e("FirebaseAuth", "Registration failed", task.getException());
                                }
                            }
                        });
            }
        });
    }

}