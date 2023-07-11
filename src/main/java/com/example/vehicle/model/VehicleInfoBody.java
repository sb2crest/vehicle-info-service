package com.example.vehicle.model;

import lombok.Getter;

import java.util.Objects;


@Getter
public class VehicleInfoBody {
    private final String message;

    public VehicleInfoBody(String message) {
        super();
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VehicleInfoBody that = (VehicleInfoBody) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), message);
    }
}
