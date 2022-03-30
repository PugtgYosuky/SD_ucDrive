package com.ucdrive.project.client.transfer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ucdrive.project.client.structures.Pair;
import com.ucdrive.project.shared.Transfer;

public class TransferDispatcher {

    private Queue<Pair<Transfer, String>> queue;
    
    public TransferDispatcher() {
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public void addTransfer(Transfer transfer, String path){
        synchronized(this){
            this.queue.add(new Pair<>(transfer, path));
            this.notifyAll();
        }
    }

    public Pair<Transfer, String> getTransfer() {
        return this.queue.peek();
    }

    public void removeTransfer() {
        this.queue.remove();
    }

    public int getSize() {
        return this.queue.size();
    }
}
