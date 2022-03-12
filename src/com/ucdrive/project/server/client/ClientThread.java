package com.ucdrive.project.server.client;

import java.io.*;
import java.net.Socket;

import com.ucdrive.project.server.storage.UserData;
import com.ucdrive.project.shared.User;

public class ClientThread {

    private User client;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private UserData userData;
    
    public ClientThread(Socket socket, UserData userData) {
        this.socket = socket;
        this.userData = userData;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticate() throws IOException {
        String s;
        // TODO: ADD A TIMEOUT
        outputStream.writeUTF("Insert username: ");
        s = inputStream.readUTF();

        client = userData.findUser(s);

        if(client == null){
            outputStream.writeUTF("Invalid username. Server is closing for you :(");
            return false;
        }

        outputStream.writeUTF("Password: ");
        s = inputStream.readUTF();

        if(s.equals(client.getPassword())) {
            outputStream.writeUTF("You are now connected! :)");
            return true;
        }
        outputStream.writeUTF("Wrong password. Server is closing for you :(");
        return false;
    }

    public void start() {

        try {
            if(!authenticate())
                return;
        } catch(IOException exc) {
            return;
        }

        while(true) {
            try {
				String request = inputStream.readUTF();
                System.out.println(request);
                outputStream.writeUTF("Response");
			} catch (IOException e) {
                break;
			}
        }
    }

}
