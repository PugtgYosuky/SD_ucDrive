/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.client.commands;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ucdrive.project.client.Client;

/**
 * The class is responsible for executing the commands that are send to the server. It also contains
 * the list of all the commands that are available to the client
 */
public class CommandExecutor {

    private Map<String, Function<Command, CommandAction>> commands;
    private Set<CommandDescription> commandDescriptions;
    private Client client;
    private DataOutputStream outputStream;

    /**
     * Given a package name, return a set of all the classes in that package
     * 
     * @param packageName The name of the package to search for classes.
     * @return A set of classes.
     */
    public Set<Class<?>> getPackageClasses(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines().filter(line -> line.endsWith(".class")).map(line -> getClass(line, packageName)).collect(Collectors.toSet());
    }

    /**
     * Given a class name and a package name, return the class object
     * 
     * @param className The name of the class to load.
     * @param packageName The name of the package where the class is located.
     * @return The class object.
     */
    public Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf(".")));
        } catch(Exception exc) {
            return null;
        }
    }

    public CommandExecutor(Client client, DataOutputStream outputStream) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        this.commands = new HashMap<>();
        this.commandDescriptions = new TreeSet<>(Comparator.comparing(CommandDescription::prefix));
        this.client = client;
        this.outputStream = outputStream;
        // Get all the classes in the given package
        for(Class<?> c : getPackageClasses("com.ucdrive.project.client.commands.list")) {
            // Check if the CommandDescription annotation is present in the class
            if(c.isAnnotationPresent(CommandDescription.class)) {
                // If the annotation is present, we will get the comment description
                CommandDescription commandDescription = c.getAnnotation(CommandDescription.class);
                // Add the command description to the list of commands
                this.commandDescriptions.add(commandDescription);
                // Get the command prefix
                String prefix = commandDescription.prefix();
                // Create a new instance of the class commandHandler (CommandHandler should be the super class of the class c)
                CommandHandler commandHandler = (CommandHandler) c.getConstructor().newInstance();
                // Add the commandHandler.parse function to the map prefix -> function
                this.commands.put(prefix, command -> {
                    try {
                        return commandHandler.parse(command);
                    } catch (IOException e) {
                        // in case we lost connection to the server, we try to reconnect again
                        return CommandAction.RETRY;
                    }
                });
            }
        }
    }

    /**
     * Returns a set of command descriptions
     * 
     * @return The set of command descriptions.
     */
    public Set<CommandDescription> getCommands() {
        return this.commandDescriptions;
    }

    /**
     * Send the command to the server
     * 
     * @param command The command that was sent by the client.
     * @return CommandAction.SUCCESS
     */
    public CommandAction executeServerCommand(Command command) throws IOException {
        outputStream.writeUTF(command.getCommand());
        return CommandAction.SUCCESS;
    }

    /**
     * If the command is a server command, sends it. Otherwise, executes it
     * 
     * @param command The command that was sent by the user.
     * @return The CommandAction object that is created by the parse method or CommandAction.SUCESS
     */
    public CommandAction execute(Command command) throws IOException {
        if(this.commands.get(command.getPrefix()) == null) {
            return executeServerCommand(command);
        } else {
            return this.commands.get(command.getPrefix()).apply(command);
        }
    }
    
    /**
     * It returns the client object.
     * 
     * @return The client object.
     */
    public Client getClient(){
        return client;
    }

}

