package com.example.elderassist.Authentication;

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

import com.example.elderassist.Caregiver.PatientOverview_Caregiver;
import com.example.elderassist.Authentication.PatientAuth.EnterCode_Patient;
import com.example.elderassist.R;
import com.example.elderassist.ToDoList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        Button AuthUser = findViewById(R.id.authLoginBtn);
        EditText emailField = findViewById(R.id.inputEmailAddress);
        EditText passField = findViewById(R.id.inputPassword);

        AuthUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passField.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Login Successful.",
                                            Toast.LENGTH_SHORT).show();
                                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(mAuth.getCurrentUser().getUid());
                                    documentReference.get().addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {
                                                    String role = documentSnapshot.getString("role");
                                                    if (role.equals("caregiver")) {
                                                        Intent intent = new Intent(Login.this, ToDoList.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else if (role.equals("patient")) {
                                                        String patientStatus = documentSnapshot.getString("claimStatus");
                                                        if (patientStatus.equals("unclaimed")) {
                                                            startActivity(new Intent(Login.this, EnterCode_Patient.class));
                                                            finish();
                                                        } else if (patientStatus.equals("claimed")) {
                                                            startActivity(new Intent(Login.this, ToDoList.class));
                                                            finish();
                                                        } else {
                                                            return;
                                                        }
                                                    }
                                                }
                                    //Intent intent = new Intent(Login.this, PatientOverview_Caregiver.class);
                                    //startActivity(intent);
                                    finish();
                                });
                                }
                                else {
                                    // Show actual Firebase error
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed.";
                                    Toast.makeText(Login.this, errorMessage,
                                            Toast.LENGTH_LONG).show();
                                    Log.e("FirebaseAuth", "Login failed", task.getException());
                                }
                            }
                        });
            }
        });
    }
}