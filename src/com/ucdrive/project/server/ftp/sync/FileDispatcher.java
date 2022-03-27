package com.ucdrive.project.server.ftp.sync;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileDispatcher {

    private Queue<SyncFile> queue;

    public FileDispatcher() {
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public void addFile(SyncFile file) {
        this.queue.add(file);
    }
    
    public SyncFile getFile() {
        return this.queue.poll();
    }

}
