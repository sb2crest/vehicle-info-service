package com.example.vehicle.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VehicleNumberException extends BusinessException {

    private static final long serialVersionUID = -5383000840940136245L;

    public VehicleNumberException(String code, String description) {
        super(code, description, HttpStatus.BAD_REQUEST);
    }
    public VehicleNumberException(ResStatus resStatus) {
        super(resStatus.getCode(), resStatus.getDesc(), HttpStatus.BAD_REQUEST);
    }
}

