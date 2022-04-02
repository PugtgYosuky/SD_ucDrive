package com.ucdrive.project.server.client;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ucdrive.project.server.ftp.sync.FileDispatcher;
import com.ucdrive.project.server.ftp.sync.FileType;
import com.ucdrive.project.server.ftp.sync.SyncFile;

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
                  String college, String address, String phoneNumber, String numberCC, Date dateCC, 
                  String path, String diskPath, FileDispatcher fileDispatcher) {
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
    
        this.ensurePath(fileDispatcher);
    }
    
    /**
     * Return the absolute path of the user's directory
     * 
     * @return The absolute path of the user's directory.
     */
    public String getAbsolutePath() {
        return this.diskPath + "/disk/users/" + username + this.path;
    }

    /**
     * Ensure that the path exists
     * 
     * @param fileDispatcher The file dispatcher that will be used ensure that
     */
    private void ensurePath(FileDispatcher fileDispatcher) {
        // This is creating the directory structure for the user.
        fileDispatcher.addFile(new SyncFile(username + "/home", this.diskPath + "/disk/users/" + username + this.path, FileType.DIRECTORY));
        new File(getAbsolutePath()).mkdirs();
    }

    /**
     * Returns the value of the isConnected variable
     * 
     * @return The value of the isConnected field.
     */
    public synchronized boolean getIsConnected() {
        return this.isConnected;
    }

    /**
     * Sets the value of the isConnected variable
     * 
     * @param isConnected A boolean value that indicates whether the client is connected to the server.
     */
    public synchronized void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    /**
     * Get the username of the current user
     * 
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the current user
     * 
     * @param username The username of the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the user password
     * 
     * @return The password.
     */
    public String getPassword() {
        /*StringBuilder decypher = new StringBuilder();
        for(char c : password.toCharArray())
            decypher.append((char) (c-6));*/
        return this.password;
    }

    /**
     * Sets the user password
     * 
     * @param password The password to be encrypted.
     */
    public void setPassword(String password) {
        /*StringBuilder cypher = new StringBuilder();
        for(char c : password.toCharArray())
            cypher.append((char) (c+6));*/
        this.password = password;
    }

    /**
     * The getDepartment method returns the department field
     * 
     * @return The department attribute.
     */
    public String getDepartment() {
        return department;
    }

    /**
     * sets the department attribute
     * 
     * @param department The department
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     *Returns the value of the college field
     * 
     * @return The college field.
     */
    public String getCollege() {
        return college;
    }

    /**
     * Sets the college attribute
     * 
     * @param college The name of the college.
     */
    public void setCollege(String college) {
        this.college = college;
    }

    /**
     * Get the address of the person
     * 
     * @return Nothing.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address property
     * 
     * @param address The address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the phone number 
     * 
     * @return The phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phoneNumber variable
     * 
     * @param phoneNumber The phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the number of the CC
     * 
     * @return The numberCC variable.
     */
    public String getNumberCC() {
        return numberCC;
    }

    /**
     * Sets the numberCC property
     * 
     * @param numberCC The number of the CC to be used.
     */
    public void setNumberCC(String numberCC) {
        this.numberCC = numberCC;
    }

    /**
     * Returns the date of the CC
     * 
     * @return The dateCC field.
     */
    public Date getDateCC() {
        return dateCC;
    }

    /**
     * Sets the dateCC property
     * 
     * @param dateCC The date 
     */
    public void setDateCC(Date dateCC) {
        this.dateCC = dateCC;
    }

    /**
     * Returns the user path 
     * 
     * @return The path
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Sets the user path
     * 
     * @param path The path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Returns the path to the disk
     * 
     * @return The disk path.
     */
    public String getDiskPath() {
        return this.diskPath;
    }

    /**
     * Sets the diskPath variable
     * 
     * @param diskPath The path to the disk.
     */
    public void setDiskPath(String diskPath){
        this.diskPath = diskPath;
    }

    /**
     * This function returns a string representation of the object
     * 
     * @return The toString method is overridden to return a String with the values of the fields.
     */
    @Override
    public String toString() {
        return username + ";" + password + ";" + department + ";" + college + ";" + address + ";" + phoneNumber + ";" + numberCC + ";" + new SimpleDateFormat("dd/mm/yyyy").format(dateCC) + ";" + path;
    }

}
