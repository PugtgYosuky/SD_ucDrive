package com.ucdrive.project.client;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.ucdrive.project.client.response.ResponseHandler;
import com.ucdrive.project.shared.Response;

public class ReadThread extends Thread {

    private ObjectInputStream inputStream;
    private ResponseHandler handler;

    public ReadThread(ObjectInputStream inputStream, ResponseHandler handler) {
        this.inputStream = inputStream;
        this.handler = handler;
    }

    public void readMessages() throws IOException {
        try {
            Response res = (Response) inputStream.readObject();
            res.execute(handler);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

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
