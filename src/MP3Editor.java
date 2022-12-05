import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

// jaudiotagger is an external library for manipulating mp3 files
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

// Purpose: Provide means to modify an MP3 file. Modifications include name, metadata, and volume.

//TODO improve error for input related and unrelated errors handling and reporting

public class MP3Editor {

    private ArrayList<MP3File> mp3Files = new ArrayList<>();
    private String path = "";

    // Constructor used to populate mp3Files ArrayList so that it may be used in other methods after instantiation
    public MP3Editor(String path) {
        // set instance variables
        setPath(path);
        setFiles(path);
    }

    // add text to either beginning (true) or end (false) of file
    public void addToFileName(boolean addToStart, String textToAdd) {
        String currentName;
        String newName;

        // get names
        for (MP3File file : mp3Files) {
            // save the current name
            currentName = file.getFile().getName();

            // add text to start of file
            if (addToStart) {
                newName = textToAdd + currentName;
            }
            // TODO add to end of file name doesn't work, it adds text after the extension not end of name
            // add text to end of file
            else {
                newName = currentName + textToAdd;
            }

            // get file properties from mp3 obj then get path
            Path oldFilePath = Paths.get(file.getFile().getPath());
            // get file properties from mp3 obj then get path without a file name, add newName as file
            Path newFilePath = Paths.get(getPathNoName(file.getFile()) + newName);

            Log.print("old name", currentName);
            Log.print("new name", newName);

            try{
                Files.copy(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (Exception e) {
                Log.errorE("Failed to save file with new name", e);
            }
        }
    }

    // remove text pattern from file name
    public void removeFromFileName(String textToRemove) {
        String currentName;
        String newName;

        // get names
        for (MP3File file : mp3Files) {
            // save the current name
            currentName = file.getFile().getName();

            // remove target text from file
            newName = currentName.replace(textToRemove, "");

            // get file properties from mp3 obj then get path
            Path oldFilePath = Paths.get(file.getFile().getPath());
            // get file properties from mp3 obj then get path without a file name, add newName as file
            Path newFilePath = Paths.get(getPathNoName(file.getFile()) + newName);

            Log.print("old name", currentName);
            Log.print("new name", newName);

            try{
                Files.copy(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (Exception e) {
                Log.errorE("Failed to save file with new name", e);
            }
        }
    }

    // general algorithm for modifying metadata
    private void modifySimpleMetadata(FieldKey fieldKey, String text) {
        for (MP3File file : mp3Files) {
            // get tag which contains metadata
            Tag tag = file.getTag();

            Log.print("old " + fieldKey.name() + " text", tag.getFirst(fieldKey));

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

            Log.print("new " + fieldKey.name() + " text", tag.getFirst(fieldKey));
        }
    }

    // change artist text
    public void modifyArtist(String artistText) {
        modifySimpleMetadata(FieldKey.ARTIST, artistText);
    }

    // change album text
    public void modifyAlbum(String albumText) {
        modifySimpleMetadata(FieldKey.ALBUM, albumText);
    }

    // change genre text
    public void modifyGenre(String genreText) {
        modifySimpleMetadata(FieldKey.GENRE, genreText);
    }

    // change album art
    public void changeArt(String pathToArt) {
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

    // display data of all audio files in folder (order by name)
    public void displayData() {
        // print top data
        // folder name      total files
        if (mp3Files.size() > 0) {
            System.out.printf("%s %-50s %s %s\n\n", "Path:", getPathNoName(mp3Files.get(0).getFile()), "Total File Count:" , mp3Files.size());
        }
        else {
            System.out.println("No files in folder");
        }

        // format for file data
        String format = "%-50s %-30s %-30s %-30s %-30s\n";

        // print header
        System.out.printf(format,
                "Name",
                "Album",
                "Artist",
                "Genre",
                "Length"
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

    // display data order by album name
    public void displayDataAlbum() {
        //TODO work on displayDataAlbum(), might structure this and displayData() so one is the overload of the other
    }

    // create folders based on X then put the target files in the corresponding folders where x is
    // metadata such as Album, Artist, Genre
    public void createFoldersFor(FieldKey sortCriteria) {
        //TODO work on createFoldersFor()
    }

    // normalize mp3 files, ask for integratedLoudness and truePeak
    public void normalizeFiles(double integratedLoudness, double truePeak, String output) {
        // create ffmpegWrapper obj to run loudness normalization command
        FFmpegWrapper fFmpegWrapper = new FFmpegWrapper();

        // TODO make progress bar based on curr loop count of total
        // normalize loudness of all target files
        for (MP3File file : mp3Files) {
            fFmpegWrapper.normalizeLoudness(file.getFile().getPath(), integratedLoudness, truePeak, output);
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

    // private utility methods for class ----------------------------------------------

    // return file's path but exclude the file from the path string
    private String getPathNoName(File file) {
        // get current file path
        String currentPath = file.getPath();

        // remove current file name from path
        // (will remove file name and extension)
        // create new string starting from beginning of path and ending at \ (inclusive)
        String newPath = currentPath.substring(0, currentPath.lastIndexOf("\\") + 1);

        return newPath;
    }

    // convert seconds min:sec format
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

        // check if path leads to folder
        if (file.isDirectory() & !file.isFile()) {
            Log.print("is folder");

            // loop through files, convert each to MP3 obj and add to arraylist
            for (File fileInLoop : file.listFiles()) {
                Log.print("file", fileInLoop);

                // TODO determine if file found is mp3

                // might not be able to read file
                try {
                    MP3File convertedFile = (MP3File) AudioFileIO.read(fileInLoop);

                    mp3Files.add(convertedFile);
                } catch (Exception e) {
                    Log.error("Unable to internally classify file with name", fileInLoop.getName());
                    Log.error("Error msg", e.getMessage());
                }
            }

            // verify number of files found
            Log.print("number of files found", mp3Files.size());
        }

        // check if path leads to file
        else if (!file.isDirectory() & file.isFile()) {
            Log.print("is file");

            // might not be able to read file
            try {
                MP3File convertedFile = (MP3File) AudioFileIO.read(file);

                mp3Files.add(convertedFile);
            }
            catch (Exception e) {
                Log.error("Unable to internally classify file with name", file.getName());
                Log.error("Error msg", e.getMessage());
            }

            // verify that there is only one file
            Log.print("Only one file found", mp3Files.size() == 1);
        }
    }

    //---------------

    // get path
    public String getPath() {
        return path;
    }

    // set a valid path
    public void setPath(String path) {
        // check if path is valid

        // create path obj so the path can be validated
        File file = new File(path);

        // set path if valid
        if (file.isDirectory() || file.isFile()) {
            this.path = path;
        }
        else {
            Log.error("Path " + path + " is invalid. Program will default to empty path.");
        }
    }
}
