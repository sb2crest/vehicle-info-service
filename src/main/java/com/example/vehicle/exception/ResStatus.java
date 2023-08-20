package com.example.vehicle.exception;

import lombok.Getter;

@Getter
public enum ResStatus {

    INVALID_NUMBER("4001", "invalid vehicle number"),
    DUPLICATE_NUMBER("4002", "vehicle number already exist"),
    VEHICLE_NUMBER("4003", "please enter vehicle number"),
    ENTER_NUMBER("4004", "please enter mobile number"),
    MOBILE_DIGIT("4005", "please enter 10 digit mobile number");

    private final String code;
    private final String desc;
    ResStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

