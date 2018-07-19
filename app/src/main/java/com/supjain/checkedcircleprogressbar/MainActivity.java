package com.supjain.checkedcircleprogressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckedCircleProgressBar progressBar = findViewById(R.id.compatibility_checkedcircle_progressbar);
        progressBar.setProgressCount(2);
        progressBar.setTotalStepCount(7);
    }
}
