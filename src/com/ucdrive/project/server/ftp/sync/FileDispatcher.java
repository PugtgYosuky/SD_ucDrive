package com.ucdrive.project.server.ftp.sync;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ucdrive.project.server.UDPSynchronized;

public class FileDispatcher {

    private Queue<SyncFile> queue;
    private UDPSynchronized udpSynchronized;

    public FileDispatcher(UDPSynchronized udpSynchronized) {
        this.queue = new ConcurrentLinkedQueue<>();
        this.udpSynchronized = udpSynchronized;
    }

    public void addFile(SyncFile file) {
        System.out.println("Bruh");
        synchronized(udpSynchronized) {
            System.out.println("ALJDWAKDAGIDAG");
            this.queue.add(file);
            System.out.println("File added to the queue");
            System.out.println("Before notify");
            udpSynchronized.notifyAll();
            System.out.println("Notify all");
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
