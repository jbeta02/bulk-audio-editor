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
    public MP3Editor(String path){
        // set instance variables
        setPath(path);
        setFiles(path);
    }

    // add text to either beginning (true) or end (false) of file
    public void addToFileName(boolean addToStart, String textToAdd){
        String currentName;
        String newName;

        // get names
        for (MP3File file : mp3Files){
            // save the current name
            currentName = file.getFile().getName();

            // add text to start of file
            if (addToStart){
                newName = textToAdd + currentName;
            }
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
            catch (Exception e){
                Log.errorE("Failed to save file with new name", e);
            }

            //TODO delete old file when done (complete this after testing thoroughly)
        }
    }

    public void removeFromFileName(String textToRemove){
        String currentName;
        String newName;

        // get names
        for (MP3File file : mp3Files){
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
            catch (Exception e){
                Log.errorE("Failed to save file with new name", e);
            }

            //TODO delete old file when done (complete this after testing thoroughly)
        }
    }

    // general algorithm for modifying metadata
    private void modifySimpleMetadata(FieldKey fieldKey, String text){
        for (MP3File file : mp3Files){
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
    public void modifyArtist(String artistText){
        modifySimpleMetadata(FieldKey.ARTIST, artistText);
    }

    // change album text
    public void modifyAlbum(String albumText){
        modifySimpleMetadata(FieldKey.ALBUM, albumText);
    }

    // change genre text
    public void modifyGenre(String genreText){
        modifySimpleMetadata(FieldKey.GENRE, genreText);
    }

    public void changeArt(String pathToArt){
        for (MP3File file : mp3Files){
            // get tag which contains metadata
            Tag tag = file.getTag();

            try {
                // create Artwork obj using ArtworkFactory with pathToArt as input
                Artwork artwork = ArtworkFactory.createArtworkFromFile(new File(pathToArt));

                // set new cover art
                tag.setField(artwork);

                // save new cover art
                file.commit();

                // will create an "AlbumArtSmall.jpg"
                // will create a "Folder.jpg"
                // seem to only be visible in IDE

                // files seem to be created when Windows Media Player is opened. Files can be deleted after they are created
                // without any apparent consequence

            } catch (Exception e) {
                Log.errorE("Unable to set art cover using path: " + pathToArt, e);
            }
        }
    }

    public void displayData(){
        //TODO cont here

        // get all songs then print list of songs and data

        // folder name      total files

        // name     artist      album   genre       len

        System.out.printf("%s %s", getPath(), );

    }

    // return file's path but exclude the file from the path string
    private String getPathNoName(File file){
        // get current file path
        String currentPath = file.getPath();

        // remove current file name from path
        // (will remove file name and extension)
        // create new string starting from beginning of path and ending at \ (inclusive)
        String newPath = currentPath.substring(0, currentPath.lastIndexOf("\\") + 1);

        return newPath;
    }

    // accessors and mutators ----------------------------------------------

    public ArrayList<MP3File> getFiles(){
        return mp3Files;
    }

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
            } catch (Exception e) {
                Log.error("Unable to internally classify file with name", file.getName());
                Log.error("Error msg", e.getMessage());
            }

            // verify that there is only one file
            Log.print("Only one file found", mp3Files.size() == 1);
        }
    }

    public String getPath(){
        return path;
    }

    public boolean setPath(String path){
        // check if path is valid
        boolean pathSet = false;

        // create path obj so the path can be validated
        File file = new File(path);

        // set path if valid
        if (file.isDirectory() || file.isFile()){
            this.path = path;
            pathSet = true;
        }
        else {
            Log.error("Path not found.");
        }

        return pathSet;
    }
}
