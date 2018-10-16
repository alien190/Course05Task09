package com.example.alien.course05task09;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private CircleProgressBar mCircleProgressBar;
    private Button mButton;
    private Disposable mDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCircleProgressBar = findViewById(R.id.progressBar);
        mButton = findViewById(R.id.btnStart);
        mButton.setOnClickListener(this::startProgress);
    }

    private void startProgress(View view) {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        mCircleProgressBar.setValue(0);
        mDisposable = Observable.interval(50, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setProgressValue, Throwable::printStackTrace);
    }

    private void setProgressValue(Long value) {
        mCircleProgressBar.setValue(value.intValue());
        if (value >= 100) {
            mDisposable.dispose();
        }
    }
}
