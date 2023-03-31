package com.example.ccuregen;

public class ReadWriteUserDetails {
    public String dob, gender, mobile;

    public ReadWriteUserDetails(){};
    public ReadWriteUserDetails(String textDoB, String textGender,
                                String textMobile) {
        this.dob = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;
    }

}
