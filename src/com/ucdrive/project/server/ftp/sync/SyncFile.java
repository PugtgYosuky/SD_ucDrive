package com.ucdrive.project.server.ftp.sync;

/**
 * A SyncFile is a file that is to be synced with the other server
 */
public class SyncFile{
    
    private String path;
    private String absolutePath;
    private FileType type;
    
    // This is the constructor of the class. It is used to create a new instance of the class.
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
