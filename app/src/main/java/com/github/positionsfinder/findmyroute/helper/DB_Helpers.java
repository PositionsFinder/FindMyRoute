package com.github.positionsfinder.findmyroute.helper;

import java.util.HashMap;
import java.util.Map;

public class DB_Helpers {

    /**
     * Take an input String array of column names and make a single String out of it.
     *  If the array is empty the wildcard '*' will be returned.
     * @param columns The input array of column names
     * @return The String made of the input or '*' if the array was empty
     */
    public static String makeCommaSeparatedStringFromArray(Object[] columns){

        String columnString = " ";
        // Check the input
        if(columns != null && columns.length > 0){
            // iterate over the amount of columns
            for(int i = 0; i < columns.length; i++){

                Object o = columns[i];
                // Check again
                if(o != null && o != ""){
                    if(i == 0){
                        // no comma here
                        columnString += o;
                    } else {
                        // Append the column's name to the String
                        columnString += "," + o;
                    }
                }
            }
        } else if(columns != null && columns.length == 1) {
            // If the length is 1 just return this first one
            return " " + columns[0]+ " ";
        } else {
            // If the input array is empty return the '*' wildcard
            return " * ";
        }

        return columnString;
    }

    /**
     * Take an input String array of column names and make a single String out of it.
     *  If the array is empty the wildcard '*' will be returned.
     * @param values The input array of column names
     * @return The String made of the input or '*' if the array was empty
     */
    public static String makeCommaSeparatedValueStringFromArray(Object[] values){

        String columnString = " ";
        // Check the input
        if(values != null && values.length > 0){
            // iterate over the amount of columns
            for(int i = 0; i < values.length; i++){

                Object o = values[i];
                // Check again
                if(o != null && o != ""){
                    if(i == 0){
                        // no comma here
                        columnString += o;
                    } else {
                        // Append the column's name to the String
                        columnString += "," + o;
                    }
                }
            }
        } else if(values != null && values.length == 1) {
            // If the length is 1 just return this first one
            return " " + values[0]+ " ";
        } else {
            // If the input array is empty return the '*' wildcard
            return " * ";
        }

        return columnString;
    }

    public static String makeWhereClauseFromMap(HashMap<String, Object> whereClauseMap){

        String whereClause = " ";
        boolean first = true;

        for(Map.Entry<String,Object> entry : whereClauseMap.entrySet()){
            if(first){
                first = false;
                if(entry.getValue().getClass().getName().equals("java.lang.String")){
                    whereClause += entry.getKey() + "='" + entry.getValue() + "'";
                } else {
                    whereClause += entry.getKey() + "=" + entry.getValue();
                }
            } else {
                if(entry.getValue().getClass().getName().equals("java.lang.String")){
                    whereClause += " AND " + entry.getKey() + "='" + entry.getValue() +"'";
                } else {
                    whereClause += " AND " + entry.getKey() + "=" + entry.getValue() + "";
                }
            }

        }

        return whereClause;
    }

    public static String makeSetClauseFromMap(HashMap<String, Object> updateValuePairs){

        String setClause = " ";
        boolean first = true;

        for(Map.Entry<String,Object> entry : updateValuePairs.entrySet()){
            if(first){
                first = false;
                if(entry.getValue().getClass().getName().equals("java.lang.String")){
                    setClause += entry.getKey() + "='" + entry.getValue() + "'";
                } else {
                    setClause += entry.getKey() + "=" + entry.getValue();
                }
            } else {
                if(entry.getValue().getClass().getName().equals("java.lang.String")){
                    setClause += " , " + entry.getKey() + "='" + entry.getValue() +"'";
                } else {
                    setClause += " , " + entry.getKey() + "=" + entry.getValue() + "";
                }
            }

        }

        return setClause;
    }
}
