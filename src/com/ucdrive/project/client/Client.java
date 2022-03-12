package com.ucdrive.project.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    
    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            try (Socket server = new Socket(args[0], Integer.parseInt(args[1]))) {
                System.out.println("Connected");

                DataInputStream inputStream = new DataInputStream(server.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(server.getOutputStream());

                new ReadThread(inputStream).start();

                while(true) {
                    String command = scanner.nextLine();
                    outputStream.writeUTF(command);
                }
                
            } catch (IOException exc) {
                return;
            }
        }

    }

}
