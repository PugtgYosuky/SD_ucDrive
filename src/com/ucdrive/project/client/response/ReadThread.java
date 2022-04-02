package com.ucdrive.project.client.response;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.ucdrive.project.shared.Response;

/**
 * It reads messages from the input stream and executes the appropriate handler
 */
public class ReadThread extends Thread {

    private ObjectInputStream inputStream;
    private ResponseHandler handler;

    // Constructor of the ReadThread class. It takes two parameters: an ObjectInputStream
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
    
    // A method that is called when the thread is started. It is used to read messages from the input
    // stream and execute the appropriate handler.
    @Override
    public void run() {
        // A loop that is executed until an exception is thrown.
        while(true) {
            try {
                readMessages();
            } catch(IOException exc) {
                return;
            }
        }
    }
    
}
