package com.ucdrive.project.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Scanner;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandExecutor;
import com.ucdrive.project.client.commands.CommandHandler;
import com.ucdrive.project.client.response.ResponseHandler;

public class Client {
    private String path;
    public Client(String path) {
        this.path = path;
    }
    public static void main(String[] args) {
        Client client = new Client(System.getProperty("user.dir"));

        ResponseHandler responseHandler = new ResponseHandler(client);

        try (Scanner scanner = new Scanner(System.in)) {
            try (Socket server = new Socket(args[0], Integer.parseInt(args[1]))) {
                System.out.println("Connected");

                ObjectInputStream inputStream = new ObjectInputStream(server.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(server.getOutputStream());
                CommandExecutor commandExecutor = new CommandExecutor(client, outputStream);
                CommandHandler.commandExecutor = commandExecutor;

                new ReadThread(inputStream, responseHandler).start();

                /*
                    !!! In orderd to identify the moment we lose connection with the server
                */
                while(true) {
                    String command = scanner.nextLine();
                    commandExecutor.execute(new Command(command, client));
                }
                
            } catch (IOException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exc) {
                return;
            }
        }

    }
    
    public void setPath(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }

}
