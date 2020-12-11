package com.centafrique.textsender.Helperclass;

import android.util.Log;

public class CheckPhoneNumber {

    private String newNumber = "";

    public String getPhoneNumber(String number){

        String firstNumber = number.substring(0,1);
        String countryCode = number.substring(0,4);

        if (firstNumber.equals("0")){

            newNumber = number.replace("0", "254");
            Log.e("-*-*-* ", newNumber);

        }else if (firstNumber.equals("+")){

        }


        return number;

    }
}
