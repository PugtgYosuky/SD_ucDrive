/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.shared;

import com.ucdrive.project.client.response.ResponseHandler;

/**
 * A message is a response that contains a message
 */
public class Message implements Response {

    private String message;

   // This is the constructor of the class. It is used to create an instance of the class.
    public Message(String message) {
        this.message = message;
    }

    /**
     * Returns the message 
     * 
     * @return The message property of the object.
     */
    public String getMessage() {
        return this.message;
    }

	/**
     * Execute the response handler
     * 
     * @param response The response handler that will be called
     */
    @Override
	public void execute(ResponseHandler response) {
        response.execute(this);
	}

    /**
     * Returns the message 
     * 
     * @return The message
     */
    @Override
    public String getCommand() {
        return this.message;
    }
    
}
