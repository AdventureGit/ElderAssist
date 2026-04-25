package com.example.elderassist.ToDoRV;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderassist.R;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoViewHolder> {
    Context context;
    List<ToDoItem> items;

    public ToDoAdapter(List<ToDoItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ToDoViewHolder(LayoutInflater.from(context).inflate(R.layout.todo_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoViewHolder holder, int position) {
        holder.task.setText(items.get(position).getTask());
        holder.date.setText(items.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
