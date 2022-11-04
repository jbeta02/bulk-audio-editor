// Purpose: Provide interface for user to interact with program

import java.util.Scanner;

public class ProgramUI { //TODO consider transferring to Main

    private Scanner input = new Scanner(System.in);

    public void runCycle(){
        MP3Editor mp3Editor;
        String command;
        String commandInput = "";

        // intro to program
        System.out.println("Welcome to \"Bulk MP3 Editor\"");
        System.out.println("-------------------------------");

        System.out.print("Enter path to a MP3 folder: ");
        // create mp3Editor obj and inter path to mp3 files as constructor argument
        mp3Editor = new MP3Editor(input.nextLine());

        System.out.print("Enter command: ");
        command = input.nextLine();

        // check if command has an argument
        if(command.contains(" ")){
            // separate command and input
            commandInput = command.substring(command.indexOf(" ") - 1);
            command = command.substring(0, command.indexOf(" "));
        }
        // if not then it is a stand along command

        Log.print("command", "<" + command + ">");
        Log.print("command input", "<" + commandInput + ">");

        while(!command.equals("-q")){
            switch(command){
                case "-ab":
                    mp3Editor.addToFileName(true, commandInput);

                case "-ae":
                    mp3Editor.addToFileName(false, commandInput);
                    break;
            }
        }
    }

    private void displayHelp(){
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
        System.out.printf(displayFormat, "-D: ", "Display command will display the metadata of the files");
        System.out.printf(format, "-dB [dB level]: ", "Normalize command will take a float value in db to normalize files to");
        System.out.printf(format, "help or -h: ", "Help command will display all command options and give their descriptions");
        System.out.printf(format, "-n [path to files]: ", "New command select new set of files or files to target");
        System.out.printf(format, "-q: ", "Quit command will terminate program");
    }

}
