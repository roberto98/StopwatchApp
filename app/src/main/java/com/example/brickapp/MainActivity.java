package com.example.brickapp;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button startButton, resetButton, stopButton;
    private TextView timerTextView;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private Runnable beepRunnable;
    private long startTime = 0;
    private boolean isRunning = false;

    private ToneGenerator toneGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100); // Adjust volume as needed

        startButton = findViewById(R.id.startButton);
        resetButton = findViewById(R.id.resetButton);
        stopButton = findViewById(R.id.stopButton);
        timerTextView = findViewById(R.id.timerTextView);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    beep();
                    startTimer();
                    isRunning = true;
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning){
                    resetTimer();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                isRunning = false;
            }
        });
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                updateTimerTextView(elapsedTime);
                // Schedule the next update in 10 milliseconds
                timerHandler.postDelayed(this, 10);
            }
        };
        // Start updating the timer text view immediately
        timerHandler.post(timerRunnable);

        // Runnable to play beep every second
        beepRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime >= 1000) { // Start beeping after 1 second
                        beep();
                        // Schedule the next beep in 1000 milliseconds
                        timerHandler.postDelayed(this, 1000);
                    } else {
                        // Schedule the next beep check in 10 milliseconds
                        timerHandler.postDelayed(this, 10);
                    }
                }
            }
        };
        // Start beeping immediately
        timerHandler.post(beepRunnable);
    }

    private void resetTimer() {
        stopTimer(); // Stop any ongoing timer and beep
        startTime = System.currentTimeMillis();
        updateTimerTextView(0);
        if (isRunning) {
            startTimer(); // Restart timer if it was running
        }
    }

    private void stopTimer() {
        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }

        if (beepRunnable != null) {
            timerHandler.removeCallbacks(beepRunnable);
        }
    }

    private void updateTimerTextView(long elapsedTime) {
        int seconds = (int) ((elapsedTime) / 1000); // Add time offset
        int milliseconds = (int) ((elapsedTime) % 1000); // Add time offset
        String timeFormatted = String.format("%02d:%03d", seconds, milliseconds);
        timerTextView.setText(timeFormatted);
    }

    private void beep() {
        toneGenerator.startTone(ToneGenerator.TONE_SUP_ERROR, 35); // Play the beep for 150 ms
    }
}