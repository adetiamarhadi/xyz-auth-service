package com.github.adetiamarhadi.xyz_auth_service.dto;

public class GenericResponse {

    private String message;

    public GenericResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
