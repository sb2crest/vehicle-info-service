package com.example.vehicle.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -93116643889493326L;

    private final String errorCode;
    private final String errorDesc;
    private final HttpStatus httpStatus;

    public BusinessException(String errorCode, String errorDesc, HttpStatus httpStatus) {
        super(errorCode +": "+errorDesc);
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
        this.httpStatus = httpStatus;
    }

}
