package com.ucdrive.project.shared;

import java.net.InetAddress;

import com.ucdrive.project.client.response.ResponseHandler;
import com.ucdrive.project.server.ftp.RequestType;

public class Transfer implements Response{
	private String id;
	private String filename;
	private int port;
	private RequestType type;
	private InetAddress ip;
	
	public Transfer(String id, String filename, int port, RequestType type, InetAddress ip){
		this.id = id;
		this.filename = filename;
		this.port = port;
		this.type = type;
		this.ip = ip;
	}
	@Override
	public void execute(ResponseHandler response) {
		response.execute(this);
	}

	public String getFilename() {
		return filename;
	}

	public String getId() {
		return id;
	}

	public int getPort() {
		return port;
	}
	
	public RequestType getType() {
		return type;
	}

	public InetAddress getIp() {
		return ip;
	}

	@Override
	public String getCommand() {
		return "transfer";
	}

}
