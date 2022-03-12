package com.ucdrive.project.client;

import java.io.DataInputStream;
import java.io.IOException;

public class ReadThread extends Thread {

    private DataInputStream inputStream;

    public ReadThread(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void readMessages() throws IOException {
        String message = inputStream.readUTF();
        System.out.println(message);
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
