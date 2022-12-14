import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

// Purpose: Utility functions for actions relating to file interactions
public class FileHandler {

    // handle input/output paths ----------------------------------------------

    // check if path is valid compared to input
    // valid output type for given input type:
    // in: folder > out: folder  |  in: file > out: file, folder
    public static boolean isPathLogical(boolean inputIsFolder, boolean outputIsFolder) {
        boolean logical = false;
        // input is folder and output is file is not allowed, all other cases are fine
        if (inputIsFolder && !outputIsFolder){
            Log.error("Output path is invalid. Can not enter a file output path for a folder input.");
            logical = false;
        }
        else {
            logical = true;
        }
        return logical;
    }

    // copy file to another destination
    public static void copyFile(File inputFile, String outputPath) {
        // create Path obj of inputFile
        Path oldFilePath = Paths.get(inputFile.getPath());

        Path newFilePath;
        // get new path of file with outputPath
        if (isFolder(outputPath)) {
            newFilePath = Paths.get(outputPath, inputFile.getName());
        }
        // if out path is file then we don't need to add the file name to path again
        else {
            newFilePath = Paths.get(outputPath);
        }

        // carry out the copy process and override if needed
        try {
            Files.copy(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e) {
            Log.errorE("Failed to copy file " + inputFile.getName() + " to new destination " + outputPath, e);
        }
    }

    // create a path from multiple string parts
    public static String createPath(String first, String... more) {
        return Paths.get(first, more).toString();
    }

    // handle file overrides ----------------------------------------------

    // ask check if file already exist, if it does then ask user if override is ok
    public static boolean isOverrideAllowed(String outputFileName, String outputPath) {
        // check if output already exists
        // if yes, prompt if override desired > if yes then finish, if no then stop
        boolean isAllowed = true;
        if (nameAlreadyExists(outputFileName, outputPath)) {

            Scanner input = new Scanner(System.in);

            System.out.println("file " + outputFileName + " already exists in " + outputPath + ". Do you want to override existing file?");
            System.out.print("Enter y or n: ");

            switch (input.nextLine()){
                case "y":
                    isAllowed = true;
                    break;
                case "n":
                    isAllowed = false;
                    break;
                default:
                    System.out.println("Enter y or n. Will prompt again.");
                    isAllowed = isOverrideAllowed(outputFileName, outputPath);
                    break;
            }
        }
        return isAllowed;
    }

    // check if file with x name already exists in given path
    // (shorthand way of checking if 2 files are equal without looking at their contents)
    public static boolean nameAlreadyExists(String outputFileName, String outputPath) {
        boolean alreadyExists = false;
        File outPathAsFile = new File(outputPath);

        if (outPathAsFile.isDirectory()){
            for (File file : outPathAsFile.listFiles()) {
                if (file.isFile()) {
                    Log.print("file name", file.getName());
                    if (outputFileName.equals(file.getName())) {
                        alreadyExists = true;
                        break;
                    }
                }
            }
        }
        else {
            if (outputFileName.equals(outPathAsFile.getName())) {
                alreadyExists = true;
            }
        }
        return alreadyExists;
    }

    // checks ----------------------------------------------

    // check if file
    public static boolean isFile(String path){
        // create path obj so the path can be validated
        File file = new File(path);

        // check if file
        return file.isFile();
    }

    // check if folder
    public static boolean isFolder(String path){
        // create path obj so the path can be validated
        File file = new File(path);

        // check if folder
        return file.isDirectory();
    }

    // getters ----------------------------------------------

    // return file's path but exclude the file from the path string
    public static String getPathNoName(File file) {
        // get current file path
        String currentPath = file.getPath();

        // remove current file name from path
        // (will remove file name and extension)
        // create new string starting from beginning of path and ending at \ (inclusive)
        String newPath = currentPath.substring(0, currentPath.lastIndexOf("\\") + 1);

        return newPath;
    }

    // return file's path but exclude the file from the path string
    public static String getPathNoName(String path) {
        // get current file path
        String currPath = getFile(path).getPath();

        // remove current file name from path
        // (will remove file name and extension)
        // create new string starting from beginning of path and ending at \ (inclusive)
        String newPath = currPath.substring(0, currPath.lastIndexOf("\\") + 1);

        return newPath;
    }

    // convert path to File obj
    public static File getFile(String path) {
        return new File(path);
    }

    // get all folders from a given directory path
    public static ArrayList<File> getFolders(String path) {
        ArrayList<File> folders = new ArrayList<>();
        File pathAsFile = getFile(path);


        for (File file : pathAsFile.listFiles()) {
            if (file.isDirectory()) {
                folders.add(file);
            }
        }

        return folders;
    }
}
