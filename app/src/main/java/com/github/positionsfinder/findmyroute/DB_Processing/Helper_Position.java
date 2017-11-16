package com.github.positionsfinder.findmyroute.DB_Processing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Paolo on 14.11.2017.
 */

/**
 * This is a helper class designed to take care of various operations related to the application's
 * user's position and their interaction with the Database.
 */
public class Helper_Position {

    /**
     * Parses a given timestamp and generates a Date object from it.
     * @param timeStamp The timestamp string returned by the database
     * @return A Date object with the parsed timeStamp or the current time if parsing failed.
     */
    public static Date parseTimeStamp(String timeStamp){

        Date d = new Date();
        // This pattern will be returned by the DB
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            d = sdf.parse(timeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // DBG: System.out.println(d);
        return d;
    }

}
