package com.ucdrive.project.server.ftp;

import java.util.UUID;

import com.ucdrive.project.server.client.User;

public class RequestFile {
    
    private String uniqueID;
    private User user;
    private String path;
    private RequestType type;
    private String fileName;
    
    public RequestFile (User user, String path, RequestType type, String fileName) {
        this.uniqueID = UUID.randomUUID().toString();
        this.user = user;
        this.path = path;
        this.type = type;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUniqueID() {
        return uniqueID;
    }
    
    public User getUser() {
        return user;
    }

    public String getPath() {
        return path;
    }

    public RequestType getType() {
        return type;
    }

}
