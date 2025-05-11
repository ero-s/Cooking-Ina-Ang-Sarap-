package com.example.cookingina.user;

import java.io.*;

public class UserCredentials implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("credentials.ser"))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UserCredentials load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("credentials.ser"))) {
            return (UserCredentials) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}