package com.ucdrive.project.server;

public class Server {

    public static void main(String[] args) {
        ServerTCP server = new ServerTCP(7000, "localhost", 2, "src/com/ucdrive/project/server/config/accounts.txt");

        server.start();

        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }

}
