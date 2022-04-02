package com.ucdrive.project.shared;

import java.net.InetAddress;

import com.ucdrive.project.client.response.ResponseHandler;
import com.ucdrive.project.server.ftp.RequestType;

/**
 * A transfer request is a request to transfer a file from the client to the server
 */
public class Transfer implements Response{
	private String id;
	private String filename;
	private int port;
	private RequestType type;
	private InetAddress ip;
	private String clientPath;
	
	// This is the constructor of the Transfer class. It is used to initialize the fields of the class.
	public Transfer(String id, String filename, int port, RequestType type, InetAddress ip, String clientPath){
		this.id = id;
		this.filename = filename;
		this.port = port;
		this.type = type;
		this.ip = ip;
		this.clientPath = clientPath;
	}
	/**
	 * It executes the response handler.
	 * 
	 * @param response The response handler that will be used to send the response.
	 */
	@Override
	public void execute(ResponseHandler response) {
		response.execute(this);
	}

	/**
	 * Returns the name of the file
	 * 
	 * @return The filename.
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Get the id of the current user
	 * 
	 * @return The id field of the object.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the port number of the server
	 * 
	 * @return The port number.
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Returns the type of the request
	 * 
	 * @return The type of request.
	 */
	public RequestType getType() {
		return type;
	}

	/**
	 * Get the IP address 
	 * 
	 * @return The IP address 
	 */
	public InetAddress getIp() {
		return ip;
	}

	/**
	 * Returns the path of the client
	 * 
	 * @return The client path.
	 */
	public String getClientPath(){
		return this.clientPath;
	}

	/**
	 * Returns the name of the command
	 * 
	 * @return The command name.
	 */
	@Override
	public String getCommand() {
		return "transfer";
	}

}
