package com.ucdrive.project.shared;

import com.ucdrive.project.client.response.ResponseHandler;

public class Message implements Response {

    private String message;

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

	@Override
	public void execute(ResponseHandler response) {
        response.execute(this);
	}
    
}
