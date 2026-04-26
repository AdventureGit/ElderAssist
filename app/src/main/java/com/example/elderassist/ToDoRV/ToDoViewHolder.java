package com.example.elderassist.ToDoRV;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderassist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    CheckBox isChecked;
    Button genSubtasks;

    public ToDoViewHolder(@NonNull View itemView) {
        super(itemView);
        task = itemView.findViewById(R.id.todoItem);
        date = itemView.findViewById(R.id.todoDate);
        genSubtasks = itemView.findViewById(R.id.generateSubtasks);

        genSubtasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient();
                // Build the prompt
                String prompt = "Break down this task into simple subtasks for an elderly person: "
                        + task.getText()
                        + ". Return ONLY a JSON array like: [{\"title\": \"subtask 1\"}, {\"title\": \"subtask 2\"}]. No other text.";

                // Build the request body
                String requestBody = "{"
                        + "\"model\": \"deepseek-chat\","
                        + "\"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]"
                        + "}";

                RequestBody body = RequestBody.create(
                        requestBody, MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url("https://api.deepseek.com/v1/chat/completions")
                        .header("Authorization", "sk-8882bfbff6b04137bc8b4b8fd3ba5aac")
                        .post(body)
                        .build();

                // Execute on background thread
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("DeepSeek", "Request failed: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();

                        try {
                            // Parse the response
                            JSONObject json = new JSONObject(responseBody);
                            String content = json.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");

                            // Parse the subtasks JSON array
                            JSONArray subtasksArray = new JSONArray(content);

                            // Save each subtask to Firestore
//                            runOnUiThread(() -> {
//                                for (int i = 0; i < subtasksArray.length(); i++) {
//                                    try {
//                                        String subtaskTitle = subtasksArray
//                                                .getJSONObject(i)
//                                                .getString("title");
//                                        saveSubtaskToFirestore(subtaskTitle, activityId);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });

                        } catch (JSONException e) {
                            Log.e("DeepSeek", "Parse error: " + e.getMessage());
                        }
                        // Add your OkHttp logic here
                    }
                });
            }
        });
    }
}
