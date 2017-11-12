package com.github.positionsfinder.findmyroute;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Button login;
    private Button register;
    private EditText user;
    private EditText pass;
    private TextView txtMessage;
    private ProgressBar progressLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (Button) findViewById(R.id.btnLogin);
        register = (Button) findViewById(R.id.btnReg);

        user = (EditText) findViewById(R.id.editUser);
        pass = (EditText) findViewById(R.id.editPass);

        txtMessage = (TextView) findViewById(R.id.textViewMessage);
        progressLogin = (ProgressBar) findViewById(R.id.progressLogin);
        progressLogin.setVisibility(View.INVISIBLE);


        if (getIntent().hasExtra("Status")) {
            Toast.makeText(this, getIntent().getExtras().getString("Status"),Toast.LENGTH_LONG).show();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userLogin login = null;
                try {
                    login = new userLogin(user.getText().toString(), pass.getText().toString());
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }

                if (login.login()) {
                    progressLogin.setVisibility(View.VISIBLE);
                    txtMessage.setText("Logged in Please Wait.");
                    Intent startIntent = new Intent(getApplicationContext(), MapsActivity.class);
                    startIntent.putExtra("username", user.getText().toString());
                    startActivity(startIntent);
                } else {
                    txtMessage.setText("Wrong Username Or Password | VPN ?");
                }

            }
        });

        //ToDo: Search in DB nach Activation Code.
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("*Still Working on It* \nPlease enter Invitation Code.", true);
            }
        });


    }

    private void showMessage(String message, final boolean input) {

        final EditText txtUrl = new EditText(this);

        // Invite Code has the length of 5


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        if (input) {
            txtUrl.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
            builder.setView(txtUrl);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
        }

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (input) {
                    getInviteCode(txtUrl.getText().toString());
                }
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


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Wichtig: When BACK BUTTON is pressed, the activity on the stack is restarted
    @Override
    public void onRestart() {
        super.onRestart();
        progressLogin.setVisibility(View.INVISIBLE);
        txtMessage.setText("");

        //finish();
        //startActivity(getIntent());
    }

    // Main Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    // Main Menu Options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.about:
                Toast.makeText(this, "About",Toast.LENGTH_SHORT).show();
                aboutMessage();
                break;
            case R.id.useoffline:
                Toast.makeText(this, "Use Offline",Toast.LENGTH_SHORT).show();
                offlineSurfe();
                break;
            case R.id.help:
                Toast.makeText(this, "Help!",Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

    private void aboutMessage() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Find My Route Version 1.0\nAsli Mosaab\nHaider Paolo\n" +
                "Hochschule München\nProgrammierung nativer Android-Apps WS 2017/18 (Schütz)\nMore Info:\nhttps://github.com/PositionsFinder/FindMyRoute");
        builder.setTitle("About:");
        builder.setIcon(R.drawable.hmlogo);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void offlineSurfe(){
        Intent startIntent = new Intent(getApplicationContext(), MapsActivity.class);
        startIntent.putExtra("offline", "offline");
        startActivity(startIntent);
    }
}
