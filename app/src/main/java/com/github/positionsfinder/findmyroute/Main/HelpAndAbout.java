package com.github.positionsfinder.findmyroute.Main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.github.positionsfinder.findmyroute.R;

public class HelpAndAbout extends AppCompatActivity {

    private CheckedTextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_about);

        text = (CheckedTextView) findViewById(R.id.checkedTextView);
        String linkText = "For more information: <a href='https://github.com/PositionsFinder/FindMyRoute'>Click Here</a> .";

        text.setText("");
        text.setText("About:\nFind My Route Version 1.0 Programmierer: \nAsli Mosaab und Haider Paolo\n-Hochschule München-\nProjekt in Programmierung nativer Android-Apps WS 2017/18 (Schütz))\n");
        text.append("Help:\nThis Application helps to find the Direction between two USERS. or a Direction between your current location and a List of tourist attractions\n");
        text.append(Html.fromHtml(linkText));
        text.append("\nOr Scan the CODE.");
        text.setMovementMethod(LinkMovementMethod.getInstance());


    }
}
