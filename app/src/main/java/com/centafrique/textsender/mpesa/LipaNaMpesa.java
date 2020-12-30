package com.centafrique.textsender.mpesa;

import com.google.gson.annotations.SerializedName;

public class LipaNaMpesa {

    @SerializedName("user_phone")
    private String user_phone;

    @SerializedName("transaction_summary")
    private String transaction_summary;

    @SerializedName("api_key")
    private String api_key;

    @SerializedName("amount")
    private String amount;

    @SerializedName("account_ref")
    private String account_ref;

    public LipaNaMpesa(String user_phone, String transaction_summary, String api_key, String amount, String account_ref) {
        this.user_phone = user_phone;
        this.transaction_summary = transaction_summary;
        this.api_key = api_key;
        this.amount = amount;
        this.account_ref = account_ref;
    }
}
