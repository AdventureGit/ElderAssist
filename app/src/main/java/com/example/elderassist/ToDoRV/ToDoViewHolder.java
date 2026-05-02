package com.example.elderassist.ToDoRV;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderassist.R;
import com.example.elderassist.Subtasks;
import com.example.elderassist.ToDoList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ToDoViewHolder extends RecyclerView.ViewHolder {
    TextView task;
    TextView date;
    CheckBox taskCheckbox;
    Button genSubtasks;
    FirebaseAuth auth;
    FirebaseFirestore fstore;
    private static final OkHttpClient client = new OkHttpClient();

    public ToDoViewHolder(@NonNull View itemView) {
        super(itemView);
        task = itemView.findViewById(R.id.todoItem);
        date = itemView.findViewById(R.id.todoDate);
        genSubtasks = itemView.findViewById(R.id.generateSubtasks);
        taskCheckbox = itemView.findViewById(R.id.todoItem);

            taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    String activityInfo = task.getTag().toString();
                    fstore = FirebaseFirestore.getInstance();
                    fstore.collection("activities")
                            .whereEqualTo("task", activityInfo)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String activityId = task.getResult().getDocuments().get(0).getId();
                                    fstore.collection("activities").document(activityId).update("status", "1");
                                    Toast.makeText(itemView.getContext(), "Task completed", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("ToDoViewHolder", "Error getting documents: ", task.getException());
                                }

                            });
                } else {
                         return;
                }
                });

        genSubtasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(itemView.getContext(), "Generating subtasks...", Toast.LENGTH_SHORT).show();
                
                String taskText = task.getText().toString();
                String prompt = "Break down this task into simple subtasks for an elderly person. Make it as succinct yet as informative as possible and avoid information overload and unnecessary tasks. Task: "
                        + taskText
                        + ". Return ONLY a JSON array like: [{\"title\": \"subtask 1\"}, {\"title\": \"subtask 2\"}]. No other text."
                        + " Generate ONLY a maximum of 5 subtasks.";

                auth = FirebaseAuth.getInstance();

                try {
                    // Use JSONObject to build the request safely
                    JSONObject payload = new JSONObject();
                    payload.put("model", "deepseek-chat");
                    JSONArray messages = new JSONArray();
                    JSONObject message = new JSONObject();
                    message.put("role", "user");
                    message.put("content", prompt);
                    messages.put(message);
                    payload.put("messages", messages);

                    RequestBody body = RequestBody.create(
                            payload.toString(), MediaType.parse("application/json")
                    );

                    Request request = new Request.Builder()
                            .url("https://api.deepseek.com/v1/chat/completions")
                            .header("Authorization", "Bearer sk-8882bfbff6b04137bc8b4b8fd3ba5aac")
                            .post(body)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("DeepSeek", "Request failed: " + e.getMessage());
                            itemView.post(() -> Toast.makeText(itemView.getContext(), "Network Error", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseBody = response.body() != null ? response.body().string() : "";
                            
                            if (!response.isSuccessful()) {
                                Log.e("DeepSeek", "API Error: " + response.code() + " " + responseBody);
                                itemView.post(() -> Toast.makeText(itemView.getContext(), "API Error: " + response.code(), Toast.LENGTH_SHORT).show());
                                return;
                            }

                            try {

                                JSONObject json = new JSONObject(responseBody);
                                //String content;
                                String content = json.getJSONArray("choices")
                                        .getJSONObject(0)
                                        .getJSONObject("message")
                                        .getString("content");

                                if (content.contains("```")) {
                                    content = content.replaceAll("```json|```", "").trim();
                                }
                                List<String> subtasksHolder = new ArrayList<>();
                                JSONArray subtasksArray = new JSONArray(content);
//                                for(int i = 0; i < json.getJSONArray("choices").length(); i++){
//                                     content = json.getJSONArray("choices")
//                                            .getJSONObject(i)
//                                            .getJSONObject("message")
//                                            .getString("content");
//                                    subtasksHolder.add(content);
//                                }
                                for (int i = 0; i < subtasksArray.length(); i++) {
                                    subtasksHolder.add(subtasksArray.getJSONObject(i).getString("title"));
                                }


//                                if (content.contains("```")) {
//                                    content = content.replaceAll("```json|```", "").trim();
//                                }

                                //final String finalContent = content;
                                final List<String> finalContent = subtasksHolder;
                                itemView.post(() -> {
                                    Intent intent = new Intent(itemView.getContext(), Subtasks.class);
                                    intent.putStringArrayListExtra("subtasks", (ArrayList<String>) subtasksHolder);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    itemView.getContext().startActivity(intent);
                                });

                            } catch (JSONException e) {
                                Log.e("DeepSeek", "Parse error: " + e.getMessage());
                                itemView.post(() -> Toast.makeText(itemView.getContext(), "Parse Error", Toast.LENGTH_SHORT).show());

                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}