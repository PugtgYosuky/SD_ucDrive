package com.ucdrive.project.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandExecutor;
import com.ucdrive.project.client.commands.CommandHandler;
import com.ucdrive.project.client.response.ResponseHandler;

public class Client {

    private String path;
    private InetAddress ipServerA;
    private InetAddress ipServerB;
    private int portServerA;
    private int portServerB;
    private Socket server;

    public Client(String path, InetAddress ipServerA, InetAddress ipServerB, int portServerA, int portServerB) {
        this.path = path;
        this.ipServerA = ipServerA;
        this.ipServerB = ipServerB;
        this.portServerA = portServerA;
        this.portServerB = portServerB; 
    }

    private boolean getCurrentSocket() {
        try {
            this.server = new Socket(ipServerA, portServerA);
        } catch (IOException e) {
            try {
                this.server = new Socket(ipServerB, portServerB);
            } catch (IOException exc) {
                return false;
            }
        }
        return true;
    }

    public void run() {
        ResponseHandler responseHandler = new ResponseHandler(this);
        try (Scanner scanner = new Scanner(System.in)) {
			int failOvers = 0, maxFailOvers = 10;

			while(failOvers < maxFailOvers) {
			    boolean hasServer = getCurrentSocket();

			    if(!hasServer) {
			        failOvers++;
			        System.out.println("Client couldn't connect to any server. Trying to reconnect (" + failOvers + "/" + maxFailOvers + ")");
			        
			        try {
			            Thread.sleep(1000);
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }
			        continue;
			    }

			    try (ObjectInputStream inputStream = new ObjectInputStream(server.getInputStream());
			        DataOutputStream outputStream = new DataOutputStream(server.getOutputStream());) {
			        failOvers = 0;
			        System.out.println("Connected");

			        CommandExecutor commandExecutor = new CommandExecutor(this, outputStream);
			        CommandHandler.commandExecutor = commandExecutor;

			        ReadThread readThread = new ReadThread(inputStream, responseHandler);
                    readThread.start();

			        /*
			            !!! In order to identify the moment we lose connection with the server
			        */
			        while(true) {
			            String command = scanner.nextLine();
			            CommandAction commandAction = commandExecutor.execute(new Command(command, this));
                        if(commandAction == CommandAction.CLOSE_CONNECTION) {
                            readThread.interrupt();
                            System.out.println("Client closed");
                            inputStream.close();
                            outputStream.close();
                            scanner.close();
                            return;
                        }
                    }
			        
			    } catch (IOException exc) {
			        System.out.println("Lost connection with the server. Trying to reconnect...");
			    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exc) {
			        exc.printStackTrace();
			        return;
			    }
			}
		}
    }

    public static void main(String[] args) {
        try {
            InetAddress ipServerA = InetAddress.getByName(args[0]);
            InetAddress ipServerB = InetAddress.getByName(args[1]);
            int portServerA = Integer.parseInt(args[2]);
            int portServerB = Integer.parseInt(args[3]);

            Client client = new Client(System.getProperty("user.dir"), ipServerA, ipServerB, portServerA, portServerB);

            client.run();
        } catch (UnknownHostException e) {
            e.printStackTrace();            
        }
    }
    
    public void setPath(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }

    public InetAddress getIpServerA() {
        return ipServerA;
    }
    
    public void setIpServerA(InetAddress ipServerA) {
        this.ipServerA = ipServerA;
    }
    
    public InetAddress getIpServerB() {
        return ipServerB;
    }
    
    public void setIpServerB(InetAddress ipServerB) {
        this.ipServerB = ipServerB;
    }
    
    public int getPortServerA() {
        return portServerA;
    }
    
    public int getPortServerB() {
        return portServerB;
    }
    

}
