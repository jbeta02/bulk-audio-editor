import org.jaudiotagger.tag.FieldKey;

import java.io.File;
import java.util.Scanner;

// Purpose: Main program class, prompt for and run commands
public class Program {

    private static Scanner input = new Scanner(System.in);

    // main method, provide user interface and run commands
    public static void main(String[] args) {
        AudioEditor audioEditor;
        String[] command;
        String path;

        // intro to program
        System.out.println("Welcome to \"Bulk Audio Editor\"     v1.1.0");
        System.out.println("-------------------------------");
        System.out.println("Enter path to a single Audio file or a folder holding Audio files");
        System.out.println("(enter h for more information)");

        // prompt for valid path
        path = promptForPath();

        // create AudioEditor obj and inter path to audio files as constructor argument
        audioEditor = new AudioEditor(path);

        // prompt for command
        command = promptForCommand(audioEditor.getInputPath(), audioEditor); // command = array [command, commandInput, output path]

        while(!command[0].equals("q")) {
            switch(command[0]) {
                case "ab":
                    // add to beginning of file name
                    audioEditor.addToFileName(true, command[1], command[2]);
                    break;

                case "ae":
                    // add to end of file name
                    audioEditor.addToFileName(false, command[1], command[2]);
                    break;

                case "r":
                    // remove from file name
                    audioEditor.removeFromFileName(command[1], command[2]);
                    break;

                case "Ar":
                    // modify artist
                    audioEditor.modifyMetadata(FieldKey.ARTIST, command[1], command[2]);
                    break;

                case "A":
                    // modify album
                    audioEditor.modifyMetadata(FieldKey.ALBUM, command[1], command[2]);
                    break;

                case "G":
                    // modify genre
                    audioEditor.modifyMetadata(FieldKey.GENRE, command[1], command[2]);
                    break;

                case "Art":
                    // change cover art
                    audioEditor.changeArt(command[1], command[2]);
                    break;

                // normalize using default value of -16 LU
                case "LN":
                    audioEditor.normalizeFiles(command[2]);
                    break;

                // normalize with custom loudness
                case "LNN":
                    // make sure value is double
                    try {
                        Double.parseDouble(command[1]);
                    }
                    catch (Exception e) {
                        Log.errorE("Value entered is not a valid number. Try again", e);
                        command = promptForCommand(audioEditor.getInputPath(), audioEditor);
                    }
                    // enter custom loudness as double
                    double loudnessValue = Double.parseDouble(command[1]);
                    audioEditor.normalizeFiles(loudnessValue, command[2]);
                    break;

                case "ffAr":
                    // create folders by artist
                    audioEditor.createFoldersFor(FieldKey.ARTIST, command[2]);
                    break;

                case "ffA":
                    // create folders by album
                    audioEditor.createFoldersFor(FieldKey.ALBUM, command[2]);
                    break;

                case "DN":
                    // display data by name
                    audioEditor.displayDataByName();
                    break;

                case "DAr":
                    // display data by artist
                    audioEditor.displayDataByArtist();
                    break;

                case "DA":
                    // display data by album
                    audioEditor.displayDataByAlbum();
                    break;

                case "DG":
                    // display data by genre
                    audioEditor.displayDataByGenre();
                    break;

                case "DL":
                    // display data by loudness
                    audioEditor.displayDataByLoudness();
                    break;

                case "n":
                    // set new input path
                    audioEditor.setInputPath(command[1]);
                    audioEditor.setFiles(command[1]);
                    break;

                case "h":
                    displayHelp();
                    break;

                default:
                    System.out.println("\nPlease enter a valid command.");
                    System.out.println("(List of commands can be produced using \"h\" command)\n");
                    break;
            }
            command = promptForCommand(audioEditor.getInputPath(), audioEditor);
        }

        System.out.println("\nClosing Program...");
    }

    // Program utility functions for assisting user

    // display a description of Bulk Audio Editor
    private static void displayAbout() {
        System.out.print("\n\"Bulk Audio Editor\" is a tool to help edit and manage audio files in bulk. (Currently supports flac and mp3) \n" +
                "A user can enter a path to a folder holding audio files such as mp3 then run commands that can edit metadata such as: \n" +
                "- Album\n" +
                "- Artist\n" +
                "- Genre\n" +
                "- Cover Art\n" +
                "The tool has other utilities such as loudness normalization, folder organization, and many more.\n" +
                "----------\n" +
                "After entering a path use the -h command to display all available commands.\n\n");
    }

