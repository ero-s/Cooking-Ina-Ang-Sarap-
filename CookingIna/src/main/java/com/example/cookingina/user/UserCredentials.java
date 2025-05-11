package com.example.cookingina.user;

import java.io.*;

public class UserCredentials implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String CREDENTIALS_PATH = System.getProperty("user.home") + "/.cooking-ina/credentials.text";

    private String username;
    private String password;

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // [Keep existing constructor and getters]

    public void save() {
        File dir = new File(System.getProperty("user.home"), ".cooking-ina");
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("Failed to create config directory");
            return;
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(CREDENTIALS_PATH))) {
            if(username != null && password != null) {
                if(!username.isEmpty() && !password.isEmpty()) {
                    oos.writeObject(null);
                }
                else{
                    oos.writeObject(this);

                }
            }
        } catch (IOException e) {
            System.err.println("Failed to save credentials: " + e.getMessage());
        }
    }

    public static UserCredentials load() {
        File file = new File(CREDENTIALS_PATH);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(CREDENTIALS_PATH))) {
            return (UserCredentials) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load credentials: " + e.getMessage());
            // Delete corrupted file
            file.delete();
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}