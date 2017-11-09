package com.github.positionsfinder.findmyroute;


import android.os.Build;
import android.support.annotation.RequiresApi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


public class DB_Connect implements Runnable {

    private String message = "Empty!";
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private String user;
    private String pass;
    private boolean status;
    private Thread thread;

    public DB_Connect(String user, String pass) throws InterruptedException {
        this.user = user;
        this.pass = pass;

        System.out.println("GEGEBEN in db: " + this.user + " - " + this.pass);

        thread = new Thread(this);
        thread.start();


        try {
            thread.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public boolean loggedIn() {
        return this.status;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {

        DriverManager.setLoginTimeout(1);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.22.62.29:3306/haider_db1", "haider", "haider_edv")) {

            Class.forName("com.mysql.jdbc.Driver");

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM user");

            if (stmt.execute("SELECT * FROM user")) {
                rs = stmt.getResultSet();
            }

            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i < numberOfColumns + 1; i++) {
                    if (rs.getString("username").equals(this.user)) {
                        if (rs.getString("password").equals(this.pass)) {
                            this.status = true;
                            System.out.println("Status db " + status + " --ok/logged in-- ");
                        }
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            message = "" + e.getMessage();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                    message = "" + sqlEx.getMessage();
                }
                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                    message = "" + sqlEx.getMessage();
                }
                stmt = null;
            }

        }

        System.out.println("DB INFO: -> " + this.message);
    }
}

