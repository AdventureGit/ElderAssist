package com.example.elderassist;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterPatient extends AppCompatActivity {

    FirebaseAuth mAuth;
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
                                    Toast.makeText(RegisterPatient.this, "Account created successfully.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterPatient.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
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