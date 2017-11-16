package com.github.positionsfinder.findmyroute;

public class userLogin {

    private String user;
    private String pass;
    private DB_Connect connect;

    public userLogin(String user, String pass) throws InterruptedException {

        this.user = user;
        this.pass = pass;

        connect = new DB_Connect(this.user, this.pass);
    }

    public boolean login() {
        return connect.loggedIn();
    }

}
