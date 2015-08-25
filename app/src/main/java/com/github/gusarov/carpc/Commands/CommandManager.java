package com.github.gusarov.carpc.Commands;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CommandManager {
	static CommandManager _instance = new CommandManager();

	public static CommandManager getInstance() {
		return _instance;
	}

	private CommandManager() {
	}

	List<Command> _commands;
	Map<String, Command> _commandsMap;

	public List<Command> getAllCommands() throws IOException, URISyntaxException, ClassNotFoundException, IllegalAccessException, InstantiationException {
		if (_commands == null) {
			_commands = new ArrayList<Command>();
			_commandsMap = new HashMap<String, Command>();
			/*
			ArrayList<String> names = getClassNamesFromPackage("com.github.gusarov.carpc.Commands");
			for (int i = 0; i < names.size(); i++) {
				// Log.i("test", "test" + names.get(i));
				String name = "com.github.gusarov.carpc.Commands."+names.get(i);
				Class type = Class.forName(name);
				if (Command.class.isAssignableFrom(type) && type != Command.class){
					Command c = (Command)type.newInstance();
					_commands.add(c);
				}
			}
			*/
			_commands.add(new NothingCommand());
			_commands.add(new VolumeUpCommand());
			_commands.add(new VolumeDownCommand());
			//_commands.add(new PlayerNextCommand());
			//_commands.add(new PlayerPrevCommand());
			//_commands.add(new PlayerPlayPauseCommand());

			for (Command command : _commands) {
				_commandsMap.put(command.getCode(), command);
			}
		}
		return _commands;
	}

	public Command getCommand(String code) {
		return (Command)_commandsMap.get(code);
	}

	public static ArrayList<String> getClassNamesFromPackage(String packageName) throws IOException, URISyntaxException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL packageURL;
		ArrayList<String> names = new ArrayList<String>();;

		packageName = packageName.replace(".", "/");
		packageURL = classLoader.getResource(packageName);

		if(packageURL.getProtocol().equals("jar")){
			String jarFileName;
			JarFile jf ;
			Enumeration<JarEntry> jarEntries;
			String entryName;

			// build jar file name, then loop through zipped entries
			jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
			jarFileName = jarFileName.substring(5,jarFileName.indexOf("!"));
			System.out.println(">"+jarFileName);
			jf = new JarFile(jarFileName);
			jarEntries = jf.entries();
			while(jarEntries.hasMoreElements()){
				entryName = jarEntries.nextElement().getName();
				if(entryName.startsWith(packageName) && entryName.length()>packageName.length()+5){
					entryName = entryName.substring(packageName.length(),entryName.lastIndexOf('.'));
					names.add(entryName);
				}
			}

			// loop through files in classpath
		} else {
			URI uri = new URI(packageURL.toString());
			File folder = new File(uri.getPath());
			// won't work with path which contains blank (%20)
			// File folder = new File(packageURL.getFile());
			File[] contenuti = folder.listFiles();
			String entryName;
			for(File actual: contenuti){
				entryName = actual.getName();
				entryName = entryName.substring(0, entryName.lastIndexOf('.'));
				names.add(entryName);
			}
		}
		return names;
	}
}
