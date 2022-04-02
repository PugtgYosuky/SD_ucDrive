package com.ucdrive.project.client.transfer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ucdrive.project.shared.Transfer;

/**
 * This class is responsible for managing the transfer queue.
 */
public class TransferDispatcher {

    private Queue<Transfer> queue;
    
    // Constructor of the class. It creates a new instance of the `ConcurrentLinkedQueue`
    // class.
    public TransferDispatcher() {
        this.queue = new ConcurrentLinkedQueue<>();
    }

    /**
     * Add a transfer to the queue
     * 
     * @param transfer The transfer to be added to the queue.
     */
    public void addTransfer(Transfer transfer){
        synchronized(this){
            this.queue.add(transfer);
            this.notifyAll();
        }
    }

    /**
     * Get the next transfer in the queue
     * 
     * @return The transfer at the front of the queue.
     */
    public Transfer getTransfer() {
        return this.queue.peek();
    }

    /**
     * Remove the first element from the queue
     */
    public void removeTransfer() {
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
    
}
