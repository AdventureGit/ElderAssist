package com.example.elderassist;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderassist.Caregiver.GenerateReport;
import com.example.elderassist.ToDoRV.ToDoAdapter;
import com.example.elderassist.ToDoRV.ToDoItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToDoList extends AppCompatActivity {
    List<ToDoItem> items = new ArrayList<>();
    private DatePickerDialog datePick;
    private ToDoAdapter adapter;
    FirebaseFirestore fstore;
    FirebaseAuth mAuth;
    Button viewReportBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.todo_list);
        
        viewReportBtn = findViewById(R.id.viewReportBtn);
        fstore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        
        if (mAuth.getCurrentUser() != null) {
            String userID = mAuth.getCurrentUser().getUid();
            fstore.collection("users")
                    .document(userID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            if ("patient".equals(role)) {
                                viewReportBtn.setVisibility(View.GONE);
                            }
                        }
                    });
        }

        RecyclerView recyclerView = findViewById(R.id.taskRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ToDoAdapter(items, this);
        recyclerView.setAdapter(adapter);

        String patientID = getIntent().getStringExtra("patientID");
        if (patientID != null) {
            fstore.collection("activities")
                    .whereEqualTo("patientID", patientID)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            return;
                        }
                        if (value != null) {
                            items.clear();
                            for (DocumentSnapshot doc : value) {
                                String taskStatus = doc.getString("status");
                                // Safe comparison to avoid NullPointerException
                                if (!"1".equals(taskStatus)) {
                                    items.add(new ToDoItem(doc.getString("date"), doc.getString("task")));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button createtaskBtn = findViewById(R.id.createtaskBtn);
        EditText taskInfo = findViewById(R.id.taskInfo);
        EditText taskDate = findViewById(R.id.taskDate);
        ImageView taskDateSelect = findViewById(R.id.taskDateSelect);
        //Button ttsBtn = findViewById(R.id.ttsBtn);


        taskDateSelect.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            datePick = new DatePickerDialog(ToDoList.this, (view, year1, month1, dayOfMonth) -> 
                    taskDate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
            datePick.show();
        });

        createtaskBtn.setOnClickListener(v -> {
            String task = taskInfo.getText().toString();
            String date = taskDate.getText().toString();
            String patientId = getIntent().getStringExtra("patientID");
            
            if (mAuth.getCurrentUser() != null && !task.isEmpty() && !date.isEmpty()) {
                String userID = mAuth.getCurrentUser().getUid();
                Map<String, Object> activity = new HashMap<>();
                activity.put("task", task);
                activity.put("date", date);
                activity.put("status", "0");
                activity.put("repeat", "no");
                activity.put("subtasks", "-1");
                activity.put("caregiverID", userID);
                activity.put("patientID", patientId);
                
                fstore.collection("activities").add(activity).addOnSuccessListener(documentReference -> {
                    Toast.makeText(ToDoList.this, "Activity added successfully", Toast.LENGTH_SHORT).show();
                    taskInfo.setText("");
                    taskDate.setText("");
                }).addOnFailureListener(e -> Toast.makeText(ToDoList.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(ToDoList.this, "Please enter a task or date.", Toast.LENGTH_SHORT).show();
            }
        });

        viewReportBtn.setOnClickListener(v -> startActivity(new Intent(ToDoList.this, GenerateReport.class)));

//        ttsBtn.setOnClickListener(v -> {
//
//        });

    }
    private void ttsCall(List<String> taskInput){

    }
}