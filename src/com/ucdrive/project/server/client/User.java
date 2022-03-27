package com.ucdrive.project.server.client;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User implements Serializable {

    private String username;
    private String password;
    private String department;
    private String college;
    private String address;
    private String phoneNumber;
    private String numberCC;
    private Date dateCC;
    private String path;
    private String diskPath;
    private boolean isConnected;

    public User(String username, String password, String department,
                  String college, String address, String phoneNumber, String numberCC, Date dateCC, String path, String diskPath) {
        this.username = username;
        this.password = password;
        this.department = department;
        this.college = college;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.numberCC = numberCC;
        this.dateCC = dateCC;
        this.path = path;
        this.diskPath = diskPath;
        this.isConnected = false;
        this.ensurePath();
    }
    
    public String getAbsolutePath() {
        return this.diskPath + "/disk/users/" + username + this.path;
    }

    private void ensurePath() {
        try {
            Files.createDirectories(Paths.get(getAbsolutePath()));
        } catch (IOException e) {
            // The path already exists
        }
    }

    public synchronized boolean getIsConnected() {
        return this.isConnected;
    }

    public synchronized void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        /*StringBuilder decypher = new StringBuilder();
        for(char c : password.toCharArray())
            decypher.append((char) (c-6));*/
        return this.password;
    }

    public void setPassword(String password) {
        /*StringBuilder cypher = new StringBuilder();
        for(char c : password.toCharArray())
            cypher.append((char) (c+6));*/
        this.password = password;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNumberCC() {
        return numberCC;
    }

    public void setNumberCC(String numberCC) {
        this.numberCC = numberCC;
    }

    public Date getDateCC() {
        return dateCC;
    }

    public void setDateCC(Date dateCC) {
        this.dateCC = dateCC;
    }

    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }

    public String getDiskPath() {
        return this.diskPath;
    }

    public void setDiskPath(String diskPath){
        this.diskPath = diskPath;
    }
    @Override
    public String toString() {
        return username + ";" + password + ";" + department + ";" + college + ";" + address + ";" + phoneNumber + ";" + numberCC + ";" + new SimpleDateFormat("dd/mm/yyyy").format(dateCC) + ";" + path;
    }

}
