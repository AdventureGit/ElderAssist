package com.example.elderassist.Caregiver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderassist.PatientRV.MyAdapter;
import com.example.elderassist.PatientRV.PatientItem;
import com.example.elderassist.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PatientOverview_Caregiver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.patient_overview);

        RecyclerView recyclerView = findViewById(R.id.patientList);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        List<PatientItem> items = new ArrayList<>();
        MyAdapter adapter = new MyAdapter(items, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

//        fstore.collection("patients").whereEqualTo("caregiverID", uid).get().addOnSuccessListener(queryDocumentSnapshots -> {
//            for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                items.add(new PatientItem(R.drawable.patient_placeholder_foreground, doc.getString("name"), doc.getString("patientCode")));
//            }
//        });
        //items
        //items.add(new PatientItem(R.drawable.patient_placeholder_foreground, "1234", "abcd"));
        fstore.collection("patients")
                        .whereEqualTo("caregiverID", uid)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            items.clear();
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    items.add(new PatientItem(R.drawable.patient_placeholder_foreground, doc.getString("name"), doc.getId()));
                                }
                            adapter.notifyDataSetChanged();
                        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button addPatient = findViewById(R.id.addPatient);

        addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientOverview_Caregiver.this, CreatePatient.class));
            }
        });
    }
}