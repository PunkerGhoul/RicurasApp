package com.example.ricurasapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.ricurasapp.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread logo = new Thread(){

            @Override
            public void run(){
                try{
                    int tiempo=0;
                    while(tiempo<5000){
                        sleep(100);
                        tiempo=tiempo+100;
                    }
                    Intent i=new Intent(IntroActivity.this, LoginActivity.class);
                    startActivity(i);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }finally{
                    finish();
                }
            }
        };

        logo.start();
    }
}