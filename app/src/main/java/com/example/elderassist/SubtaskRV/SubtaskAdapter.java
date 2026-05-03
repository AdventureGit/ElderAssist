package com.example.elderassist.SubtaskRV;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderassist.R;

import java.util.ArrayList;

public class SubtaskAdapter extends RecyclerView.Adapter<SubtaskViewHolder> {
    Context context;
    ArrayList<SubtaskItem> items;

    public SubtaskAdapter(Context context, ArrayList<SubtaskItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public SubtaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubtaskViewHolder(LayoutInflater.from(context).inflate(R.layout.subtask_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubtaskViewHolder holder, int position) {
        SubtaskItem currentItem = items.get(position);
        holder.subtask.setText(currentItem.getTaskName());
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}
