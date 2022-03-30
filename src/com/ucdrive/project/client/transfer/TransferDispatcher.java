package com.ucdrive.project.client.transfer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ucdrive.project.shared.Transfer;

public class TransferDispatcher {

    private Queue<Transfer> queue;
    
    public TransferDispatcher() {
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public void addTransfer(Transfer transfer){
        synchronized(this){
            this.queue.add(transfer);
            this.notifyAll();
        }
    }

    public Transfer getTransfer() {
        return this.queue.peek();
    }

    public void removeTransfer() {
        this.queue.remove();
    }

    public int getSize() {
        return this.queue.size();
    }
    
}
