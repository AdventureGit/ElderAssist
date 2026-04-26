package com.example.elderassist.Caregiver;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.elderassist.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class GenerateReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.generate_report);
        PieChart pieChart = findViewById(R.id.progressPieChart);
        pieChart.setUsePercentValues(true);

        //entries
        ArrayList<PieEntry> entries = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            float value = (float) (i*10.0);
            entries.add(new PieEntry(value, "Task " + i));

        }
        PieDataSet dataSet = new PieDataSet(entries, "Tasks");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setData(new PieData(dataSet));
        pieChart.animateXY(5000, 5000);
        pieChart.getDescription().setEnabled(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}