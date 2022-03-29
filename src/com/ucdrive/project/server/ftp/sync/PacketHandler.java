package com.ucdrive.project.server.ftp.sync;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.ucdrive.project.server.Server;

public class PacketHandler {

    private DataOutputStream fileData;
    private int currentIndex;
    private Server server;

    public PacketHandler(Server server) {
        this.fileData = null;
        this.currentIndex = 0;
        this.server = server;
    }

    public int execute(FilePacket file) throws IOException {
        if(!file.getIsBinaryFile()) {
            new File(server.getStoragePath() + file.getLocation() + file.getPath()).mkdir();
            return 2;
        }

        if(currentIndex + 1 != file.getIndex()){
            return currentIndex + 1;
        }

        System.out.println("RECEIVED: (" + file.getIndex() + "/" + file.getTotalPackets() + ")");

        if(Arrays.compare(file.getChecksum(), file.calculateChecksum(file.getBuffer())) != 0) {
            System.out.println("Checksum failed");
            return currentIndex;
        }

        currentIndex++;
        if(currentIndex == 1) {
            System.out.println("Received file: " + file.getPath());
            fileData = new DataOutputStream(new FileOutputStream(server.getStoragePath() + file.getLocation() + file.getPath()));
        }

        int next = currentIndex + 1;

        fileData.write(file.getBuffer(), 0, file.getBufferLength());

        if(file.getIndex() == file.getTotalPackets()) {
            fileData.close();
            currentIndex = 0;
        }

        return next;
    }

}
