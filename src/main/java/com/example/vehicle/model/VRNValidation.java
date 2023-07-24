package com.example.vehicle.model;

import java.util.regex.*;

public class VRNValidation {
    private VRNValidation(){
    }
    public static boolean isValid(String number){
        String regexp = "(([a-zA-Z,]){2}\\s([\\d,]{0,4})\\s([a-zA-Z]{0,3})\\s([\\d,]){4})$";

        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher=pattern.matcher(number);
        return matcher.matches();
    }
}
