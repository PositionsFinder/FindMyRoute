package com.github.positionsfinder.findmyroute;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class Splash extends AppCompatActivity {

    // Spalsh Variablen
    private static int SPLASH_TIME_OUT = 3000;
    private Handler handler;
    private Runnable runnable;

    private TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        txtView = (TextView) findViewById(R.id.textView);

        //ToDo: DB CONNECTION CHECK (in 3 Sekunden)
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                txtView.setText("Internet: " + internetConnection());
                Intent home_activity = new Intent(getApplicationContext(), MainActivity.class);
                if (!internetConnection()) {
                    home_activity.putExtra("Status", "Check Your Internet Connection.");
                }
                startActivity(home_activity);
                finish();
            }
        };
        handler.postDelayed(runnable, SPLASH_TIME_OUT);

    }

    // Check if user have Internet on Device.
    private boolean internetConnection() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return (null != ni);

    }
}
