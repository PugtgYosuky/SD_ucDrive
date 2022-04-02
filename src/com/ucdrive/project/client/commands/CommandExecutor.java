/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.client.commands;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

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

    // Function from https://stackoverflow.com/questions/28678026/how-can-i-get-all-class-files-in-a-specific-package-in-java
    public static List<Class<?>> getClassesInPackage(String packageName) {
        String path = packageName.replaceAll("[.]", "/");
        List<Class<?>> classes = new ArrayList<>();
        String[] classPathEntries = System.getProperty("java.class.path").split(
                System.getProperty("path.separator")
        );

        String name;
        for (String classpathEntry : classPathEntries) {
            if (classpathEntry.endsWith(".jar")) {
                File jar = new File(classpathEntry);
                try {
                    JarInputStream is = new JarInputStream(new FileInputStream(jar));
                    JarEntry entry;
                    while((entry = is.getNextJarEntry()) != null) {
                        name = entry.getName();
                        if (name.endsWith(".class")) {
                            if (name.contains(path) && name.endsWith(".class")) {
                                String classPath = name.substring(0, entry.getName().length() - 6);
                                classPath = classPath.replaceAll("[\\|/]", ".");
                                classes.add(Class.forName(classPath));
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                }
            } else {
                try {
                    File base = new File(classpathEntry + File.separatorChar + path);
                    for (File file : base.listFiles()) {
                        name = file.getName();
                        if (name.endsWith(".class")) {
                            name = name.substring(0, name.length() - 6);
                            classes.add(Class.forName(packageName + "." + name));
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                }
            }
        }

        return classes;
    }

    public CommandExecutor(Client client, DataOutputStream outputStream) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        this.commands = new HashMap<>();
        this.commandDescriptions = new TreeSet<>(Comparator.comparing(CommandDescription::prefix));
        this.client = client;
        this.outputStream = outputStream;
        // Get all the classes in the given package
        for(Class<?> c : getClassesInPackage("com.ucdrive.project.client.commands.list")) {
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

