import java.io.File;
import java.util.Scanner;

// Purpose: Main program class, prompt for and run commands
//TODO make an icon for exe file
public class Program {

    private static Scanner input = new Scanner(System.in);

    // main method, provide user interface and run commands
    public static void main(String[] args) {
        MP3Editor mp3Editor;
        String[] command;
        String path;

        // intro to program
        System.out.println("Welcome to \"Bulk Audio Editor\"     v1.0.0");
        System.out.println("-------------------------------");
        System.out.println("Enter path to a single Mp3 file or a folder holding MP3 files");
        System.out.println("(enter h for more information)");

        // prompt for valid path
        path = promptForPath();

        // create mp3Editor obj and inter path to mp3 files as constructor argument
        mp3Editor = new MP3Editor(path);

        // prompt for command
        command = promptForCommand(mp3Editor.getInputPath(), mp3Editor); // command = array [command, commandInput, output path]

        while(!command[0].equals("q")) {
            switch(command[0]) {
                case "ab":
                    mp3Editor.addToFileName(true, command[1], command[2]);
                    break;

                case "ae":
                    mp3Editor.addToFileName(false, command[1], command[2]);
                    break;

                case "r":
                    mp3Editor.removeFromFileName(command[1], command[2]);
                    break;

                case "Ar":
                    mp3Editor.modifyArtist(command[1], command[2]);
                    break;

                case "A":
                    mp3Editor.modifyAlbum(command[1], command[2]);
                    break;

                case "G":
                    mp3Editor.modifyGenre(command[1], command[2]);
                    break;

                case "Art":
                    mp3Editor.changeArt(command[1], command[2]);
                    break;

                // normalize using default value of -16 LU
                case "LN":
                    mp3Editor.normalizeFiles(command[2]);
                    break;

                // normalize with custom loudness
                case "LNN":
                    // make sure value is double
                    try {
                        Double.parseDouble(command[1]);
                    }
                    catch (Exception e) {
                        Log.errorE("Value entered is not a valid number. Try again", e);
                        command = promptForCommand(mp3Editor.getInputPath(), mp3Editor);
                    }
                    // enter custom loudness as double
                    double loudnessValue = Double.parseDouble(command[1]);
                    mp3Editor.normalizeFiles(loudnessValue, command[2]);
                    break;

                case "ffAr":
                    mp3Editor.createFoldersByArtist(command[2]);
                    break;

                case "ffA":
                    mp3Editor.createFoldersByAlbum(command[2]);
                    break;

                case "DN":
                    mp3Editor.displayDataByName();
                    break;

                case "DAr":
                    mp3Editor.displayDataByArtist();
                    break;

                case "DA":
                    mp3Editor.displayDataByAlbum();
                    break;

                case "DG":
                    mp3Editor.displayDataByGenre();
                    break;

                case "DL":
                    mp3Editor.displayDataByLoudness();
                    break;

                case "n":
                    mp3Editor.setInputPath(command[1]);
                    mp3Editor.setFiles(command[1]);
                    break;

                case "h":
                    displayHelp();
                    break;

                default:
                    System.out.println("\nPlease enter a valid command.");
                    System.out.println("(List of commands can be produced using \"h\" command)\n");
                    break;
            }
            command = promptForCommand(mp3Editor.getInputPath(), mp3Editor);
        }

        System.out.println("\nClosing Program...");
    }

    // display a description of Bulk Audio Editor
    private static void displayAbout() {
        System.out.print("\n\"Bulk Audio Editor\" is a tool to help edit and manage audio files in bulk. \n" +
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
    private static String[] promptForCommand(String currInputPath, MP3Editor mp3Editor) {
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
        fullCommand = checkOutputValidity(fullCommand, currInputPath, mp3Editor);

        return fullCommand;
    }

    // check for valid output path then check if there are file conflicts
    private static String[] checkOutputValidity(String[] command, String path, MP3Editor mp3Editor) {
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
                command = promptForCommand(path, mp3Editor);
            }
            // if output valid then check file conflicts
            else {

                // cffAr and cffA commands handle name conflicts internally
                if (!command[0].equals("ffAr") && !command[0].equals("ffA")){
                    // check if there are name conflicts
                    // (if conflicts exists then ask if overriding ok, if override not ok then skip file)
                    mp3Editor.skipOverrideDenials(command[2]);
                }
            }
        }

        return command;
    }
}