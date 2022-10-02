package com.example.homework06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    final static String PROGRESS_KEY = "PROGRESS";
    final static String VALUE_KEY = "VALUE";
    ExecutorService threadpool;
    Handler handler;

    Button buttonGenerate;
    TextView textViewProgress,textViewAverage,textViewComplexity;
    SeekBar seekBarComplexity;
    ProgressBar progressBarProgress;
    ListView listView;
    ArrayAdapter<Double> adapter;
    ArrayList<Double> generatedNumbers;
    public int complexity=1, i;
    int progress = 0;
    Double average = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        threadpool =  Executors.newFixedThreadPool(2);
        listView = findViewById(R.id.listView);
        buttonGenerate = findViewById(R.id.buttonGenerate);
        textViewAverage = findViewById(R.id.textViewAverage);
        textViewProgress = findViewById(R.id.textViewprogress);
        textViewComplexity = findViewById(R.id.textViewComplexity);
        seekBarComplexity = findViewById(R.id.seekBar);
        progressBarProgress = findViewById(R.id.progressBar);


        seekBarComplexity.setProgress(0);
        textViewComplexity.setText("1 Time");
        seekBarComplexity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                textViewComplexity.setText(String.valueOf(progress)+" Times");
                if(progress==0) {
                    textViewComplexity.setText("1 Time");
                    complexity = 1;
                }
                complexity = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        generatedNumbers = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,android.R.id.text1,generatedNumbers);
        listView.setAdapter(adapter);
        textViewAverage.setText("Average: "+average);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                average += message.getData().getDouble(VALUE_KEY)/message.getData().getInt(PROGRESS_KEY);
                textViewProgress.setText(String.valueOf(message.getData().getInt(PROGRESS_KEY)) + "/" + complexity);
                generatedNumbers.add(message.getData().getDouble(VALUE_KEY));
                adapter.notifyDataSetChanged();
                progressBarProgress.setProgress((message.getData().getInt(PROGRESS_KEY)));
                progressBarProgress.setMax(complexity);
                textViewAverage.setText("Average: "+average);
                Log.d("TAG", "handleMessage: "+message.getData().getInt(PROGRESS_KEY));
                return false;
            }
        });

        buttonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = 0;
                for ( i = 0; i < complexity; i++) {
                    threadpool.execute(new DoWork());
                }
            }
        });


    }
    class DoWork implements Runnable{


        @Override
        public void run() {
            progress++;
            Double number = HeavyWork.getNumber();
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt(PROGRESS_KEY,progress);
            bundle.putDouble(VALUE_KEY,number);
            msg.setData(bundle);
            handler.sendMessage(msg);

        }
    }
}
