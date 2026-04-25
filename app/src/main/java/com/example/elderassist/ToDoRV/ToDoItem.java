package com.example.elderassist.ToDoRV;
public class ToDoItem {
    String task;
    String date;

    public ToDoItem(String date, String task) {
        this.date = date;
        this.task = task;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
