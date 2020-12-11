package com.centafrique.textsender.mpesa;

import com.google.gson.annotations.SerializedName;

public class UserLogin {

    @SerializedName("email")
    public String email;

    @SerializedName("password")
    public String password;

    public UserLogin(String email, String Password) {
        this.email = email;
        password = Password;
    }
}
