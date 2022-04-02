package com.ucdrive.project.server.client.commands;

import java.io.BufferedReader;
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

import com.ucdrive.project.server.Server;
import com.ucdrive.project.server.ftp.ServerFTP;
import com.ucdrive.project.server.ftp.sync.FileDispatcher;

public class CommandExecutor {

    private Map<String, Function<Command, CommandAction>> commands;
    private Set<CommandDescription> commandDescriptions;
    private FileDispatcher fileDispatcher;
    private Server server;
    private ServerFTP serverFTP;

    /**
     * Get all the classes in a package
     * 
     * @param packageName The name of the package to be searched.
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

    // Initializing the command executor.
    public CommandExecutor(FileDispatcher fileDispatcher, Server server, ServerFTP serverFTP) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        this.commands = new HashMap<>();
        this.fileDispatcher = fileDispatcher;
        this.server = server;
        this.serverFTP = serverFTP;
        this.commandDescriptions = new TreeSet<>(Comparator.comparing(CommandDescription::prefix));

        for(Class<?> c : getPackageClasses("com.ucdrive.project.server.client.commands.list")) {
            if(c.isAnnotationPresent(CommandDescription.class)) {
                CommandDescription commandDescription = c.getAnnotation(CommandDescription.class);
                this.commandDescriptions.add(commandDescription);
                String prefix = commandDescription.prefix();
                CommandHandler commandHandler = (CommandHandler) c.getConstructor().newInstance();
                this.commands.put(prefix, command -> {
                    try {
                        return commandHandler.parse(command);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
            }
        }
    }

    public ServerFTP getServerFTP() {
        return serverFTP;
    }

    public FileDispatcher getFileDispatcher() {
        return fileDispatcher;
    }

    public void setFileDispatcher(FileDispatcher fileDispatcher) {
        this.fileDispatcher = fileDispatcher;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
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
     * If the command is found, execute it. Otherwise, return a CommandAction.NOT_FOUND
     * 
     * @param command The command that was sent by the client.
     * @return Nothing.
     */
    public CommandAction execute(Command command) {
        if(this.commands.get(command.getPrefix()) == null) {
            try {
                command.getClient().sendMessage("Command not found");
            } catch(IOException exc) {
                return CommandAction.CLOSE_CONNECTION;
            }
            return CommandAction.NOT_FOUND;
        } else {
            return this.commands.get(command.getPrefix()).apply(command);
        }
    }

}
