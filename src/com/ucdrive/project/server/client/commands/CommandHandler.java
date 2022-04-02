/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.server.client.commands;

import java.io.IOException;

/**
 * This class is responsible for parsing the command and executing the command.
 */
public abstract class CommandHandler {

    public static CommandExecutor commandExecutor;

    public abstract CommandAction parse(Command command) throws IOException;

}
