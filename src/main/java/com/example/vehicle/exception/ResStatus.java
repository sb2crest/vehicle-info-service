package com.example.vehicle.exception;

public enum ResStatus {

    INVALID_NUMBER("4001", "invalid vehicle number"),
    DUPLICATE_NUMBER("4002", "vehicle number already exist"),
    VEHICLE_NUMBER("4003", "please enter vehicle number"),  ;

    private final String code;
    private final String desc;
    ResStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public String getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
}

