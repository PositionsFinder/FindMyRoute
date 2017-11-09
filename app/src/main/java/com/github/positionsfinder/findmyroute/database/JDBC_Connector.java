package com.github.positionsfinder.findmyroute.database;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import com.github.positionsfinder.findmyroute.helper.DB_Helpers;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class JDBC_Connector {

    Connection conn = null;

    // The instance of this class
    private static JDBC_Connector instance;

    // If existent returns the instance - otherwise a new instance (object) will be generated
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static JDBC_Connector getInstance () {
        if (JDBC_Connector.instance == null) {
            JDBC_Connector.instance = new JDBC_Connector();
        }
        return JDBC_Connector.instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private JDBC_Connector() {

        try {
            // The newInstance() call is a work around for some
            // broken Java implementations
            Class.forName("com.mysql.jdbc.Driver").newInstance();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ie) {

            Log.e("ERR", "JDBC driver could not be loaded");
        }


    }

    public void connect(String serverIp, String database, String userName, String password){
        connect(serverIp, 3306, database, userName, password);
    }

    public void connect(String serverIp, int serverPort, String database, String userName, String password){

        if(serverIp != null && userName != null && password != null) {

            try {

                conn = DriverManager.getConnection("jdbc:mysql://" + serverIp + ":" + serverPort + "/" + database, userName, password);

            } catch (SQLException e) {

                //e.printStackTrace();
                // handle any errors
                System.out.println("SQLException: " + e.getMessage());
                System.out.println("SQLState: " + e.getSQLState());
                System.out.println("VendorError: " + e.getErrorCode());

            }
        }
    }

    /**
     * Create a SELECT statement from the provided columns list and table name.
     * @param columns The String array of columns to fetch
     * @param tableName The table name from which to fetch
     * @return The result of the executed SELECT statement as ArrayList
     */
    public ArrayList<HashMap<String, Object>> selectSTMNT(String[] columns, String tableName, HashMap<String, Object> whereClauseMap) {

        ResultSet rs = null;
        PreparedStatement prepStmnt = null;
        // The ArrayList to store the results
        ArrayList<HashMap<String, Object>> resultsMap = new ArrayList<>();
        // Create a single String from the column names array @columns
        String columnsString = DB_Helpers.makeCommaSeparatedStringFromArray(columns);
        String whereClause = DB_Helpers.makeWhereClauseFromMap(whereClauseMap);

        try {
            String stmnt = "SELECT " + columnsString + " FROM " + tableName + " WHERE " + whereClause + ";";
            // DBG: System.out.println("STATEMENT: " + stmnt);
            // prepare the statement from the input
            prepStmnt = conn.prepareStatement(stmnt);

            // execute the prepared statement
            if (prepStmnt.execute()) {
                // get the ResultSet
                rs = prepStmnt.getResultSet();
            }

            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();

            // iterate over the ResultSet..
            while (rs.next()) {

                HashMap<String, Object> map =  new HashMap<String, Object>();

                for (int i = 1; i < numberOfColumns + 1; i++) {
                    // ..and put the results in a HashMap..
                    map.put(rsMetaData.getColumnName(i), rs.getString(i));
                }
                // ..and finally in our ArrayList
                resultsMap.add(map);
            }
        } catch (SQLException e){
            System.out.println("SQLException: " + e.getMessage());
        } finally {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore

                rs = null;
            }

            if (prepStmnt != null) {
                try {
                    prepStmnt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                prepStmnt = null;
            }
        }

        return resultsMap;
    }

    /**
     * Create an INSERT statement from the provided columns list and table name.
     * @param updateValuePairs The String array of columns affected
     * @param tableName The table name to insert into
     * @return true if successful, false otherwise
     */
    public void updateSTMNT(HashMap<String, Object> updateValuePairs, String tableName, HashMap<String, Object> whereClauseMap) {

        ResultSet rs = null;
        PreparedStatement prepStmnt = null;
        // Create a single String from the column names array @columns
        String whereClause = DB_Helpers.makeWhereClauseFromMap(whereClauseMap);
        String setClause = DB_Helpers.makeSetClauseFromMap(updateValuePairs);

        try {
            String stmnt = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause + ";";
            //DBG: System.out.println("STATEMENT: " + stmnt);
            // prepare the statement from the input
            prepStmnt = conn.prepareStatement(stmnt);

            // execute the prepared statement
            if (prepStmnt.execute()) {
                // get the ResultSet
                rs = prepStmnt.getResultSet();
            }

        } catch (SQLException e){
            System.out.println("SQLException: " + e.getMessage());
        } finally {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore

                rs = null;
            }

            if (prepStmnt != null) {
                try {
                    prepStmnt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                prepStmnt = null;
            }
        }
    }

    /**
     * Create an INSERT statement from the provided columns list and table name.
     * @param columns The String array of columns affected
     * @param tableName The table name to insert into
     * @return true if successful, false otherwise
     */
    public void insertSTMNT(String[] columns, Object[] values, String tableName) {

        ResultSet rs = null;
        PreparedStatement prepStmnt = null;
        // Create a single String from the column names array @columns
        String columnsString = DB_Helpers.makeCommaSeparatedStringFromArray(columns);
        String valueString = DB_Helpers.makeCommaSeparatedValueStringFromArray(values);

        try {
            String stmnt = "INSERT into " + tableName + "(" + columnsString + ") values(" + valueString + ");";
            System.out.println("STATEMENT: " + stmnt);
            // prepare the statement from the input
            prepStmnt = conn.prepareStatement(stmnt);

            // execute the prepared statement
            if (prepStmnt.execute()) {
                // get the ResultSet
                rs = prepStmnt.getResultSet();
            }

        } catch (SQLException e){
            System.out.println("SQLException: " + e.getMessage());
        } finally {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore

                rs = null;
            }

            if (prepStmnt != null) {
                try {
                    prepStmnt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                prepStmnt = null;
            }
        }
    }

}
