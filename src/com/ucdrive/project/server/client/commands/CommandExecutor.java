/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.server.client.commands;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

import com.ucdrive.project.server.Server;
import com.ucdrive.project.server.ServerFTP;
import com.ucdrive.project.server.ftp.sync.FileDispatcher;

/**
 * The command executor is responsible for executing commands sent by the client
 */
public class CommandExecutor {

    private Map<String, Function<Command, CommandAction>> commands;
    private Set<CommandDescription> commandDescriptions;
    private FileDispatcher fileDispatcher;
    private Server server;
    private ServerFTP serverFTP;

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

    // Initializing the command executor.
    public CommandExecutor(FileDispatcher fileDispatcher, Server server, ServerFTP serverFTP) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        this.commands = new HashMap<>();
        this.fileDispatcher = fileDispatcher;
        this.server = server;
        this.serverFTP = serverFTP;
        this.commandDescriptions = new TreeSet<>(Comparator.comparing(CommandDescription::prefix));

        for(Class<?> c : getClassesInPackage("com.ucdrive.project.server.client.commands.list")) {
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

    /**
     * Returns the server FTP object
     * 
     * @return The serverFTP object.
     */
    public ServerFTP getServerFTP() {
        return serverFTP;
    }

   /**
    * Get the file dispatcher
    * 
    * @return The FileDispatcher object.
    */
    public FileDispatcher getFileDispatcher() {
        return fileDispatcher;
    }

    /**
     * Set the file dispatcher
     * 
     * @param fileDispatcher The FileDispatcher object 
     */
    public void setFileDispatcher(FileDispatcher fileDispatcher) {
        this.fileDispatcher = fileDispatcher;
    }

    /**
     * Return the server object
     * 
     * @return The server object.
     */
    public Server getServer() {
        return server;
    }

    /**
     * Sets the server property 
     * 
     * @param server The server 
     */
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
     * @return CommandAction
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
