package com.example.trackingapp.Util;

import java.util.Map;

public class Userinformation {
    private String nameSurname;
    private String email;
    private String phone_num;
    private String Connected_num;
    private String Alarm_num;

    public Userinformation() {


    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getConnected_num() {
        return Connected_num;
    }

    public void setConnected_num(String connected_num) {
        Connected_num = connected_num;
    }

    public String getAlarm_num() {
        return Alarm_num;
    }

    public void setAlarm_num(String alarm_num) {
        Alarm_num = alarm_num;
    }
}
