package com.ucdrive.project.server.ftp.sync;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ucdrive.project.server.UDPSynchronized;

public class FileDispatcher {

    private Queue<SyncFile> queue;
    private UDPSynchronized udpSynchronized;

    public FileDispatcher() {
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public void addFile(SyncFile file) {
        this.queue.add(file);
        synchronized(udpSynchronized) {
            udpSynchronized.notifyAll();
        }
    }
    public UDPSynchronized getUdpSynchronized() {
        return udpSynchronized;
    }

    public void setUdpSynchronized(UDPSynchronized udpSynchronized) {
        this.udpSynchronized = udpSynchronized;
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

}
