package com.centafrique.textsender.mpesa;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("message")
    @Nullable
    public String message;

    @SerializedName("code")
    public String code;

}
