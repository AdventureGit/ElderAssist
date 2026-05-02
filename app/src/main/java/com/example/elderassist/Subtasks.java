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

import java.util.ArrayList;

public class Subtasks extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.todo_subtasks);

        RecyclerView subtasksRV = findViewById(R.id.subtaskRV);
        ArrayList<String> subtasks = getIntent().getStringArrayListExtra("subtasks");
        
        if (subtasks == null) {
            subtasks = new ArrayList<>();
        }

        subtasksRV.setLayoutManager(new LinearLayoutManager(this));
        SubtaskAdapter adapter = new SubtaskAdapter(this, subtasks);
        subtasksRV.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
