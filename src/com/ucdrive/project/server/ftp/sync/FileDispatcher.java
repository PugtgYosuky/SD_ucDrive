package com.ucdrive.project.server.ftp.sync;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ucdrive.project.server.UDPSynchronized;

/**
 * This class is responsible for dispatching files to the other server
 */
public class FileDispatcher {

    private Queue<SyncFile> queue;
    private UDPSynchronized udpSynchronized;

    // This is the constructor of the class. It initializes the queue and the udpSynchronized object.
    public FileDispatcher(UDPSynchronized udpSynchronized) {
        this.queue = new ConcurrentLinkedQueue<>();
        this.udpSynchronized = udpSynchronized;
    }

    /**
     * Add a file to the queue
     * 
     * @param file The file to be added to the queue.
     */
    public void addFile(SyncFile file) {
        synchronized(udpSynchronized) {
            this.queue.add(file);
            System.out.println("File added to the queue");
            udpSynchronized.notifyAll();
        }
    }
    
    /**
     * Get the next file in the queue.
     * 
     * @return The first element in the queue.
     */
    public SyncFile getFile() {
        return this.queue.peek();
    }

    /**
     * Remove the first file in the queue.
     */
    public void removeFile() {
        this.queue.remove();
    }

    /**
     * Return the size of the queue
     * 
     * @return The size of the queue.
     */
    public int getSize() {
        return this.queue.size();
    }

    /**
     * Returns the queue of files that need to be synced
     * 
     * @return The queue of SyncFile objects.
     */
    public Queue<SyncFile> getQueue() {
        return this.queue;
    }

}
