package com.ucdrive.project.server.ftp.sync;

public class SyncFile{
    
    private String path;
    private String absolutePath;
    private FileType type;
    
    public SyncFile(String path, String absolutePath, FileType type){
        this.path = path;
        this.absolutePath = absolutePath;
        this.type = type;
    }

    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }

    public String getAbsolutePath() {
        return this.absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public FileType getType() {
        return this.type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

}
