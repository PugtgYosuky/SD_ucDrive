package com.ucdrive.project.server.ftp;

public enum RequestType {
    
    UPLOAD("upload"), DOWNLOAD("download");

    private String type;

    RequestType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }

}
