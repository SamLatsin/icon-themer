package com.sensustech.iconthemer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.sensustech.iconthemer.R;

public class PremiumActivity extends AppCompatActivity {
    private ConstraintLayout b_pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        final Animation anim = new ScaleAnimation(
                1f, 1.075f, // Start and end values for the X axis scaling
                1f, 1.075f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(900);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        b_pay = findViewById(R.id.b_pay);
        b_pay.setAnimation(anim);
    }

    public void continueLimitedClick(View view) {
        finish();
    }
}