    // display command options and descriptions
    private static void displayHelp() {
        String format = "%-30s %s\n";

        System.out.printf(format, "-o [output path]: ", "Output modifier can be added to all commands to specify where to save output file(s) (see examples below)");
        System.out.printf(format, "", "\t\tWill be either folder or file path depending on the input path set");
        System.out.printf(format, "ab [text]: ", "Add to beginning command will add [text] to beginning");
        System.out.printf(format, "ae [text]: ", "Add to end command will add [text] to end");
        System.out.printf(format, "r [text]: ", "Remove command will remove [text] from files");
        System.out.printf(format, "Ar [artist text]: ", "Artist command will add [artist text] to artist metadata of files");
        System.out.printf(format, "A [album text]: ", "Album command will add [album text] to album metadata of files");
        System.out.printf(format, "G [genre text]: ", "Genre command will add [genre text] to genre metadata of files");
        System.out.printf(format, "Art [path to art]: ", "Art command will add art in [path to art] to art metadata of files");
        System.out.printf(format, "LN: ", "Loudness Normalize command will make loudness of files similar.");
        System.out.printf(format, "", "\t\tThis will allow user to listen to music without needing to change the volume. Will set loudness to -16 LU,");
        System.out.printf(format, "", "\t\ttrue peak set to -2 dBFS and loudness range set to match file's current range");
        System.out.printf(format, "LNN [LUFS value]: ", "Loudness Normalize command (same as -LN but with custom loudness) will take a value in LU and bring the loudness of files to that target.");
        System.out.printf(format, "", "\t\tThis will allow user to listen to music without needing to change the volume. Recommended LU values are -24 to -14 (numbers closer to 0 are louder).");
        System.out.printf(format, "", "\t\tTrue peak set to -2 dBFS and loudness range set to match file's current range");
        System.out.printf(format, "ffA: ", "Folders for album command will create folder for files based on their album then put the files in those folders");
        System.out.printf(format, "ffAr: ", "Folders for artist command will create folder for files based on their artist then put the files in those folders");
        System.out.printf(format, "DN: ", "Display by name command will display the metadata of the files organized by name");
        System.out.printf(format, "DAr: ", "Display by artist command will display the metadata of the files organized by artist name");
        System.out.printf(format, "DA: ", "Display by album command will display the metadata of the files organized by album");
        System.out.printf(format, "DG: ", "Display by genre command will display the metadata of the files organized by genre");
        System.out.printf(format, "DL: ", "Display by loudness command will display the metadata and loudness data of the files organized by loudest to quietest in LU");
        System.out.printf(format, "n [path to files]: ", "New command will select new set of files or file to target");
        System.out.printf(format, "h: ", "Help command will display all command options and give their descriptions");
        System.out.printf(format, "q: ", "Quit command will terminate program");
        System.out.println();

        System.out.println("Examples------------------------------------");
        System.out.println("Add command with and without output path modifier");
        System.out.println("ab new-");
        System.out.println("ab new- -o C:folder1/folder2/");
        System.out.println("original: file.mp3 >>> after command: new-file.mp3");
        System.out.println();

        System.out.println("Loudness Normalization command with and without output path modifier");
        System.out.println("LN");
        System.out.println("LN -o C:folder1/folder2/");
        System.out.println("Loudness Normalization command with custom loudness with and without output path modifier");
        System.out.println("LNN -23");
        System.out.println("LNN -23 -o C:folder1/folder2/out-file.mp3");
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

            if (path.equals("h") || path.equals("help")) {
                displayAbout();
            }
            return promptForPath();
        }
    }

    // prompt for command, returns [command, commandInput, outputPath] as array
    private static String[] promptForCommand(String currInputPath, AudioEditor audioEditor) {
        System.out.println("\nCurrent target path: " + currInputPath);
        System.out.print("\nEnter command: ");
        String command = input.nextLine();
        String commandInput = "";
        String outputPath = "";
        String[] fullCommand;

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

        UserFeedback.printIndent("command", "<" + command + ">");
        UserFeedback.printIndent("command input", "<" + commandInput + ">");
        UserFeedback.printIndent("out path", "<" + outputPath + ">");

        fullCommand = new String[]{command, commandInput, outputPath};

        // if an output path is specified that make sure it is logical and there are no name conflicts
        fullCommand = checkOutputValidity(fullCommand, currInputPath, audioEditor);

        return fullCommand;
    }

    // check for valid output path then check if there are file conflicts
    private static String[] checkOutputValidity(String[] command, String path, AudioEditor audioEditor) {
        // check for valid output path then check if there are file conflicts
        if (!command[2].equals("")) {
            // first check output
            boolean isValidOutput = FileHandler.isPathLogical(
                    FileHandler.isFolder(path),
                    FileHandler.isFolder(command[2])
            );
            if (!isValidOutput) {
                // prompt for command
                UserFeedback.print("re-enter command");
                command = promptForCommand(path, audioEditor);
            }
            // if output valid then check file conflicts
            else {
                // output is valid so update audio files to any changes made by user in between commands
                audioEditor.setFiles(audioEditor.getInputPath());

                // cffAr and cffA commands handle name conflicts internally
                if (!command[0].equals("ffAr") && !command[0].equals("ffA")){
                    // check if there are name conflicts
                    // (if conflicts exists then ask if overriding ok, if override not ok then skip file)
                    audioEditor.skipOverrideDenials(command[2]);
                }
            }
        }
        else {
            // since no output was specified input path will be used as output.
            // input has already been verified so assume safe.
            // update audio files to any changes made by user in between commands
            audioEditor.setFiles(audioEditor.getInputPath());
        }

        return command;
    }
}