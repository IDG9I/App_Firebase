package com.example.app_firebase;

public class Luchador {
    private String identifier;
    private String name;

    public Luchador() {
        // Constructor vacío requerido para Firebase
    }

    public Luchador(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

}
