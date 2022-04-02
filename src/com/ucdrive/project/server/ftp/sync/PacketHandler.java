/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.server.ftp.sync;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.ucdrive.project.server.Server;

/**
 * Synchronizes files between servers
 */
public class PacketHandler {

    private File file;
    private DataOutputStream fileData;
    private int currentIndex;
    private Server server;

    // This is the constructor of the PacketHandler class. 
    public PacketHandler(Server server) {
        this.fileData = null;
        this.file = null;
        this.currentIndex = 0;
        this.server = server;
    }

    public int execute(FilePacket file) throws IOException {
        // If it's not a binaryFile, creates the directory
        if(!file.getIsBinaryFile()) {
            new File(server.getStoragePath() + file.getLocation() + file.getPath()).mkdirs();
            return 2;
        }

        // It's checking if the current packet is the next one in the sequence. If it's not, it means
        // that the packet is missing and the server should send the missing packet.
        if(currentIndex + 1 != file.getIndex()){
            return currentIndex + 1;
        }

        System.out.println("RECEIVED: (" + file.getIndex() + "/" + file.getTotalPackets() + ")");

        // It's checking if the checksum of the file is the same as the checksum that the server
        // calculated. If it's not, it means that the file is corrupted.
        if(Arrays.compare(file.getChecksum(), file.calculateChecksum(file.getBuffer())) != 0) {
            System.out.println("Checksum failed");
            return currentIndex;
        }

        currentIndex++;
        // It's checking if the current packet is the first one in the sequence. If it's the first one,
        // it means that the server is receiving a new file. So it creates a new file and writes the
        // data to it.
        if(currentIndex == 1) {
            System.out.println("Received file: " + file.getPath());
            this.file = new File(server.getStoragePath() + file.getLocation() + file.getPath());
            this.fileData = new DataOutputStream(new FileOutputStream(this.file));
        }

        int next = currentIndex + 1;

        this.fileData.write(file.getBuffer(), 0, file.getBufferLength());

        // It's checking if the current packet is the last one in the sequence. If it's the last one,
        // it means that the server is receiving the last packet of the file. So it closes the
        // file and sets the currentIndex to 0.
        if(file.getIndex() == file.getTotalPackets()) {
            this.file = null;
            fileData.close();
            currentIndex = 0;
        }

        return next;
    }

    /**
     * Delete the file if it exists
     */
    public void deleteFile() {
        if(this.file != null) {
            try {
                this.fileData.close();
                this.file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
