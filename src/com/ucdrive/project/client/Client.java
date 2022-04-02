package com.ucdrive.project.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandExecutor;
import com.ucdrive.project.client.commands.CommandHandler;
import com.ucdrive.project.client.response.ReadThread;
import com.ucdrive.project.client.response.ResponseHandler;
import com.ucdrive.project.client.transfer.TransferDispatcher;
import com.ucdrive.project.client.transfer.TransferThread;
import com.ucdrive.project.shared.Response;
import com.ucdrive.project.shared.Transfer;

/**
 * The client is the one who interacts with the user and the server
 */
public class Client {

    private String path;
    private InetAddress ipServerA;
    private InetAddress ipServerB;
    private int portServerA;
    private int portServerB;
    private String username;
    private String password;
    private Socket server;

    // Constructor of the class. It initializes the variables of the class.
    public Client(String path, InetAddress ipServerA, InetAddress ipServerB, int portServerA, int portServerB) {
        this.path = path;
        this.ipServerA = ipServerA;
        this.ipServerB = ipServerB;
        this.portServerA = portServerA;
        this.portServerB = portServerB;
        this.username = "";
        this.password = "";
    }

    /**
     * Checks if it can connect to any server
     * 
     * @return The boolean value of whether or not the socket was successfully created.
     */
    private boolean getCurrentSocket() {
        // Trying to connect to the first server. If it fails, it tries to connect to the second
        // server.
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

    /**
     * It asks the user to insert a username and a password.
     * 
     * @param scanner The Scanner object that will be used to read the user's input.
     */
    public void login(Scanner scanner) {
        System.out.println("Insert username: ");
        this.username = scanner.nextLine();
        System.out.println("Insert password: ");
        this.password = scanner.nextLine();
    }
    
    /**
     * It reads commands from the user, executes them, and sends responses to the user
     * 
     * @param scanner The Scanner object that will be used to read commands from the user.
     * @param responseHandler The ResponseHandler object that will handle the responses from the
     * server.
     * @param transferDispatcher The transfer dispatcher used to manage the transfers.
     * @return Nothing.
     */
    public boolean readCommands(Scanner scanner, ResponseHandler responseHandler, TransferDispatcher transferDispatcher) {
        // It creates a queue of commands that will be executed by the command executor.
        Queue<Command> remainingCommands = new LinkedList<>();
        int failOvers = 0, maxFailOvers = 10;
        String command = "";
        
        while(failOvers < maxFailOvers) {
            boolean hasServer = getCurrentSocket();
            // Checks if the client can connect to any server. If it can't, it tries to connect to
            // the other server.
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
            
               // It sends the username and the password to the server.
                outputStream.writeUTF(this.username);
                outputStream.writeUTF(this.password);
                
                // read the response from the server and checks if it is connected to it
                try {
					Response res = (Response) inputStream.readObject();
                    System.out.println(res.getCommand());
                    if(!res.getCommand().equals("You are now connected! :)"))
                        return true;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
                

                // Sends the remaining commands from the prev connection to the server
                while(transferDispatcher.getSize() != 0) {
                    Transfer transfer = transferDispatcher.getTransfer();
                    
                    remainingCommands.add(new Command(transfer.getType().toString() + " " + transfer.getFilename() + " " + transfer.getClientPath(), this));

                    transferDispatcher.removeTransfer();
                }

                // It creates a thread that will read the responses from the server and send them to
                // the response handler.
                ReadThread readThread = new ReadThread(inputStream, responseHandler);
                readThread.start();
                // It creates a thread that will manage the transfers.
                TransferThread transferThread = new TransferThread(transferDispatcher);
                transferThread.start();

                // Executing the commands to be send to the server.
                while(!remainingCommands.isEmpty())
                    commandExecutor.executeServerCommand(remainingCommands.poll());

                while(true) {
                    if(command.isEmpty())
                        command = scanner.nextLine();
                    CommandAction commandAction = commandExecutor.execute(new Command(command, this));
                    switch(commandAction) {
                        // It interrupts the read thread and the transfer thread, closes the input and
                        // output streams
                        case RETRY:
                            readThread.interrupt();
                            transferThread.interrupt();
                            inputStream.close();
                            outputStream.close();
                            break;
                        // It closes the connection with the server and returns to the login method.
                        case CLOSE_CONNECTION:
                            System.out.println("Client closed");
                            readThread.interrupt();
                            transferThread.interrupt();
                            inputStream.close();
                            outputStream.close();
                            command = "";
                            return false;
                        // Changing the password of the user.
                        case CHANGE_PASSWORD:
                            System.out.println("Password changed");
                            readThread.interrupt();
                            transferThread.interrupt();
                            inputStream.close();
                            outputStream.close();
                            command = "";
                            return true;
                        default:
                            command = "";
                            break;
                    }

                    // Breaking the loop and returning to the login method.
                    if(commandAction == CommandAction.RETRY)
                        break;
                }
            } // When the client can't connect to the
            // server.
            catch (IOException exc) {
                System.out.println("Lost connection with the server. Trying to reconnect...");
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exc) {
                exc.printStackTrace();
                return false;
            }
        }
        return false;
    }

   /**
    * Reads commands from the user and executes them
    */
    public void run() {
        TransferDispatcher transferDispatcher = new TransferDispatcher();
        ResponseHandler responseHandler = new ResponseHandler(transferDispatcher);
        
        try (Scanner scanner = new Scanner(System.in)) {
            while(true) {
                login(scanner);

                if(!readCommands(scanner, responseHandler, transferDispatcher))
                    return;
            }
		}
    }

   /**
    * It creates a client that connects to the server.
    */
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

   /**
     * The getPassword() function returns the value of the password variable
     * 
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * The setter method for the password property
     * 
     * @param password The password to be used for authentication.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * The getUsername() function returns the value of the username variable
     * 
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * The setter method for the username
     * 
     * @param username The username of the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Returns the user path
     * 
     * @return The path variable.
     */
    public String getPath(){
        return path;
    }
    
    /**
     * `setPath` sets the path of the file
     * 
     * @param path The client path
     */
    public void setPath(String path){
        this.path = path;
    }

    /**
     * Get the IP address of the server A
     * 
     * @return The IP address of the server A.
     */
    public InetAddress getIpServerA() {
        return ipServerA;
    }
    
    /**
     * The setter method for the ipServerA field
     * 
     * @param ipServerA The IP address of the server A.
     */
    public void setIpServerA(InetAddress ipServerA) {
        this.ipServerA = ipServerA;
    }
    
    /**
     * Get the IP address of the server B
     * 
     * @return The IP address of the server B.
     */
    public InetAddress getIpServerB() {
        return ipServerB;
    }
    
    /**
     * The setter method for the ipServerB property
     * 
     * @param ipServerB The IP address of the server B.
     */
    public void setIpServerB(InetAddress ipServerB) {
        this.ipServerB = ipServerB;
    }
    
    /**
     * Returns the port number of the server A
     * 
     * @return The port number of the server A.
     */
    public int getPortServerA() {
        return portServerA;
    }
    
    /**
     * Returns the port number of the server B
     * 
     * @return The port number of the server B.
     */
    public int getPortServerB() {
        return portServerB;
    }
    

}
