package com.example.elderassist.SubtaskRV;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderassist.R;

public class SubtaskViewHolder extends RecyclerView.ViewHolder {
    CheckBox subtask;

    public SubtaskViewHolder(@NonNull View itemView) {
        super(itemView);
        subtask = itemView.findViewById(R.id.subtaskCheckBox);
    }

    //subtask = itemView.findViewById(R.id.subtaskItem);


}
