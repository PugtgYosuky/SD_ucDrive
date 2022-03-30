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

public class CommandExecutor {

    private Map<String, Function<Command, CommandAction>> commands;
    private Set<CommandDescription> commandDescriptions;
    private Client client;
    private DataOutputStream outputStream;

    public Set<Class<?>> getPackageClasses(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines().filter(line -> line.endsWith(".class")).map(line -> getClass(line, packageName)).collect(Collectors.toSet());
    }

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

    public Set<CommandDescription> getCommands() {
        return this.commandDescriptions;
    }

    public CommandAction executeServerCommand(Command command) throws IOException {
        outputStream.writeUTF(command.getCommand());
        return CommandAction.SUCCESS;
    }

    public CommandAction execute(Command command) throws IOException {
        if(this.commands.get(command.getPrefix()) == null) {
            return executeServerCommand(command);
        } else {
            return this.commands.get(command.getPrefix()).apply(command);
        }
    }
    
    public Client getClient(){
        return client;
    }

}

