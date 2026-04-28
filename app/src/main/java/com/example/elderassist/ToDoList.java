package com.example.elderassist;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderassist.Caregiver.CreatePatient;
import com.example.elderassist.Caregiver.GenerateReport;
import com.example.elderassist.ToDoRV.ToDoAdapter;
import com.example.elderassist.ToDoRV.ToDoItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToDoList extends AppCompatActivity {
    List<ToDoItem> items = new ArrayList<ToDoItem>();
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
        String userID = mAuth.getCurrentUser().getUid();
        fstore.collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                            if (role == "patient") {
                                viewReportBtn.setVisibility(View.GONE);
                            }
                            else{
                                //viewReportBtn.setVisibility(View.GONE);
                            }
                    }
                        });

        RecyclerView recyclerView = findViewById(R.id.taskRV);
        //items.add(new ToDoItem("4/25/2026", "Go for a walk"));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(new ToDoAdapter(items, this));
        adapter = new ToDoAdapter(items, this);
        recyclerView.setAdapter(adapter);

        fstore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //String userID = mAuth.getCurrentUser().getUid();
        String patientID = getIntent().getStringExtra("patientID");
        fstore.collection("activities")
                        .whereEqualTo("patientID", patientID)
                        .addSnapshotListener((value, error) -> {
                                    items.clear();
                                    for (com.google.firebase.firestore.DocumentSnapshot doc : value) {
                                        items.add(new ToDoItem(doc.getString("date"), doc.getString("task")));
                                        adapter.notifyItemInserted(items.size() - 1);
                                    }
                                });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button createtaskBtn = findViewById(R.id.createtaskBtn);
        viewReportBtn = findViewById(R.id.viewReportBtn);
        //Button generateSubtasks = findViewById(R.id.generateSubtasks);
        EditText taskInfo = findViewById(R.id.taskInfo);
        EditText taskDate = findViewById(R.id.taskDate);
        ImageView taskDateSelect = findViewById(R.id.taskDateSelect);


        taskDateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                datePick = new DatePickerDialog(ToDoList.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        taskDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePick.show();
            }
        });

        createtaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = taskInfo.getText().toString();
                String date = taskDate.getText().toString();
                mAuth = FirebaseAuth.getInstance();
                String userID = mAuth.getCurrentUser().getUid();
                String patientID = getIntent().getStringExtra("patientID");
                if (!task.isEmpty() && !date.isEmpty()) {
                    fstore = FirebaseFirestore.getInstance();
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("task", task);
                    activity.put("date", date);
                    activity.put("status", "0");
                    activity.put("repeat", "no");
                    activity.put("caregiverID", userID);
                    activity.put("patientID", patientID);
                    fstore.collection("activities").add(activity).addOnSuccessListener(documentReference -> {
                        Toast.makeText(ToDoList.this, "Activity added successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(ToDoList.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
//                    items.add(new ToDoItem(date, task));
//                    adapter.notifyItemInserted(items.size() - 1);
                    taskInfo.setText("");
                    taskDate.setText("");
                }
                else{
                    Toast.makeText(ToDoList.this, "Please enter a task or date.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ToDoList.this, GenerateReport.class));
            }
        });
    }
}