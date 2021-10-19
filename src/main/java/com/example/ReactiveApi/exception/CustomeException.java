package com.example.ReactiveApi.exception;

public class CustomeException extends Throwable {
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CustomeException(Throwable e) {
        this.message = e.getMessage();
    }
}
