// Purpose: Main program class, prompt for and run commands

import org.jaudiotagger.tag.FieldKey;

import java.io.File;
import java.util.Scanner;

public class Program {

    private static Scanner input = new Scanner(System.in);

    // main method, provide user interface and run commands
    public static void main(String[] args) { //TODO when Version 1 finished create a releases tag on GitHub
        MP3Editor mp3Editor;
        String[] command;
        String path;

        // intro to program
        System.out.println("Welcome to \"Bulk Audio Editor\"");
        System.out.println("-------------------------------");
        System.out.println("Enter path to a single Mp3 file or a folder holding MP3 files");
        System.out.println("(enter \"help\" for more information)");

        // prompt for valid path
        path = promptForPath();

        // create mp3Editor obj and inter path to mp3 files as constructor argument
        mp3Editor = new MP3Editor(path);

        // prompt for command
        command = promptForCommand(mp3Editor.getInputPath()); // command = array [command, commandInput, output path]

        //TODO trim output path

        // check for valid output path then check if there are file conflicts
        if (!command[2].equals("")) {
            // first check output
            boolean isValidOutput = FileHandler.isPathLogical(
                    FileHandler.isFolder(path),
                    FileHandler.isFolder(command[2])
            );
            if (!isValidOutput) {
                // prompt for command
                Log.print("Re-enter command");
                command = promptForCommand(mp3Editor.getInputPath());
            }
            // if output valid then check file conflicts
            else {
                // check if there are name conflicts
                // (if conflicts exist ask if overriding ok, if override not ok then skip file)
                mp3Editor.skipOverrideDenials(command[2]);
            }
        }

        while(!command[0].equals("-q") & !command[0].equals("q")) {
            switch(command[0]) {
                case "-ab":
                    mp3Editor.addToFileName(true, command[1], command[2]);
                    break;

                case "-ae":
                    mp3Editor.addToFileName(false, command[1], command[2]);
                    break;

                case "-r":
                    mp3Editor.removeFromFileName(command[1], command[2]);
                    break;

                case "-Ar":
                    mp3Editor.modifyArtist(command[1], command[2]);
                    break;

                case "-A":
                    mp3Editor.modifyAlbum(command[1], command[2]);
                    break;

                case "-G":
                    mp3Editor.modifyGenre(command[1], command[2]);
                    break;

                case "-Art":
                    mp3Editor.changeArt(command[1], command[2]);
                    break;

                case "-DN":
                    mp3Editor.displayDataByName();
                    break;

                case "-DAr":
                    mp3Editor.displayDataByArtist();
                    break;

                case "-DA":
                    mp3Editor.displayDataByAlbum();
                    break;

                case "-DG":
                    mp3Editor.displayDataByGenre();
                    break;

                case "-DL":
                    mp3Editor.displayDataByLoudness();
                    break;

                case "-LN":
                    mp3Editor.normalizeFiles(command[2]);
                    break;

                case "-LNN":
                    mp3Editor.normalizeFiles(toDouble(command[1]), command[2]);
                    break;

                case "-n":
                    mp3Editor.setInputPath(command[1]);
                    mp3Editor.setFiles(command[1]);
                    break;

                case "-h":
                    displayHelp();
                    break;

                default:
                    System.out.println("\nPlease enter a valid command. Below is a list of all commands.");
                    System.out.println("(List of commands can be produced again using \"-h\" command)\n");
                    displayHelp();
                    break;
            }
            command = promptForCommand(mp3Editor.getInputPath());
        }

        System.out.println("\nClosing Program...");
    }

    // display a description of Bulk Audio Editor
    private static void displayAbout() {
        System.out.print("\n\"Bulk Audio Editor\" is a tool to help edit audio files in bulk. \n" +
                "A user can enter a path to a folder holding audio files such as mp3 then can run commands that can edit metadata such as: \n" +
                "- Album\n" +
                "- Artist\n" +
                "- Genre\n" +
                "- Cover Art\n" +
                "and more\n" +
                "After entering a path use the -h command to display all available commands.\n\n");
    }

