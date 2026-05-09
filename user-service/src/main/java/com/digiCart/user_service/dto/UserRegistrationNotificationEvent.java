package com.digiCart.user_service.dto;

public class UserRegistrationNotificationEvent {
    private String email;
    private String name;
    private String verificationCode;

    public UserRegistrationNotificationEvent() {}

    public UserRegistrationNotificationEvent(String email, String name, String verificationCode) {
        this.email = email;
        this.name = name;
        this.verificationCode = verificationCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}