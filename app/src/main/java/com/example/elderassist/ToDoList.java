package com.example.elderassist;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.elderassist.ToDoRV.ToDoAdapter;
import com.example.elderassist.ToDoRV.ToDoItem;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ToDoList extends AppCompatActivity {
    List<ToDoItem> items = new ArrayList<ToDoItem>();
    private DatePickerDialog datePick;
    private ToDoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.todo_list);

        RecyclerView recyclerView = findViewById(R.id.taskRV);

        //items.add(new ToDoItem("4/25/2026", "Go for a walk"));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(new ToDoAdapter(items, this));
        adapter = new ToDoAdapter(items, this);
        recyclerView.setAdapter(adapter);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button createtaskBtn = findViewById(R.id.createtaskBtn);
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
                if (!task.isEmpty() && !date.isEmpty()) {
                    items.add(new ToDoItem(date, task));
                    adapter.notifyItemInserted(items.size() - 1);
                    taskInfo.setText("");
                    taskDate.setText("");
                }
                else{
                    Toast.makeText(ToDoList.this, "Please enter a task or date.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}