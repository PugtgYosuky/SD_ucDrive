package com.ucdrive.project.shared;

public class Message implements Response {

    private String message;

    public Message(String message) {
        this.message = message;
    }

	@Override
	public void execute() {
        System.out.println(message);
	}
    
}
