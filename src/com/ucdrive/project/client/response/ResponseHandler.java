package com.ucdrive.project.client.response;

import com.ucdrive.project.client.transfer.TransferDispatcher;
import com.ucdrive.project.shared.Message;
import com.ucdrive.project.shared.Transfer;

/**
 * The ResponseHandler class is responsible for handling responses from the server
 */
public class ResponseHandler {
    private TransferDispatcher transferDispatcher;

    // Constructor of the ResponseHandler class. It is responsible for initializing the
    // transferDispatcher
    public ResponseHandler(TransferDispatcher transferDispatcher) {
        this.transferDispatcher = transferDispatcher;
    }

    /**
     * Prints the message to the console
     * 
     * @param message The message to be printed.
     */
    public void execute(Message message) {
        System.out.println(message.getMessage());
    }

    /**
     * Receives the Transfer object and adds it to the transferDispatcher 
     * 
     * @param transfer The transfer to be executed.
     */
    public void execute(Transfer transfer) {
        transferDispatcher.addTransfer(transfer);
    }

}
