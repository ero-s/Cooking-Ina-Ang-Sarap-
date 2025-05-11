package com.example.cookingina.session;

public final class Session {
    private static String username;
    private Session() {}

    public static void setUsername(String u) {
        username = u;
    }

    public static String getUsername() {
        return username;
    }
}
