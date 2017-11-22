package com.github.positionsfinder.findmyroute.Splash;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.positionsfinder.findmyroute.DB_Processing.Helper_User;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Stop Rotaion in this Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        txtView = (TextView) findViewById(R.id.textView);


        boolean success = Helper_User.loginUser(getApplicationContext(), "admin", "admin");

        /* On start, Check Internet Connection,
         * if false send a Message to MainActivity to Check Internet Connection.
         */
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                txtView.setText("Internet: " + internetConnection() + " VPN: " + success);
                Intent home_activity = new Intent(getApplicationContext(), MainActivity.class);
                if (!internetConnection() & !success) {
                    SPLASH_TIME_OUT = 3000;
                    home_activity.putExtra("No_Internet_VPN", "Check Your Internet and VPN Connection.");
                } else if (!internetConnection()) {
                    SPLASH_TIME_OUT = 2000;
                    home_activity.putExtra("No_Internet", "Check Your Internet Connection.");
                } else if (!success) {
                    SPLASH_TIME_OUT = 2000;
                    home_activity.putExtra("No_VPN", "Check Your VPN Connection.");
                } else {
                    home_activity.putExtra("OK_All", "Internet and VPN Status OK - Good to go.");
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
