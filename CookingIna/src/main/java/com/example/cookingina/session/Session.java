package com.example.cookingina.session;

public final class Session {
    private static String username;
    public Session() {}

    public static void setUsername(String u) {
        username = u;
    }

    public static String getUsername() {
        return username;
    }

    public static void clear() {
        username = null;
    }
}
