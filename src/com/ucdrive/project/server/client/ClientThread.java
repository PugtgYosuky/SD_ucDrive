package com.ucdrive.project.server.client;

import java.io.*;
import java.net.Socket;

import com.ucdrive.project.server.client.commands.CommandExecutor;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.storage.UserData;
import com.ucdrive.project.shared.Message;
import com.ucdrive.project.shared.Response;

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
        inputStream = new DataInputStream(this.socket.getInputStream());
        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.commandExecutor = commandExecutor;
    }

    /**
     * Save the user data to a file
     */
    public void saveUsers() {
        this.userData.saveUsers();
    }
    
    public User getUser() {
        return this.client;
    }

    /**
     * It checks if the user is in the database and if the password is correct.
     * 
     * @return Nothing.
     */
    public boolean authenticate() throws IOException {
        String s;
        // TODO: ADD A TIMEOUT
        //sendMessage("Insert username: ");
        s = inputStream.readUTF();

        client = userData.findUser(s);

        if(client == null){
            sendMessage("Invalid username. Server is closing for you :(");
            return false;
        }

        if(client.getIsConnected()) {
            sendMessage("This user is already connected");
            client = null;
            return false;
        }

        //sendMessage("Password: ");
        s = inputStream.readUTF();

        if(s.equals(client.getPassword())) {
            sendMessage("You are now connected! :)");

            client.setIsConnected(true);

            return true;
        }
        sendMessage("Wrong password. Server is closing for you :(");
        return false;
    }

    public void sendMessage(String message) throws IOException {
        this.outputStream.writeObject(new Message(message));
    }

    public void sendResponse(Response res) throws IOException {
        this.outputStream.writeObject(res);
    }

    /**
     * Reads a command from the client, executes it, and sends a response back to the client
     */
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
                CommandAction action = commandExecutor.execute(new Command(this, request));
                System.out.println("Action: " + action);
                if(action == CommandAction.CLOSE_CONNECTION)
                    break;
			} catch (IOException e) {
                break;
			}
        }
    }

}
