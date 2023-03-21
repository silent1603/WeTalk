package com.ET.wetalk.models;

public class Users {
    String profilec ;
    String userName;
    String mail;
    String password;
    String userId;
    String lastMessage;

    public Users()
    {

    }

    public Users(String userName, String mail, String password)
    {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
    }

    public Users(String profilec, String userName, String mail, String password) {
        this.profilec = profilec;
        this.userName = userName;
        this.mail = mail;
        this.password = password;

    }

    public Users(String profilec, String userName, String mail, String password, String userId, String lastMessage) {
        this.profilec = profilec;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.lastMessage = lastMessage;
    }

    public String getProfilec() {
        return profilec;
    }

    public void setProfilec(String profilec) {
        this.profilec = profilec;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
