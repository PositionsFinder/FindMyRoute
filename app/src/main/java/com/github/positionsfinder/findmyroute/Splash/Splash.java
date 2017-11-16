package com.github.positionsfinder.findmyroute.Splash;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.positionsfinder.findmyroute.Main.MainActivity;
import com.github.positionsfinder.findmyroute.R;


public class Splash extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 1500; // Splash time 1.5 Sec.

    // Handler and Runnable for setting the time.
    private Handler handler;
    private Runnable runnable;

    private TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the Actionbar.
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        txtView = (TextView) findViewById(R.id.textView);

        /* On start, Check Internet Connection,
         * if false send a Message to MainActivity to Check Internet Connection.
         */
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

    // Methode to Check the Internet Connection @Return True or False.
    private boolean internetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return (null != ni);

    }
}