    // display command options and descriptions
    private static void displayHelp() {
        String format = "%-30s %s\n";

        // TODO continue here: complete all remaining commands
        // TODO complete -DL (display loudness stats), consider removing "-" from all commands so it is one less character to type
        System.out.printf(format, "-o [output path]: ", "Output modifier can be added to all commands to specify where to save output file after command");
        System.out.printf(format, "", "\t\tWill be either folder or file path depending on initial path set");
        System.out.printf(format, "-ab [text]: ", "Add command will add [text] to beginning");
        System.out.printf(format, "-ae [text]: ", "Add command will add [text] to end");
        System.out.printf(format, "-r [text]: ", "Remove command will remove [text] from files");
        System.out.printf(format, "-Ar [artist text]: ", "Artist command will add [artist text] to artist metadata of files");
        System.out.printf(format, "-A [album text]: ", "Album command will add [album text] to album metadata of files");
        System.out.printf(format, "-G [genre text]: ", "Genre command will add [genre text] to genre metadata of files");
        System.out.printf(format, "-Art [path to art]: ", "Art command will add art in [path to art] to art metadata of files");
        System.out.printf(format, "-DN: ", "Display by name command will display the metadata of the files organized by name");
        System.out.printf(format, "-DAr: ", "Display by artist command will display the metadata of the files organized by artist name");
        System.out.printf(format, "-DA: ", "Display by album command will display the metadata of the files organized by album");
        System.out.printf(format, "-DG: ", "Display by genre command will display the metadata of the files organized by genre");
        System.out.printf(format, "-DL: ", "Display by loudness command will display the metadata and loudness stats of the files organized by loudest to quietest");
        System.out.printf(format, "-LN: ", "Loudness Normalize command will make loudness of files similar.");
        System.out.printf(format, "", "\t\tThis will allow user to listen to music without needing to change the volume. Will set loudness to -16 LUFS.");
        System.out.printf(format, "", "\t\tInternally, true peak set to -2 and loudness range set to match file's current range");
        System.out.printf(format, "-LNN [LUFS value]: ", "Loudness Normalize command (same as -LN but with custom loudness) will take a value in LUFS and bring the loudness of files to that target.");
        System.out.printf(format, "", "\t\tThis will allow user to listen to music without needing to change the volume. Recommended LUFS values are -24 to -14 (numbers closer to 0 are louder).");
        System.out.printf(format, "", "\t\tInternally, true peak set to -2 and loudness range set to match file's current range\"");
        System.out.printf(format, "-n [path to files]: ", "New command will select new set of files or file to target");
        System.out.printf(format, "-h or help: ", "Help command will display all command options and give their descriptions");
        System.out.printf(format, "-q or q: ", "Quit command will terminate program");
        System.out.println();

        System.out.println("Examples------------------------------------");
        System.out.println("Add command with and without output path modifier");
        System.out.println("-ab new-");
        System.out.println("-ab new- -o C:folder1/folder2/");
        System.out.println("original: file.mp3 >>> after command: new-file.mp3");
        System.out.println();

        System.out.println("Loudness Normalization command with and without output path modifier");
        System.out.println("-LN");
        System.out.println("-LN -o C:folder1/folder2/");
        System.out.println("Loudness Normalization command with custom loudness with and without output path modifier");
        System.out.println("-LNN -23");
        System.out.println("-LNN -o C:folder1/folder2/out-file.mp3");
    }

    // recursively prompt for path until a valid path is entered
    private static String promptForPath() {
        String path;

        // prompt for path
        System.out.print("Enter path: ");
        path = input.nextLine();

        // create path obj so the path can be validated
        File file = new File(path);

        // check if path is, if not valid then re-prompt until it is
        if (file.isDirectory() || file.isFile()) {
            return path;
        }
        else {
            Log.error("Path not found. Please enter a valid path. Ex: C:\\folder1\\folder2\\");

            if (path.equals("help")) {
                displayAbout();
            }
            return promptForPath();
        }
    }

    // prompt for command, returns [command, commandInput, outputPath] as array
    private static String[] promptForCommand(String currInputPath) {
        System.out.println("\nCurrent target path: " + currInputPath);
        System.out.print("\nEnter command: ");
        String command = input.nextLine();
        String commandInput = "";
        String outputPath = "";

        // check if user entered output modifier
        if (command.contains(" -o ")) {
            outputPath = command.substring(command.indexOf("-o ") + 3);
            command = command.replace(" -o " + outputPath, "");
        }

        // check if command has an argument
        if (command.contains(" ")) {
            // separate command and input
            commandInput = command.substring(command.indexOf(" ") + 1);
            command = command.substring(0, command.indexOf(" "));
        }
        // if not then it is a stand along command

        Log.print("command", "<" + command + ">");
        Log.print("command input", "<" + commandInput + ">");
        Log.print("out path", "<" + outputPath + ">");

        return new String[]{command, commandInput, outputPath};
    }

    private static double toDouble(String stringVal) {
        double value = 0;

        try {
            value = Double.parseDouble(stringVal);
        }
        catch (Exception e) {
            Log.errorE("Value entered is not a valid number. Try again", e);
            promptForCommand("");
        }

        return value;
    }
}