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
import com.ucdrive.project.shared.Response;

public class Client {

    private String path;
    private InetAddress ipServerA;
    private InetAddress ipServerB;
    private int portServerA;
    private int portServerB;
    private String username;
    private String password;
    private Socket server;

    public Client(String path, InetAddress ipServerA, InetAddress ipServerB, int portServerA, int portServerB) {
        this.path = path;
        this.ipServerA = ipServerA;
        this.ipServerB = ipServerB;
        this.portServerA = portServerA;
        this.portServerB = portServerB;
        this.username = "";
        this.password = "";
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

    public void login(Scanner scanner) {
        System.out.println("Insert username: ");
        this.username = scanner.nextLine();
        System.out.println("Insert password: ");
        this.password = scanner.nextLine();
    }
    public boolean readCommands(Scanner scanner, ResponseHandler responseHandler){
        int failOvers = 0, maxFailOvers = 10;
        String command = "";
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

                CommandExecutor commandExecutor = new CommandExecutor(this, outputStream);
                CommandHandler.commandExecutor = commandExecutor;
            
                outputStream.writeUTF(this.username);
                outputStream.writeUTF(this.password);
                
                try {
					Response res = (Response) inputStream.readObject();
                    System.out.println(res.getCommand());
                    if(!res.getCommand().equals("You are now connected! :)"))
                        return true;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

                ReadThread readThread = new ReadThread(inputStream, responseHandler);
                readThread.start();

                while(true) {
                    if(command.isEmpty())
                        command = scanner.nextLine();
                    CommandAction commandAction = commandExecutor.execute(new Command(command, this));
                    command = "";
                    switch(commandAction) {
                        case CLOSE_CONNECTION:
                            readThread.interrupt();
                            System.out.println("Client closed");
                            inputStream.close();
                            outputStream.close();
                            return false;
                        case CHANGE_PASSWORD:
                            System.out.println("Password changed");
                            readThread.interrupt();
                            inputStream.close();
                            outputStream.close();
                            return true;
                        default:
                            break;
                    }
                }
            } catch (IOException exc) {
                System.out.println("Lost connection with the server. Trying to reconnect...");
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exc) {
                exc.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void run() {
        ResponseHandler responseHandler = new ResponseHandler(this);
        
        try (Scanner scanner = new Scanner(System.in)) {
            while(true) {
                login(scanner);

                if(!readCommands(scanner, responseHandler))
                    return;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPath(){
        return path;
    }
    
    public void setPath(String path){
        this.path = path;
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
