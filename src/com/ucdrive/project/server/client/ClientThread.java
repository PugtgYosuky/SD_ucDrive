/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.server.client;

import java.io.*;
import java.net.Socket;

import com.ucdrive.project.server.client.commands.CommandExecutor;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.storage.UserData;
import com.ucdrive.project.shared.Message;
import com.ucdrive.project.shared.Response;

/**
 * It reads a command from the client, executes it, and sends a response back to the client
 */
public class ClientThread {

    private User client;
    private Socket socket;
    private DataInputStream inputStream;
    private ObjectOutputStream outputStream;
    private UserData userData;
    private CommandExecutor commandExecutor;

    
    // It creates a new instance of the class ClientThread.
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
    
    /**
     * Returns the user 
     * 
     * @return The user object.
     */
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
        s = inputStream.readUTF(); // username

        client = userData.findUser(s);

        if(client == null){
            sendMessage("Invalid username. Server is closing for you :(");
            return false;
        }

        // It checks if the user is already connected. If it is, it sends a message to the client and
        // closes the connection.
        if(client.getIsConnected()) {
            sendMessage("This user is already connected");
            client = null;
            return false;
        }

        s = inputStream.readUTF(); // password

        // It checks if the password is correct. If it is, it sends a message to the client and
        // sets the isConnected flag to true.
        if(s.equals(client.getPassword())) {
            sendMessage("You are now connected! :)");

            client.setIsConnected(true);

            return true;
        }
        sendMessage("Wrong password. Server is closing for you :(");
        return false;
    }

    /**
     * Send a message to the server
     * 
     * @param message The message to send to the server.
     */
    public void sendMessage(String message) throws IOException {
        this.outputStream.writeObject(new Message(message));
    }

    /**
     * Send a response to the client
     * 
     * @param res The response to send.
     */
    public void sendResponse(Response res) throws IOException {
        this.outputStream.writeObject(res);
    }

    /**
     * Reads commands from the client, executes it, and sends a response back to the client
     */
    public void start() {

        try {
            // It checks if the user is authenticated. If it is not, it closes the connection.
            if(!authenticate())
                return;
        } catch(IOException exc) {
            return;
        }

        while(true) {
            try {
				// It reads a command from the client, executes it, and sends a response back to the client.
                String request = inputStream.readUTF();
                CommandAction action = commandExecutor.execute(new Command(this, request));
                // Closing the connection.
                if(action == CommandAction.CLOSE_CONNECTION)
                    break;
			} catch (IOException e) {
                break;
			}
        }
    }

}
