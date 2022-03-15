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

public class CommandExecutor {

    private Map<String, Function<Command, CommandAction>> commands;
    private Set<CommandDescription> commandDescriptions;

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

    public CommandExecutor() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        this.commands = new HashMap<>();
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

    public Set<CommandDescription> getCommands() {
        return this.commandDescriptions;
    }

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
