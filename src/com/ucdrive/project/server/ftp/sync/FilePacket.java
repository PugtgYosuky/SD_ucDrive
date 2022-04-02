/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.server.ftp.sync;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class represents a file that is to be sent to the server.
 */
public class FilePacket implements SyncPacket{
    private int index; // starts at 1
    private int totalPackets;
    private byte [] buffer;
    private int bufferLength;
    private String path; // id
    private boolean isBinaryFile;
    private String location;
    private byte [] checksum;

    // This is the constructor for the FilePacket class. 
    public FilePacket(int index, int totalPackets, byte[] buffer, String path, boolean isBinaryFile, String location) {
        this.index = index;
        this.totalPackets = totalPackets;
        this.buffer = buffer;
        this.path = path;
        this.isBinaryFile = isBinaryFile;
        this.location = location;
    }
    
    // This is the constructor for the FilePacket class.
    public FilePacket (int index, int totalPackets, String path, boolean isBinaryFile, String location){
        this.index = index;
        this.totalPackets = totalPackets;
        this.path = path;
        this.isBinaryFile = isBinaryFile;
        this.location = location;
    }
    public byte[] getChecksum() {
        return checksum;
    }

    public void setChecksum(byte[] checksum) {
        this.checksum = checksum;
    }

    public byte[] getBuffer() {
        return buffer;
    }
    
    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
        this.checksum = calculateChecksum(buffer);
    }
    
    public int getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(int bufferLength) {
        this.bufferLength = bufferLength;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTotalPackets() {
        return totalPackets;
    }

    public void setTotalPackets(int totalPackets) {
        this.totalPackets = totalPackets;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }

    public boolean getIsBinaryFile(){
        return this.isBinaryFile;
    }
    
    public void setIsBinaryFile(boolean isBinaryFile) {
        this.isBinaryFile = isBinaryFile;
    }

    public String getLocation() {
        return location;
    }

    public void setBinaryFile(boolean isBinaryFile) {
        this.isBinaryFile = isBinaryFile;
    }

    /**
     * Calculate the MD5 checksum of the buffer
     * 
     * @param buffer The buffer to calculate the checksum for.
     * @return The checksum.
     */
    public byte[] calculateChecksum(byte[] buffer){
        byte [] checksum = null;
        try {
			MessageDigest md = MessageDigest.getInstance("MD5");
            checksum = md.digest(this.buffer);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return checksum;
    }

    /**
     * This is the function that will be called by the server to execute the packet
     * 
     * @param packet The packet that is being processed.
     * @return A integer
     */
    @Override
    public int execute(PacketHandler packet) throws IOException {
        return packet.execute(this);
    }

}
