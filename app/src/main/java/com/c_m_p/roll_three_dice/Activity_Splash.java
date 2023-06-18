package com.c_m_p.roll_three_dice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
/**
 * <p style="color:yellow">
 * Hello World</p>
 */

public class Activity_Splash extends AppCompatActivity {
    ImageView dice_1, dice_2, dice_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        dice_1 = findViewById(R.id.dice_1);
        dice_2 = findViewById(R.id.dice_2);
        dice_3 = findViewById(R.id.dice_3);

        Animation animation_rotate_1 = AnimationUtils.loadAnimation(this, R.anim.anim_rotate_1);
        Animation animation_rotate_2 = AnimationUtils.loadAnimation(this, R.anim.anim_rotate_2);
        Animation animation_rotate_3 = AnimationUtils.loadAnimation(this, R.anim.anim_rotate_3);
        dice_1.startAnimation(animation_rotate_1);
        dice_2.startAnimation(animation_rotate_2);
        dice_3.startAnimation(animation_rotate_3);

        new CountDownTimer(2500, 500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(Activity_Splash.this, MainActivity.class));
                finish();
            }
        }.start();


    }
}