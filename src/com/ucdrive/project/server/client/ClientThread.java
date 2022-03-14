package com.ucdrive.project.server.client;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Function;

import com.ucdrive.project.server.client.commands.list.CmdChangePassword;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.client.commands.CommandHandler;
import com.ucdrive.project.server.storage.UserData;
import com.ucdrive.project.shared.User;

public class ClientThread {

    private User client;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private UserData userData;
    private HashMap<String, CommandHandler> commandHandlers;
    private HashMap<String, Function<String, CommandAction>> commandList; 
    
    public ClientThread(Socket socket, UserData userData) {
        this.socket = socket;
        this.userData = userData;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
        this.commandList = new HashMap<>();
        this.commandHandlers = new HashMap<>();
        addCommands();
    }

    public void saveUsers() {
        this.userData.saveUsers();
    }
    
    public User getUser() {
        return this.client;
    }

    public void addCommands() {
        addCommand("change-password", new CmdChangePassword());
    }

    public void addCommand(String prefix, CommandHandler commandHandler) {
        this.commandHandlers.put(prefix, commandHandler);
        this.commandList.put(prefix, string -> {
            try {
                return commandHandlers.get(prefix).parse(new Command(this, string));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        });
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

    public void sendMessage(String message) throws IOException {
        this.outputStream.writeUTF(message);
    }

    public CommandAction parseCommand(String command) throws IOException {
        String prefix = command.split(" ")[0];
        Function<String, CommandAction> function = this.commandList.get(prefix);
        if(function == null) {
            sendMessage("Command not found");
            return null;
        } else {
            CommandAction action = function.apply(command);
            return action;
        }
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
                if(parseCommand(request) == CommandAction.CLOSE_CONNECTION)
                    break;
			} catch (IOException e) {
                break;
			}
        }
    }

}
