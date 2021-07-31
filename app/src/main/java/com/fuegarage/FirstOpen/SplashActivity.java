package com.fuegarage.FirstOpen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.fuegarage.MainActivity;
import com.fuegarage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {


    private FirebaseAuth mMainAuth ;
    private FirebaseUser mMainCurrentUser ;

    ImageView  mainLogo ;
    Animation uptodown , downtoup;
    Button mSplashBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mainLogo = findViewById(R.id.splash_main_logo);
        mSplashBtn = findViewById(R.id.splash_lets_start_btn);

        uptodown = AnimationUtils.loadAnimation(SplashActivity.this , R.anim.up_to_down);
        downtoup = AnimationUtils.loadAnimation(SplashActivity.this , R.anim.down_to_up);


        mainLogo.setAnimation(uptodown);
        uptodown.setDuration(1000);
        uptodown.start();


        mSplashBtn.setAnimation(downtoup);
        downtoup.setDuration(1200);
        downtoup.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        /////////////// Check if user  not loged in ///////////////////
        mMainAuth = FirebaseAuth.getInstance();
        if(mMainAuth!=null){
            mMainCurrentUser = mMainAuth.getCurrentUser();
        }
        if(mMainCurrentUser!=null){
            startActivity(new Intent(SplashActivity.this , MainActivity.class));
            this.finish();
        }

    }

    public void letsStart(View view) {
        Intent intent =new Intent(this , LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}