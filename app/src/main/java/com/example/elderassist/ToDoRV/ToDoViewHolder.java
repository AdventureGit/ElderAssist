package com.example.elderassist.ToDoRV;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderassist.R;

public class ToDoViewHolder extends RecyclerView.ViewHolder {
    TextView task;
    TextView date;

    public ToDoViewHolder(@NonNull View itemView) {
        super(itemView);
        task = itemView.findViewById(R.id.todoItem);
        date = itemView.findViewById(R.id.todoDate);
    }
}
