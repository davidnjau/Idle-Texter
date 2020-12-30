package com.centafrique.textsender.mpesa;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface InterFaces {

    @POST("getaccess")
    Call<TokenResponse> getAccessLiveToken(@Body UserLogin userLogin);

    @POST("makepayment")
    Call<PaymentResponse> lipaNaMpesa(@HeaderMap Map<String, String> headers,
                                      @Body LipaNaMpesa userRegister);

}
