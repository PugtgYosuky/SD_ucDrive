package com.ucdrive.project.server.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import com.ucdrive.project.server.client.User;
import com.ucdrive.project.server.ftp.sync.FileDispatcher;

public class UserData {
    
    private Vector<User> users;
    private String path;


    // Initializing the users Vector and the path.
    public UserData(String path, FileDispatcher fileDispatcher) {
        this.users = new Vector<>();
        this.path = path + "/config/accounts.txt";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.path))) {
            String line;
            String[] data;

            while((line = bufferedReader.readLine()) != null && line.length() > 0) {;  
                data = line.split(";");
                try {
                    users.add(new User(data[0], data[1], data[2], data[3], data[4], data[5], data[6], new SimpleDateFormat("dd/MM/yyyy").parse(data[7]), data[8], path, fileDispatcher));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch(FileNotFoundException exc) {
            exc.printStackTrace();
        } catch(IOException exc) {
            exc.printStackTrace();
        }

    }

    /**
     * Find a user by their username
     * 
     * @param username The username of the user you want to find.
     * @return Nothing.
     */
    public User findUser(String username) {
        for(User user : users){
            if(username.equalsIgnoreCase(user.getUsername()))
                return user;
        }
        return null;
    }

    /**
     * Save the users to a file
     */
    public synchronized void saveUsers() {

        try (PrintWriter bufferedWriter = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
            for(User user : users){
                bufferedWriter.println(user);
            }
        } catch(FileNotFoundException exc) {
            exc.printStackTrace();
        } catch(IOException exc) {
            exc.printStackTrace();
        }
    }
}
