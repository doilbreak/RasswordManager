package com.radilbe.rassword;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String USER_FILE = "passwordmanager/users.txt";

    public static void saveUser(User user) throws IOException {
        File userDir = new File("passwordmanager");
        if (!userDir.exists()) {
            userDir.mkdir();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getEmail());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Kullanıcı verileri kaydedilirken bir hata oluştu: " + e.getMessage());
        }
    }

    public static List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                users.add(new User(parts[0], parts[1], parts[2]));
            }
        }
        return users;
    }

    public static void deleteUser(User user) throws IOException {
        List<User> users = loadUsers();
        users.removeIf(u -> u.getUsername().equals(user.getUsername()) && u.getEmail().equals(user.getEmail()));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (User u : users) {
                writer.write(u.getUsername() + "," + u.getPassword() + "," + u.getEmail());
                writer.newLine();
            }
        }
    }
}
