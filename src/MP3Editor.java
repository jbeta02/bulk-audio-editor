import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;

// jaudiotagger is an external library for manipulating mp3 files
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

// Purpose: Provide means to modify an MP3 file. Modifications include name, metadata, and volume.

//TODO improve error for input related and unrelated errors handling and reporting

public class MP3Editor {

    private ArrayList<MP3File> mp3Files = new ArrayList<>();
    private String inputPath = "";

    private ArrayList<MP3File> mp3FilesNoOverride = new ArrayList<>();

    private final String TEMP_FOLDER = "temp\\";

    // Constructor used to populate mp3Files ArrayList so that it may be used in other methods after instantiation
    public MP3Editor(String path) {
        // set instance variables
        setInputPath(path);
        setFiles(path);
    }

    // add text to either beginning (true) or end (false) of file
    public void addToFileName(boolean addToStart, String textToAdd, String outputPath) {
        String currentName;
        String newName;
        // will hold newly created files to set as new target mp3Files
        String newFilesPath = "";

        // if an output path was set then copy files over
        copyFilesToOutput(outputPath);

        // get names
        for (MP3File file : mp3Files) {
            // save the current name
            currentName = file.getFile().getName();

            // add text to start of file
            if (addToStart) {
                newName = textToAdd + currentName;
            }
            else {
                // get extension to add to end of new name
                String extension = currentName.substring(currentName.lastIndexOf("."));
                // remove extension from current name new text can be added to end of name
                currentName = currentName.replace(extension, "");

                // create new name with text added to end
                newName = currentName + textToAdd + extension;
            }

            // get file properties from mp3 obj then get path
            Path oldFilePath = Paths.get(file.getFile().getPath());
            // get file properties from mp3 obj then get path without a file name, add newName as file
            Path newFilePath = Paths.get(FileHandler.getPathNoName(file.getFile()), newName);

            try{
                Files.copy(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (Exception e) {
                Log.errorE("Failed to save file with new name", e);
            }

            // delete old file
            try{
                Files.delete(oldFilePath);
            }
            catch (Exception e) {
                Log.errorE("Failed to save file with new name", e);
            }

            // track newly produced file path
            newFilesPath = FileHandler.getPathNoName(file.getFile());
        }

        // will allow commands to be ran on new files
        if (!newFilesPath.equals("")){
            setFiles(newFilesPath);
        }

    }

    // remove text pattern from file name
    public void removeFromFileName(String textToRemove, String outputPath) {
        String currentName;
        String newName;
        // will hold newly created files to set as new target mp3Files
        String newFilesPath = "";

        // if an output path was set then copy files over
        copyFilesToOutput(outputPath);

        // get names
        for (MP3File file : mp3Files) {
            // save the current name
            currentName = file.getFile().getName();

            // remove target text from file
            newName = currentName.replace(textToRemove, "");

            // get file properties from mp3 obj then get path
            Path oldFilePath = Paths.get(file.getFile().getPath());
            // get file properties from mp3 obj then get path without a file name, add newName as file
            Path newFilePath = Paths.get(FileHandler.getPathNoName(file.getFile()), newName);

            // only make change if needed
            if (!oldFilePath.equals(newFilePath)) {

                try{
                    Files.copy(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                }
                catch (Exception e) {
                    Log.errorE("Failed to save file with new name", e);
                }

                // delete old file
                try{
                    Files.delete(oldFilePath);
                }
                catch (Exception e) {
                    Log.errorE("Failed to save file with new name", e);
                }
            }

            // track newly produced file path
            newFilesPath = FileHandler.getPathNoName(file.getFile());
        }

        // will allow commands to be run on new files
        if (!newFilesPath.equals("")){
            setFiles(newFilesPath);
        }

    }

    // general algorithm for modifying metadata
    private void modifyMetadata(FieldKey fieldKey, String text, String outputPath) {

        // if an output path was set then copy files over
        copyFilesToOutput(outputPath);

        for (MP3File file : mp3Files) {
            // get tag which contains metadata
            Tag tag = file.getTag();

            try {
                // set new metadata value
                tag.setField(fieldKey, text);

                // save new value
                file.commit();
            }
            // might not be able to write data
            catch (Exception e) {
                Log.errorE("Unable to set new album text", e);
            }
        }
        // set output as target files
        setFiles(outputPath);
    }

    // change artist text
    public void modifyArtist(String artistText, String outputPath) {
        modifyMetadata(FieldKey.ARTIST, artistText, outputPath);
    }

    // change album text
    public void modifyAlbum(String albumText, String outputPath) {
        modifyMetadata(FieldKey.ALBUM, albumText, outputPath);
    }

    // change genre text
    public void modifyGenre(String genreText, String outputPath) {
        modifyMetadata(FieldKey.GENRE, genreText, outputPath);
    }

    // change album art
    public void changeArt(String pathToArt, String outputPath) {
        // if an output path was set then copy files over
        copyFilesToOutput(outputPath);

        for (MP3File file : mp3Files) {
            // get tag which contains metadata
            Tag tag = file.getTag();

            try {
                // create Artwork obj using ArtworkFactory with pathToArt as input
                Artwork artwork = ArtworkFactory.createArtworkFromFile(new File(pathToArt));

                // set new cover art
                tag.setField(artwork);

                // save new cover art
                file.commit();

            } catch (Exception e) {
                Log.errorE("Unable to set art cover using path: " + pathToArt, e);
            }
        }
    }

    // display files sorted by name
    public void displayDataByName() {
        // create sorting criteria using Comparator obj
        // use lambda to compare mp3 files in the list using the file name
        Comparator<MP3File> sortByCriteria = Comparator.comparing(MP3File -> MP3File.getFile().getName());

        // sort and display
        displayDataBy(sortByCriteria);
    }

    // display files sorted by album name
    public void displayDataByAlbum() {
        // create sorting criteria using Comparator obj
        // use lambda to compare mp3 files in the list using album name
        Comparator<MP3File> sortByCriteria = Comparator.comparing(MP3File -> MP3File.getTag().getFirst(FieldKey.ALBUM));

        // sort and display
        displayDataBy(sortByCriteria);
    }

    // display files sorted by artist name
    public void displayDataByArtist() {
        // create sorting criteria using Comparator obj
        // use lambda to compare mp3 files in the list using given artist name
        Comparator<MP3File> sortByCriteria = Comparator.comparing(MP3File -> MP3File.getTag().getFirst(FieldKey.ARTIST));

        // sort and display
        displayDataBy(sortByCriteria);
    }

    // display files sorted by genre
    public void displayDataByGenre() {
        // create sorting criteria using Comparator obj
        // use lambda to compare mp3 files in the list using the genre
        Comparator<MP3File> sortByCriteria = Comparator.comparing(MP3File -> MP3File.getTag().getFirst(FieldKey.GENRE));

        // sort and display
        displayDataBy(sortByCriteria);
    }

    // display files sorted by genre
    public void displayDataByLoudness() {
        // create sorting criteria using Comparator obj
        // use lambda to compare mp3 files in the list using loudness
        Comparator<LoudnessFile> sortByCriteria = Comparator.comparing(LoudnessFile -> LoudnessFile.getMeasuredI());

        // sort and display
        displayDataByLoudness(sortByCriteria.reversed());
    }

    // display data order by given criteria
    private void displayDataBy(Comparator<MP3File> sortByCriteria) {
        ArrayList<MP3File> mp3FilesSorted = new ArrayList<>();

        // copy contents of mp3Files to mp3FilesSorted
        for (MP3File file : mp3Files) {
            mp3FilesSorted.add(file);
        }
        // sort list
        mp3FilesSorted.sort(sortByCriteria);

        // display sorted data
        displayData(mp3FilesSorted);
    }

    // display data order by given criteria using loudnessFile
    private void displayDataByLoudness(Comparator<LoudnessFile> sortByCriteria) {
        ArrayList<LoudnessFile> mp3FilesSorted = new ArrayList<>();

        // convert files in mp3Files to LoudnessFile then copy list of mp3Files to mp3FilesSorted
        for (int i = 0; i < mp3Files.size(); i++) {
            LoudnessFile loudnessFile = new LoudnessFile(mp3Files.get(i));
            mp3FilesSorted.add(loudnessFile);

            // create progress bar
            UserFeedback.progressBar("Progress Getting Loudness Data", i + 1, mp3Files.size());
        }

        // sort list
        mp3FilesSorted.sort(sortByCriteria);

        // display sorted data
        displayDataWithLoudness(mp3FilesSorted);
    }

    // display data of all audio files in folder
    private void displayData(ArrayList<MP3File> mp3Files) {
        // print top data
        // folder name      total files
        if (mp3Files.size() > 0) {
            System.out.printf("%s %-50s %s %s\n\n", "Current target Path:", FileHandler.getPathNoName(mp3Files.get(0).getFile()), "Total File Count:" , mp3Files.size());
        }
        else {
            System.out.println("No files in folder");
        }

        // format for file data
        String format = "%-50s %-30s %-30s %-20s %-15s\n";

        // print header
        System.out.printf(format,
                "Name",
                "Album",
                "Artist",
                "Genre",
                "Length"
        );
        // print bottom of each header piece
        System.out.printf(format,
                "--------",
                "--------",
                "--------",
                "--------",
                "------"
        );

        // print file data
        // name     artist      album   genre       len
        for (MP3File file : mp3Files) {
            Tag tag = file.getTag();

            System.out.printf(format,
                    file.getFile().getName(),
                    tag.getFirst(FieldKey.ALBUM),
                    tag.getFirst(FieldKey.ARTIST),
                    tag.getFirst(FieldKey.GENRE),
                    convertToMinSec(file.getAudioHeader().getTrackLength())
            );
        }
    }

    // display data of all audio files in folder including loudness stats
    public void displayDataWithLoudness(ArrayList<LoudnessFile> loudnessFilesFiles) {
        // print top data
        // folder name      total files
        if (loudnessFilesFiles.size() > 0) {
            System.out.printf("%s %-50s %s %s\n\n", "Path:", FileHandler.getPathNoName(loudnessFilesFiles.get(0).getMp3File().getFile()), "Total File Count:" , loudnessFilesFiles.size());
        }
        else {
            System.out.println("No files in folder");
        }

        // format for file data
        String format = "%-50s %-30s %-30s %-20s %-15s %-30s %-20s %-20s\n";

        // print header
        System.out.printf(format,
                "Name",
                "Album",
                "Artist",
                "Genre",
                "Length",
                "Integrated Loudness [LU]",
                "True Peak [dBFS]",
                "Loudness Range (LUFS)"
        );
        // print bottom of each header piece
        System.out.printf(format,
                "--------",
                "--------",
                "--------",
                "--------",
                "------",
                "--------",
                "-----",
                "------"
        );

        // print file data
        // name     artist      album   genre       len
        for (LoudnessFile file : loudnessFilesFiles) {
            Tag tag = file.getMp3File().getTag();

            System.out.printf(format,
                    file.getMp3File().getFile().getName(),
                    tag.getFirst(FieldKey.ALBUM),
                    tag.getFirst(FieldKey.ARTIST),
                    tag.getFirst(FieldKey.GENRE),
                    convertToMinSec(file.getMp3File().getAudioHeader().getTrackLength()),
                    file.getMeasuredI(), // integrated loudness
                    file.getMeasuredTp(), // true peak
                    file.getMeasuredLRA() // loudness range
            );
        }
    }

    // create folder based on files artist
    public void createFoldersByArtist(String outputPath) {
        createFoldersFor(FieldKey.ARTIST, outputPath);
    }

    // create folder based on files album
    public void createFoldersByAlbum(String outputPath) {
        createFoldersFor(FieldKey.ALBUM, outputPath);
    }

    // create folders based on X then put the target files in the corresponding folders where x is
    // metadata such as Album, Artist, Genre
    private void createFoldersFor(FieldKey fieldKey, String outputPath) {
        ArrayList<File> folders;
        String originalOutputPath = outputPath;

        // use output as input if user wants to keep work input folder
        if (outputPath.equals("")) {
            outputPath = inputPath;
        }

        // check if output is folder
        if (FileHandler.isFolder(outputPath)) {
            // save folders already present
            folders = FileHandler.getFolders(outputPath);

            // place files in appropriate folders
            for (MP3File file : mp3Files) {
                boolean nameConflict = false;
                Tag tag = file.getTag();

                if (!tag.getFirst(fieldKey).equals("")) {

                    // check if folder name conflicts exist, if so then check if folder contains file name conflicts,
                    //      if so ask then ask if override ok
                    for (File folder : folders) {
                        // check if folder name conflicts exist
                        if (tag.getFirst(fieldKey).contains(folder.getName())) {

                            // check if name conflicts
                            if (FileHandler.isOverrideAllowed(file.getFile().getName(), folder.toString())) {
                                // add files to new folder
                                FileHandler.copyFile(file.getFile(), (folder.toString()));
                            }
                            nameConflict = true;
                        }
                    }

                    // if no other folders with possible name conflicts exist then new create folder and place files inside
                    if (!nameConflict) {
                        try {
                            // create folder and add to list of folders
                            Path createdFolder = Paths.get(outputPath, tag.getFirst(fieldKey));
                            Files.createDirectory(createdFolder);
                            folders.add(createdFolder.toFile());

                            // check if name conflicts
                            if (FileHandler.isOverrideAllowed(file.getFile().getName(), createdFolder.toString())) {
                                // add files to new folder
                                FileHandler.copyFile(file.getFile(), (createdFolder.toString()));
                            }
                        } catch (Exception e) {
                            Log.errorE("Unable to create new folder", e);
                        }
                    }

                    // delete old file (not inside folder) if work being done inside input folder
                    if (originalOutputPath.equals("")) {
                        try {
                            Files.delete(file.getFile().toPath());
                        }
                        catch (IOException e) {
                            Log.errorE("Unable to remove copy of " + file.getFile().getName() + ",so not in folder", e);
                        }
                    }
                }
            }
            setInputPath(outputPath);
            setFiles(outputPath);
        }
        else {
            UserFeedback.print("To use this command output path must be a folder.");
        }
    }

    // normalize mp3 files, ask for integratedLoudness and truePeak
    public void normalizeFiles(double integratedLoudness, double truePeak, String outputPath) {
        // create ffmpegWrapper obj to run loudness normalization command
        FFmpegWrapper fFmpegWrapper = new FFmpegWrapper();
        boolean usingTemp = false;
        boolean isOutputFolder;
        String originalOutPath;

        // used temp as output path if no output path is set by user
        if (outputPath.equals("")) {
            outputPath = TEMP_FOLDER;
            usingTemp = true;
        }

        // save original path since it will be overridden later
        originalOutPath = outputPath;

        // check if folder so we can build a path accordingly
        isOutputFolder = FileHandler.isFolder(outputPath);

        // normalize loudness of all target files
        for (int i = 0; i < mp3Files.size(); i++) {
            // if outputPath is a folder then will need to change to file specific path for ffmpeg
            if (isOutputFolder) {
                // add name of curr file to path
                outputPath = FileHandler.createPath(outputPath, mp3Files.get(i).getFile().getName());
            }

            // run ffmpeg normalization command
            fFmpegWrapper.normalizeLoudness(mp3Files.get(i).getFile().getPath(), integratedLoudness, truePeak, outputPath);

            // create progress bar
            UserFeedback.progressBar("Progress Normalizing Loudness", i + 1, mp3Files.size());

            // reset output path
            outputPath = originalOutPath;
        }

        // (not needed if an output path is specified)
        // copied normalized files from temp to input then delete files in temp
        if (usingTemp) {
            setFiles(TEMP_FOLDER);
            copyFilesToOutput(inputPath);
            for (File file : FileHandler.getFile(TEMP_FOLDER).listFiles()) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
        else {
            setInputPath(originalOutPath);
            setFiles(outputPath);
        }
    }

    // overload of normalizeFiles, ask for integratedLoudness
    public void normalizeFiles(double integratedLoudness, String output) {
        normalizeFiles(integratedLoudness, FFmpegWrapper.TRUE_PEAK, output);
    }

    // overload of normalizeFiles, use default values
    public void normalizeFiles(String output) {
        normalizeFiles(FFmpegWrapper.INTEGRATED_LOUDNESS, FFmpegWrapper.TRUE_PEAK, output);
    }

    // go through files and look for conflicts
    // if conflicts exists ask user if override is allowed
    // take files out of list if file isn't allowed to be overwritten
    public void skipOverrideDenials(String outputPath) {
        // loop through list of files and look for conflicts
        for (int i = 0; i < mp3Files.size(); i++){
            // if conflict found and override not allowed then remove file from list
            if (!FileHandler.isOverrideAllowed(mp3Files.get(i).getFile().getName(), outputPath)) {
                mp3FilesNoOverride.add(mp3Files.get(i));
                mp3Files.remove(i);
                i = i - 1;
            }
        }
    }

    // private utility methods for class ----------------------------------------------

    // copy mp3 files to a set output path
    private void copyFilesToOutput(String outputPath) {
        if (!outputPath.equals("")) {
            // loop through files and move to new location
            for (MP3File mp3File : mp3Files) {
                FileHandler.copyFile(mp3File.getFile(), outputPath);
            }
            // set new target path since work will likely be done to new files instead of old ones
            setOutputToInput(outputPath);
        }
    }

    // set output to input and ignore no override files
    private void setOutputToInput(String outputPath) {
        if (!outputPath.equals("")) {
            // set new target path since work will likely be done to new files instead of old ones
            setInputPath(outputPath);
            mp3Files = transferInputMp3ToOutput(outputPath);

            // from new mp3 files remove override not allowed files by checking if names correspond
            if (mp3FilesNoOverride.size() > 0) {
                for (int i = 0; i < mp3Files.size(); i++) {

                    for (int j = 0; j < mp3FilesNoOverride.size(); j++) {
                        if (FileHandler.nameAlreadyExists(mp3Files.get(i).getFile().getName(), mp3FilesNoOverride.get(j).getFile().getPath())) {
                            mp3Files.remove(i);
                        }
                    }
                }
            }
            UserFeedback.printIndent("Target path CHANGED to", inputPath);
        }
    }

    // transfer input files to output
    private ArrayList<MP3File> transferInputMp3ToOutput(String outputPath) {
        ArrayList<MP3File> targetFiles = new ArrayList<>();

        if (FileHandler.isFile(outputPath)) {
            outputPath = FileHandler.getPathNoName(outputPath);
        }

        // convert input files to output
        for (MP3File file : mp3Files) {
            try {
                MP3File newTargetFile = new MP3File(FileHandler.getFile(FileHandler.createPath(outputPath, file.getFile().getName())));

                targetFiles.add(newTargetFile);
            }
            catch (Exception e) {
                Log.errorE("Unable to transfer file to output", e);
            }
        }

        return targetFiles;
    }

    // convert seconds to min:sec format
    private String convertToMinSec(int seconds) {
        int min = seconds / 60;

        int sec = seconds % 60;

        return String.format("%d:%02d", min, sec);
    }

    // accessors and mutators ----------------------------------------------

    // get file ArrayList
    public ArrayList<MP3File> getFiles() {
        return mp3Files;
    }

    // populate mp3Files ArrayList
    public void setFiles(String path) {
        File file = new File(path);

        // reset previous list so new files are not added to an already existing list of files
        mp3Files.clear();

        // check if path leads to folder
        if (file.isDirectory() & !file.isFile()) {
            UserFeedback.print("Input path leads to folder");

            // loop through files, convert each to MP3 obj and add to arraylist
            for (File fileInLoop : file.listFiles()) {

                // check if file is mp3
                if (FileHandler.isFile(fileInLoop.getPath()) && FileHandler.getFileExtension(fileInLoop.getName()).contains("mp3")){
                    // might not be able to read file
                    try {
                        MP3File convertedFile = (MP3File) AudioFileIO.read(fileInLoop);

                        mp3Files.add(convertedFile);
                    }
                    catch (Exception e) {
                        Log.errorE("Unable to internally classify file with name: " + fileInLoop.getName(), e);
                    }
                }
            }

            // verify number of files found
            UserFeedback.printIndent("Number of files found", mp3Files.size());
        }

        // check if path leads to file
        else if (!file.isDirectory() & file.isFile()) {
            UserFeedback.print("Input path leads to file");

            // might not be able to read file
            try {
                MP3File convertedFile = (MP3File) AudioFileIO.read(file);

                mp3Files.add(convertedFile);
            }
            catch (Exception e) {
                Log.error("Unable to internally classify file with name" + file.getName(), e);
            }
        }
    }

    //---------------

    // get path
    public String getInputPath() {
        return inputPath;
    }

    // set a valid inputPath
    public void setInputPath(String path) {
        // check if path is valid

        // create path obj so the path can be validated
        File file = new File(path);

        // set path if valid
        if (file.isDirectory() || file.isFile()) {
            this.inputPath = path;
        }
        else {
            Log.error("Input path " + path + " is invalid. Program will default to empty path.");
        }
    }
}
