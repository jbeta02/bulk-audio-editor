// Purpose: Main program class, prompt for and run commands

import java.io.File;
import java.util.Scanner;

public class Program {

    private static Scanner input = new Scanner(System.in);

    // main method, provide user interface and run commands
    public static void main(String[] args){
        MP3Editor mp3Editor;
        String[] command;
        String path;

        // intro to program
        System.out.println("Welcome to \"Bulk Audio Editor\"");
        System.out.println("-------------------------------");
        System.out.println("Enter path to a single Mp3 file or a folder holding MP3 files");
        System.out.println("(enter \"help\" for more information)");

        path = promptForPath();

        // create mp3Editor obj and inter path to mp3 files as constructor argument
        mp3Editor = new MP3Editor(path);

        command = promptForCommand(); // command = array [command, commandInput]

        while(!command[0].equals("-q") & !command[0].equals("q")){
            switch(command[0]){
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

                case "-h":
                    displayHelp();
                    break;
            }
            command = promptForCommand();
        }
    }

    // display a description of Bulk Audio Editor
    private static void displayAbout(){
        System.out.print("\n\"Bulk Audio Editor\" is a tool to help edit audio files in bulk. \n" +
                "A user can enter a path to a folder holding audio files such as mp3 then can run commands that can edit metadata such as: \n" +
                "- Album\n" +
                "- Artist\n" +
                "- Genre\n" +
                "- Cover Act\n" +
                "and more\n" +
                "After entering a path use the -h command to display all available commands.\n\n");
    }

    // display command options and descriptions
    private static void displayHelp(){
        String format = "%-30s %s\n";
        // file name, artist, album, genre
        String displayFormat = "%s %s %s %s";

        System.out.printf(format, "-ab [text]: ", "Add command will add [text] to beginning");
        System.out.printf(format, "-ae [text]: ", "Add command will add [text] to end");
        System.out.printf(format, "-r [text]: ", "Remove command will remove [text] from files");
        System.out.printf(format, "-Ar [artist text]: ", "Artist command will add [artist text] to artist metadata of files");
        System.out.printf(format, "-A [album text]: ", "Album command will add [album text] to album metadata of files");
        System.out.printf(format, "-G [genre text]: ", "Genre command will add [genre text] to genre metadata of files");
        System.out.printf(format, "-Art [path to art]: ", "Art command will add art in [path to art] to art metadata of files");
        //System.out.printf(displayFormat, "-D: ", "Display command will display the metadata of the files");
        System.out.printf(format, "-dB [dB level]: ", "Normalize command will take a float value in db to normalize files to");
        System.out.printf(format, "-h or help: ", "Help command will display all command options and give their descriptions");
        System.out.printf(format, "-n [path to files]: ", "New command will select new set of files or file to target");
        System.out.printf(format, "-q or q: ", "Quit command will terminate program");
    }

    private static String promptForPath(){
        String path;

        System.out.print("Enter path: ");

        path = input.nextLine();

        File file = new File(path);

        if (file.isDirectory() || file.isFile()){
            return path;
        }
        else {
            Log.error("Path not found. Please enter a valid path");

            if (path.equals("help")){
                displayAbout();
            }
            return promptForPath();
        }
    }

    // prompt for command, returns [command, commandInput] as array
    private static String[] promptForCommand(){
        System.out.print("\nEnter command: ");
        String command = input.nextLine();
        String commandInput = "";

        // check if command has an argument
        if(command.contains(" ")){
            // separate command and input
            commandInput = command.substring(command.indexOf(" ") + 1);
            command = command.substring(0, command.indexOf(" "));
        }
        // if not then it is a stand along command

        Log.print("command", "<" + command + ">");
        Log.print("command input", "<" + commandInput + ">");

        return new String[]{command, commandInput};
    }
}