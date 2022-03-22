package com.ucdrive.project.server.client;

import java.io.*;
import java.net.Socket;

import com.ucdrive.project.server.client.commands.CommandExecutor;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.storage.UserData;
import com.ucdrive.project.shared.Message;

public class ClientThread {

    private User client;
    private Socket socket;
    private DataInputStream inputStream;
    private ObjectOutputStream outputStream;
    private UserData userData;
    private CommandExecutor commandExecutor;

    
    public ClientThread(Socket socket, UserData userData, CommandExecutor commandExecutor) throws IOException {
        this.socket = socket;
        this.userData = userData;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.commandExecutor = commandExecutor;
    }

    public void saveUsers() {
        this.userData.saveUsers();
    }
    
    public User getUser() {
        return this.client;
    }

    public boolean authenticate() throws IOException {
        String s;
        // TODO: ADD A TIMEOUT
        sendMessage("Insert username: ");
        s = inputStream.readUTF();

        client = userData.findUser(s);

        if(client == null){
            sendMessage("Invalid username. Server is closing for you :(");
            return false;
        }

        sendMessage("Password: ");
        s = inputStream.readUTF();

        if(s.equals(client.getPassword())) {
            sendMessage("You are now connected! :)");
            return true;
        }
        sendMessage("Wrong password. Server is closing for you :(");
        return false;
    }

    public void sendMessage(String message) throws IOException {
        this.outputStream.writeObject(new Message(message));
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
                if(commandExecutor.execute(new Command(this, request)) == CommandAction.CLOSE_CONNECTION)
                    break;
			} catch (IOException e) {
                break;
			}
        }
    }

}
