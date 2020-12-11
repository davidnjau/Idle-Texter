package com.centafrique.textsender.mpesa;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {

    @SerializedName("message")
    public Message message;

    public static class Message{

        @SerializedName("ResultCode")
        @Nullable
        public String ResultCode;

        @SerializedName("checkout_req_id")
        public String checkout_req_id;

        @SerializedName("phone_number")
        @Nullable
        public String phone_number;

        @SerializedName("confirmed")
        @Nullable
        public String confirmed;

        @SerializedName("amount")
        @Nullable
        public String amount;

        @SerializedName("business_token")
        @Nullable
        public String business_token;

        @SerializedName("id")
        @Nullable
        public String id;

        @SerializedName("ResultDesc")
        @Nullable
        public String ResultDesc;



    }

}
