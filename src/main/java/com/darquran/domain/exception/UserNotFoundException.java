package com.darquran.domain.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String id) {
        super("Utilisateur introuvable avec l'ID : " + id);
    }

    public UserNotFoundException(String email, boolean isEmail) {
        super("Utilisateur introuvable avec l'email : " + email);
    }
}