package com.example.elderassist.SubtaskRV;

import android.widget.CheckBox;

public class SubtaskItem {
    CheckBox subtask;

    public SubtaskItem(CheckBox subtask) {
        this.subtask = subtask;
    }

    public CheckBox getSubtask() {
        return subtask;
    }

    public void setSubtask(CheckBox subtask) {
        this.subtask = subtask;
    }
}
