package seedu.task.logic.commands;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.awt.Desktop;

import seedu.task.commons.core.Config;
import seedu.task.commons.core.EventsCenter;
import seedu.task.commons.util.ConfigUtil;
import seedu.task.commons.util.FileUtil;
import seedu.task.commons.core.LogsCenter;
import seedu.task.commons.events.ui.ExitAppRequestEvent;

// @@author A0147944U
/**
 * Changes working task manager data to data at specified directory.
 */
public class DirectoryCommand extends Command {

    private static final Logger logger = LogsCenter.getLogger(ConfigUtil.class);

    public static final String COMMAND_WORD = "directory";

    public static final String COMMAND_WORD_ALT = "dir";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Load TaskManager with data in given directory. \n"
            + "Parameters: directory/filename OR filename\n" + "Example: " + COMMAND_WORD
            + " c:/Users/user/Desktop/TaskManagerBackup1 OR TaskManagerBackup2";

    public static final String MESSAGE_NEW_DIRECTORY_SUCCESS = "New data: %1$s";

    public static final String MESSAGE_UNSUPPORTED_OPERATING_SYSTEM = "Unsupported operating system, please manually close application and start it again.";

    public static final String MESSAGE_FILE_NOT_FOUND_ERROR = "File does not exist: %1$s";

    /* This constant string variable is file extension of the storage file. */
    private final String FILE_EXTENSION = ".xml";

    /* This is the path of the selected storage file. */
    private String _destination;

    /* This is the path of the selected storage file. */
    private Boolean _isAbleToRestart;

    public DirectoryCommand(String newFilePath) {
        appendExtension(newFilePath);
        // Check if file supplied by user actually exists
        if (new File(_destination).exists()) {
            Config config = getConfig();
            updateConfigWithNewFilePath(config);
            saveConfig(config);
        }
    }

    /**
     * Change TaskManager file path in Config.
     * 
     * @param config
     *            Config to update
     */
    private void updateConfigWithNewFilePath(Config config) {
        config.setTaskManagerFilePath(_destination);
    }

    /**
     * Saves changes made to Config.
     * 
     * @param config
     *            Config file with updated data
     */
    private void saveConfig(Config config) {
        try {
            ConfigUtil.saveConfig(config, "config.json");
        } catch (IOException e) {
            logger.warning("Error saving to config file : " + e);
            e.printStackTrace();
        }
    }

    /**
     * Retrieves Config file.
     * 
     * @return the deserialized config
     */
    private Config getConfig() {
        Config config = new Config();
        File configFile = new File("config.json");
        try {
            config = FileUtil.deserializeObjectFromJsonFile(configFile, Config.class);
        } catch (IOException e) {
            logger.warning("Error reading from config file " + "config.json" + ": " + e);
        }
        return config;
    }

    /**
     * Appends FILE_EXTENSION to given destination.
     * This ensures user will not accidentally override non.xml files.
     * 
     * @param destination
     *            path of new data file provided by user
     */
    private void appendExtension(String destination) {
        if (destination != null) {
            _destination = destination + FILE_EXTENSION;
        }
    }

    @Override
    public CommandResult execute(boolean isUndo) {
        // Check if file supplied by user exists
        if (!new File(_destination).exists()) {
            return new CommandResult(String.format(MESSAGE_FILE_NOT_FOUND_ERROR, _destination));
        }

        assert model != null;

        // Check if Desktop is supported by Platform as it is required to
        // restart itself automatically
        if (!isOperatingSystemSupported()) {
            return new CommandResult(String.format(MESSAGE_UNSUPPORTED_OPERATING_SYSTEM));
        }
        restartTaskManager();
        if (_isAbleToRestart) {
            // Shut down current instance of TaskManager
            EventsCenter.getInstance().post(new ExitAppRequestEvent());
            return new CommandResult(String.format(MESSAGE_NEW_DIRECTORY_SUCCESS, _destination));
        } else {
            return new CommandResult(String.format(MESSAGE_UNSUPPORTED_OPERATING_SYSTEM));
        }
    }

    /**
     * Checks if Operating System is supported by Desktop
     * 
     * @return true if Operating System is supported, false otherwise
     */
    private Boolean isOperatingSystemSupported() {
        if (Desktop.isDesktopSupported()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Run TaskManager.jar located at root directory
     */
    private void restartTaskManager() {
        logger.info("============================ [ Restarting Task Manager ] =============================");
        File Taskmanager = new File("TaskManager.jar");
        Desktop desktop = Desktop.getDesktop();
        if (Taskmanager.exists()) {
            try {
                desktop.open(Taskmanager);
                _isAbleToRestart = true;
            } catch (IOException e) {
                e.printStackTrace();
                _isAbleToRestart = false;
            }
        } else {
            _isAbleToRestart = false;
        }
    }

    /* Alternative method to restart TaskManger on Windows,keeping for knowledge
    //**
     * Locates TaskManager.jar file and silently run it via Windows Command Line
     *//*
    private void restartTaskManagerOnWindows() {
        logger.info("============================ [ Restarting Task Manager ] =============================");
        String command = "";
        String filePath = Paths.get(".").toAbsolutePath().normalize().toString() + "\\";
        command = "/c cd /d \"" + filePath + "\" & TaskManager.jar & exit";
        logger.info("DOS command generated:" + command);
        try {
            new ProcessBuilder("cmd", command).start();
        } catch (IOException e) {
            logger.warning("Error starting process. " + e);
        }
    }*/

}