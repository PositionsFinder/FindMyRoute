package com.github.positionsfinder.findmyroute.Main;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.positionsfinder.findmyroute.DB_Processing.Helper_User;
import com.github.positionsfinder.findmyroute.Maps.MapsActivity;
import com.github.positionsfinder.findmyroute.R;

public class MainActivity extends AppCompatActivity {

    private Button login;
    private Button register;
    private EditText user;
    private EditText pass;
    private TextView txtMessage;
    private ProgressBar progressLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Stop Rotaion in this Activity

        login = (Button) findViewById(R.id.btnLogin);
        register = (Button) findViewById(R.id.btnReg);

        user = (EditText) findViewById(R.id.editUser);
        pass = (EditText) findViewById(R.id.editPass);

        txtMessage = (TextView) findViewById(R.id.textViewMessage);

        progressLogin = (ProgressBar) findViewById(R.id.progressLogin);
        progressLogin.setVisibility(View.INVISIBLE);

        //Internet and VPN check from Splash.class
        if (getIntent().hasExtra("No_Internet_VPN")) {
            Toast.makeText(this, getIntent().getExtras().getString("No_Internet_VPN"), Toast.LENGTH_LONG).show();
        } else if (getIntent().hasExtra("No_Internet")) {
            Toast.makeText(this, getIntent().getExtras().getString("No_Internet"), Toast.LENGTH_LONG).show();
        } else if (getIntent().hasExtra("No_VPN")) {
            Toast.makeText(this, getIntent().getExtras().getString("No_VPN"), Toast.LENGTH_LONG).show();
        } else if (getIntent().hasExtra("OK_All")) {
            Toast.makeText(this, getIntent().getExtras().getString("OK_All"), Toast.LENGTH_LONG).show();
        }

        // Check Username und Password in DB.
        // ToDo: auf neue DB_Processing umsteigen.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean success = Helper_User.loginUser(getApplicationContext(), user.getText().toString(), pass.getText().toString());

                if (success) {

                    progressLogin.setVisibility(View.VISIBLE);
                    txtMessage.setText("Logged in Please Wait.");
                    Intent startIntent = new Intent(getApplicationContext(), MapsActivity.class);
                    startIntent.putExtra("user", user.getText().toString());//send Username to MapsActivity for Greeting.
                    startActivity(startIntent);
                } else {
                    txtMessage.setText("Invalid username or password.");
                }

            }
        });

        //ToDo: Search in DB nach Activation Code. When Button Pressed.
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //TODO: Mask with actioncode and username/password because we send all three together at the moment.
                boolean success = Helper_User.activateUser(getApplicationContext(), user.getText().toString(), pass.getText().toString(), "omsuy");
                showMessage("Please enter the invitation code:");

            }
        });


    }

    private void showMessage(String message) {

        final EditText txtUrl = new EditText(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        txtUrl.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});// Invite Code has the length of 5
        builder.setView(txtUrl);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                toastMessage("Canceled");
            }
        });


        //ToDo: Search in DB nach Activation Code.
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getInviteCode(txtUrl.getText().toString());
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void getInviteCode(String s) {
        ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        dialog.closeOptionsMenu();
    }

    // if Back-Button Pressed, Load MainActivity without Text and Set Progress Invisible.
    @Override
    public void onRestart() {
        super.onRestart();
        progressLogin.setVisibility(View.INVISIBLE);
        txtMessage.setText("");
    }

    // Main-Activity Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    // Main-Activity Menu-Options @return always true.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent startIntent;
        switch (id) {
            // About Tab - Open new Activity with the About and Help Information
            case R.id.about:
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                startIntent = new Intent(getApplicationContext(), HelpAndAbout.class);
                startActivity(startIntent);
                break;
            // Use Offline Tab - Open Map-Activity with Reduced Menu-Options. Using @see offlineSurfe()
            case R.id.useoffline:
                Toast.makeText(this, "Offline: Please wait until the Location is Loaded!", Toast.LENGTH_LONG).show();
                offlineSurfe();
                break;
        }

        return true;
    }

    private void offlineSurfe() {
        Intent startIntent = new Intent(getApplicationContext(), MapsActivity.class);
        startIntent.putExtra("offline", "offline");
        startActivity(startIntent);
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //ToDo: on closing set user offline.
    protected void onDestroy() {
        super.onDestroy();
        //Helper_User.setUserOffline(getApplicationContext(), user.getText().toString());
        //Update the User.php from my Folder on Server.


    }
}
