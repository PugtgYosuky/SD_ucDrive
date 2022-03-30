package com.ucdrive.project.client.response;

import com.ucdrive.project.client.Client;
import com.ucdrive.project.client.transfer.TransferDispatcher;
import com.ucdrive.project.shared.Message;
import com.ucdrive.project.shared.Transfer;

public class ResponseHandler {

    private Client client;
    private TransferDispatcher transferDispatcher;

    public ResponseHandler(Client client, TransferDispatcher transferDispatcher) {
        this.client = client;
        this.transferDispatcher = transferDispatcher;
    }

    public void execute(Message message) {
        System.out.println(message.getMessage());
    }

    public void execute(Transfer transfer) {
        transferDispatcher.addTransfer(transfer, client.getPath());
    }

}
