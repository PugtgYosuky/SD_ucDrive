package com.ucdrive.project.server.ftp.sync;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileDispatcher {

    private Queue<SyncFile> queue;
    private UDPSynchronized udpSynchronized;

    public FileDispatcher(UDPSynchronized udpSynchronized) {
        this.queue = new ConcurrentLinkedQueue<>();
        this.udpSynchronized = udpSynchronized;
    }

    public void addFile(SyncFile file) {
        synchronized(udpSynchronized) {
            this.queue.add(file);
            System.out.println("File added to the queue");
            udpSynchronized.notifyAll();
        }
    }
    
    public SyncFile getFile() {
        return this.queue.peek();
    }

    public void removeFile() {
        this.queue.remove();
    }

    public int getSize() {
        return this.queue.size();
    }

    public Queue<SyncFile> getQueue() {
        return this.queue;
    }

}
