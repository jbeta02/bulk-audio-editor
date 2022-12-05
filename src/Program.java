// Purpose: Main program class, prompt for and run commands

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
        command = promptForCommand(); // command = array [command, commandInput, output path]

        while(!command[0].equals("-q") & !command[0].equals("q")) {
            switch(command[0]) {
                case "-ab":
                    mp3Editor.addToFileName(true, command[1]);
                    break;

                case "-ae":
                    mp3Editor.addToFileName(false, command[1]);
                    break;

                case "-r":
                    mp3Editor.removeFromFileName(command[1]);
                    break;

                case "-Ar":
                    mp3Editor.modifyArtist(command[1]);
                    break;

                case "-A":
                    mp3Editor.modifyAlbum(command[1]);
                    break;

                case "-G":
                    mp3Editor.modifyGenre(command[1]);
                    break;

                case "-Art":
                    mp3Editor.changeArt(command[1]);
                    break;

                case "-D":
                    mp3Editor.displayData();
                    break;

                case "-LN":
                    mp3Editor.normalizeFiles(command[2]);
                    break;

                case "-LNN":
                    if (isDouble(command[1])) {
                        mp3Editor.normalizeFiles(Double.parseDouble(command[1]), command[2]);
                    }
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
            command = promptForCommand();
        }
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

        // TODO complete -DL (display loudness stats), -DA (display by album) -n and -q command
        System.out.printf(format, "-o [output path]: ", "Output modifier can be added to all commands to specify where to save output file after command");
        System.out.printf(format, "", "\t\tWill be either folder or file path depending on initial path set");
        System.out.printf(format, "-ab [text]: ", "Add command will add [text] to beginning");
        System.out.printf(format, "-ae [text]: ", "Add command will add [text] to end");
        System.out.printf(format, "-r [text]: ", "Remove command will remove [text] from files");
        System.out.printf(format, "-Ar [artist text]: ", "Artist command will add [artist text] to artist metadata of files");
        System.out.printf(format, "-A [album text]: ", "Album command will add [album text] to album metadata of files");
        System.out.printf(format, "-G [genre text]: ", "Genre command will add [genre text] to genre metadata of files");
        System.out.printf(format, "-Art [path to art]: ", "Art command will add art in [path to art] to art metadata of files");
        System.out.printf(format, "-D: ", "Display command will display the metadata of the files");
        System.out.printf(format, "-LN: ", "Loudness Normalize command will make loudness of files similar.");
        System.out.printf(format, "", "\t\tThis will allow user to listen to music without needing to change the volume. Will set loudness to -16 LUFS.");
        System.out.printf(format, "", "\t\tInternally, true peak set to -2 and loudness range set to match file's current range");
        System.out.printf(format, "-LNN [LUFS value]: ", "Loudness Normalize command (same as -LN but with custom loudness) will take a value in LUFS and bring the loudness of files to that target.");
        System.out.printf(format, "", "\t\tThis will allow user to listen to music without needing to change the volume. Recommended LUFS values are -24 to -14 (numbers closer to 0 are louder).");
        System.out.printf(format, "", "\t\tInternally, true peak set to -2 and loudness range set to match file's current range\"");
        System.out.printf(format, "-h or help: ", "Help command will display all command options and give their descriptions");
        System.out.printf(format, "-n [path to files]: ", "New command will select new set of files or file to target");
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
    private static String[] promptForCommand() {
        System.out.print("\nEnter command: ");
        String command = input.nextLine();
        String commandInput = "";
        String outputPath = "";

        // check if user entered output modifier
        //TODO make sure output same type as path type (file or folder)
        // check override behavior
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

    private static boolean isDouble(String stringVal) {
        boolean valid = false;

        try {
            Double.parseDouble(stringVal);
            valid = true;
        }
        catch (Exception e) {
            Log.errorE("Value entered is not a valid number. Try again", e);
            promptForCommand();
        }

        return valid;
    }
}