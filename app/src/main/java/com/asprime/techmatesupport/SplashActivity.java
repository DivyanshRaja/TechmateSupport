package com.asprime.techmatesupport;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.asprime.techmatesupport.databinding.ActivitySplashBinding;
import com.asprime.techmatesupport.utils.CommonUtils;
import com.asprime.techmatesupport.utils.PreferenceHandler;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    PreferenceHandler preferenceHandler;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* To change status bar color*/
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));

        /* View Binding start */
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        /* View Binding end */

        binding.versionNAme.setText("App Version : v." + CommonUtils.getVersionName(this));
        preferenceHandler = new PreferenceHandler(SplashActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* start splash animation */
        splashAnimation();

        /* Handler To start activity */
        new Handler().postDelayed(() -> {
            Intent intent;
            if(!preferenceHandler.getUser_name().equalsIgnoreCase("")){
                intent = new Intent(SplashActivity.this, CommonActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 3000);
    }

    /* Code for logo animation */
    private void splashAnimation() {
        Interpolator interpolator = this::getPowOut;
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.techMateImageLogo, "translationY", 0, 25, 0);
        animator.setInterpolator(interpolator);
        animator.setStartDelay(200);
        animator.setDuration(800);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    /* Get pow for logo bounce animation */
    private float getPowOut(float elapsedTimeRate) {
        return (float) ((float) 1 - Math.pow(1 - elapsedTimeRate, 3));
    }
}