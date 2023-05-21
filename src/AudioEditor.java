import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;

// Purpose: Provide means to modify an audio file. Modifications include name, metadata, and loudness.
// Currently, supports mp3 and flac
public class AudioEditor {

    private ArrayList<AudioFile> audioFiles = new ArrayList<>();
    private String inputPath = "";

    private ArrayList<AudioFile> audioFilesNoOverride = new ArrayList<>();

    private final String TEMP_FOLDER = "temp\\";

    private final String[] SUPPORTED_CONTAINERS = {
        "mp3",
        "flac",
    };


    // Constructor used to populate audioFiles ArrayList so that it may be used in other methods after instantiation
    public AudioEditor(String path) {
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
        for (AudioFile file : audioFiles) {
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
                Log.errorE("Failed to save file with new name" + " for file \"" + file.getFile().getName() + "\"", e);
            }

            // delete old file
            try{
                Files.delete(oldFilePath);
            }
            catch (Exception e) {
                Log.errorE("Failed to save file with new name" + " for file \"" + file.getFile().getName() + "\"", e);
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
        // will hold newly created files to set as new target audioFiles
        String newFilesPath = "";

        // if an output path was set then copy files over
        copyFilesToOutput(outputPath);

        // get names
        for (AudioFile file : audioFiles) {
            // save the current name
            currentName = file.getFile().getName();

            // remove target text from file
            newName = currentName.replace(textToRemove, "");

            // get file properties from AudioFile obj then get path
            Path oldFilePath = Paths.get(file.getFile().getPath());
            // get file properties from AudioFile obj then get path without a file name, add newName as file
            Path newFilePath = Paths.get(FileHandler.getPathNoName(file.getFile()), newName);

            // only make change if needed
            if (!oldFilePath.equals(newFilePath)) {

                try{
                    Files.copy(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                }
                catch (Exception e) {
                    Log.errorE("Failed to save file with new name" + " for file \"" + file.getFile().getName() + "\"", e);
                }

                // delete old file
                try{
                    Files.delete(oldFilePath);
                }
                catch (Exception e) {
                    Log.errorE("Failed to save file with new name" + " for file \"" + file.getFile().getName() + "\"", e);
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
    public void modifyMetadata(FieldKey fieldKey, String text, String outputPath) {

        // if an output path was set then copy files over
        copyFilesToOutput(outputPath);

        for (AudioFile file : audioFiles) {
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
                Log.errorE("Unable to set new metadata text \"" + text + "\" for file \"" + file.getFile().getName() + "\"", e);
            }
        }
        // set output as target files
        setFiles(outputPath);
    }


    // change album art
    public void changeArt(String pathToArt, String outputPath) { //TODO need to test on flac
        // if an output path was set then copy files over
        copyFilesToOutput(outputPath);

        for (AudioFile file : audioFiles) {
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
                Log.errorE("Unable to set art cover using path: " + pathToArt + " for file \"" + file.getFile().getName() + "\"", e);
            }
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

        // tell user that work is being done (add display before progress bar)
        UserFeedback.print("processing...");

        // normalize loudness of all target files
        for (int i = 0; i < audioFiles.size(); i++) {
            // if outputPath is a folder then will need to change to file specific path for ffmpeg
            if (isOutputFolder) {
                // add name of curr file to path
                outputPath = FileHandler.createPath(outputPath, audioFiles.get(i).getFile().getName());
            }

            // run ffmpeg normalization command
            fFmpegWrapper.normalizeLoudness(audioFiles.get(i).getFile().getPath(), integratedLoudness, truePeak, outputPath);

            // create progress bar
            UserFeedback.progressBar("Progress Normalizing Loudness", i + 1, audioFiles.size());

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
        for (int i = 0; i < audioFiles.size(); i++){
            // if conflict found and override not allowed then remove file from list
            if (!FileHandler.isOverrideAllowed(audioFiles.get(i).getFile().getName(), outputPath)) {
                audioFilesNoOverride.add(audioFiles.get(i));
                audioFiles.remove(i);
                i = i - 1;
            }
        }
    }


    // create folders based on X then put the target files in the corresponding folders where x is
    // metadata such as Album, Artist, Genre
    public void createFoldersFor(FieldKey fieldKey, String outputPath) {
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
            for (AudioFile file : audioFiles) {
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


    // display methods ---------------------------------------------------------------

    // display data order by given criteria
    public void displayDataBy(Comparator<AudioFile> sortByCriteria) {
        ArrayList<AudioFile> audioFilesSorted = new ArrayList<>();

        // copy contents of mp3Files to mp3FilesSorted
        for (AudioFile file : audioFiles) {
            audioFilesSorted.add(file);
        }
        // sort list
        audioFilesSorted.sort(sortByCriteria);

        // display sorted data
        displayData(audioFilesSorted);
    }


    // display data order by given criteria using loudnessFile
    private void displayDataByLoudness(Comparator<LoudnessFile> sortByCriteria) {
        ArrayList<LoudnessFile> audioFilesSorted = new ArrayList<>();

        // tell user that work is being done (add display before progress bar)
        UserFeedback.print("processing...");

        // convert files in audioFiles to LoudnessFile then copy list of files to audioFilesSorted
        for (int i = 0; i < audioFiles.size(); i++) {
            LoudnessFile loudnessFile = new LoudnessFile(audioFiles.get(i));
            audioFilesSorted.add(loudnessFile);

            // create progress bar
            UserFeedback.progressBar("Progress Getting Loudness Data", i + 1, audioFiles.size());
        }

        // sort audioFilesSorted
        audioFilesSorted.sort(sortByCriteria);

        // display sorted data
        displayDataWithLoudness(audioFilesSorted);
    }


    // display data of all audio files in folder
    private void displayData(ArrayList<AudioFile> files) {
        // print top data
        // folder name      total files
        if (files.size() > 0) {
            System.out.printf("%s %-50s %s %s\n\n", "Current target Path:", FileHandler.getPathNoName(files.get(0).getFile()), "Total File Count:" , files.size());
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
        for (AudioFile file : audioFiles) {
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
            System.out.printf("%s %-50s %s %s\n\n", "Path:", FileHandler.getPathNoName(loudnessFilesFiles.get(0).getAudioFile().getFile()), "Total File Count:" , loudnessFilesFiles.size());
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
            Tag tag = file.getAudioFile().getTag();

            System.out.printf(format,
                    file.getAudioFile().getFile().getName(),
                    tag.getFirst(FieldKey.ALBUM),
                    tag.getFirst(FieldKey.ARTIST),
                    tag.getFirst(FieldKey.GENRE),
                    convertToMinSec(file.getAudioFile().getAudioHeader().getTrackLength()),
                    file.getMeasuredI(), // integrated loudness
                    file.getMeasuredTp(), // true peak
                    file.getMeasuredLRA() // loudness range
            );
        }
    }


    // private utility methods for class ----------------------------------------------

    // copy mp3 files to a set output path
    private void copyFilesToOutput(String outputPath) {
        if (!outputPath.equals("")) {
            // loop through files and move to new location
            for (AudioFile file : audioFiles) {
                FileHandler.copyFile(file.getFile(), outputPath);
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
            audioFiles = transferInputFileToOutput(outputPath);

            // from new audio files remove override not allowed files by checking if names correspond
            if (audioFilesNoOverride.size() > 0) {
                for (int i = 0; i < audioFiles.size(); i++) {

                    for (int j = 0; j < audioFilesNoOverride.size(); j++) {
                        if (FileHandler.nameAlreadyExists(audioFiles.get(i).getFile().getName(), audioFilesNoOverride.get(j).getFile().getPath())) {
                            audioFiles.remove(i);
                        }
                    }
                }
            }
            UserFeedback.printIndent("Target path CHANGED to", inputPath);
        }
    }


    // transfer input files to output
    private ArrayList<AudioFile> transferInputFileToOutput(String outputPath) {
        ArrayList<AudioFile> targetFiles = new ArrayList<>();

        if (FileHandler.isFile(outputPath)) {
            outputPath = FileHandler.getPathNoName(outputPath);
        }

        // convert input files to output
        for (AudioFile file : audioFiles) {
            try {
                AudioFile newTargetFile = AudioFileIO.read(FileHandler.getFile(FileHandler.createPath(outputPath, file.getFile().getName())));

                targetFiles.add(newTargetFile);
            }
            catch (Exception e) {
                Log.errorE("Unable to transfer file to output" + " for file \"" + file.getFile().getName() + "\"", e);
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

    private boolean fileSupported(File file) {
        String fileExtension = FileHandler.getFileExtension(file.getName());
        boolean supported = false;

        for (String container : SUPPORTED_CONTAINERS) {
            if (fileExtension.contains(container)) {
                supported = true;
                break;
            }
        }
        return supported;
    }


// accessors and mutators ----------------------------------------------

    // get file ArrayList
    public ArrayList<AudioFile> getFiles() {
        return audioFiles;
    }


    // populate audioFiles ArrayList
    public void setFiles(String path) {
        File file = new File(path);

        // reset previous list so new files are not added to an already existing list of files
        audioFiles.clear();

        // check if path leads to folder
        if (file.isDirectory() & !file.isFile()) {
            UserFeedback.print("Input path leads a to folder"); //TODO tell user what files types were found in folder

            // loop through files, convert each to AudioFile obj and add to arraylist
            for (File fileInLoop : file.listFiles()) {

                // check if file is a supported file format
                if (FileHandler.isFile(fileInLoop.getPath()) && fileSupported(fileInLoop)){
                    // might not be able to read file
                    try {
                        AudioFile convertedFile = AudioFileIO.read(fileInLoop);

                        audioFiles.add(convertedFile);
                    }
                    catch (Exception e) {
                        Log.errorE("Unable to internally classify file with name: " + fileInLoop.getName(), e);
                    }
                }
            }

            // verify number of files found
            UserFeedback.printIndent("Number of files found", audioFiles.size());
        }

        // check if path leads to file
        else if (!file.isDirectory() & file.isFile()) {
            UserFeedback.print("Input path leads to a file"); //TODO tell user what type of file it is

            // check if file is a supported file format
            if (FileHandler.isFile(file.getPath()) && fileSupported(file)) {
                // might not be able to read file
                try {
                    AudioFile convertedFile = AudioFileIO.read(file);

                    audioFiles.add(convertedFile);
                }
                catch (Exception e) {
                    Log.error("Unable to internally classify file with name" + file.getName(), e);
                }
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
        } else {
            Log.error("Input path " + path + " is invalid. Program will default to empty path.");
        }
    }
}
