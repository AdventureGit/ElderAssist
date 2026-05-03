package com.example.elderassist;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderassist.SubtaskRV.SubtaskAdapter;
import com.example.elderassist.SubtaskRV.SubtaskItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Subtasks extends AppCompatActivity {
    FirebaseFirestore fstore;
    FirebaseAuth auth;
    ArrayList<SubtaskItem> subtasksList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.todo_subtasks);
        fstore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        RecyclerView subtasksRV = findViewById(R.id.subtaskRV);
//        ArrayList<String> subtasks = getIntent().getStringArrayListExtra("subtasks");
        String activityId = getIntent().getStringExtra("activityID");
//        if (subtasks == null) {
//            subtasks = new ArrayList<>();
//        }
//        HashMap<String, Object> subtask = new HashMap<>();
//        for (String task : subtasks) {
//            subtask.put("activityID", activityId);
//            subtask.put("task", task);
//        }
//
//

        subtasksRV.setLayoutManager(new LinearLayoutManager(this));
        SubtaskAdapter adapter = new SubtaskAdapter(this, subtasksList);
        subtasksRV.setAdapter(adapter);

        //fstore.collection("subtasks")
        if(activityId != null){
            fstore.collection("subtasks")
                    .whereEqualTo("activityID", activityId)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            return;
                        }
                        if (value != null) {
                            subtasksList.clear();
                            for (DocumentSnapshot doc : value) {
                                String status = doc.getString("status");
                                if (!"1".equals(status)){
                                    subtasksList.add(new SubtaskItem(doc.getString("task")));
                                    //subtasksList.add(doc.get("task").toString());
                                }
                                //subtasksList.add(doc.get("task").toString());
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
    }
}
