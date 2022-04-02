package com.ucdrive.project.client.response;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.ucdrive.project.shared.Response;

public class ReadThread extends Thread {

    private ObjectInputStream inputStream;
    private ResponseHandler handler;

    // This is the constructor of the ReadThread class. It takes two parameters: an ObjectInputStream
    // and a ResponseHandler.
    public ReadThread(ObjectInputStream inputStream, ResponseHandler handler) {
        this.inputStream = inputStream;
        this.handler = handler;
    }

    /**
     * Reads messages from the input stream and executes the appropriate handler
     */
    public void readMessages() throws IOException {
        try {
            Response res = (Response) inputStream.readObject();
            res.execute(handler);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read messages from the socket and put them into the queue.
     */
    @Override
    public void run() {
        while(true) {
            try {
                readMessages();
            } catch(IOException exc) {
                return;
            }
        }
    }
    
}
