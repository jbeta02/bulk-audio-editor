/*
 * Name:        Jesus Betancourt
 *
 * Course:      CS-12, Fall 2022
 *
 * Date:        10/27/22
 *
 * Filename:    Main.java
 *
 * Purpose:     Main program class
 */

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        MP3File mp3File2;

        File file = new File("test-mp3/DOA.mp3");

        try {
            mp3File2 = (MP3File) AudioFileIO.read(file);

            if (mp3File2.hasID3v1Tag()){
                Log.print("has 3v1 tag");
            }

            if (mp3File2.hasID3v2Tag()){
                Log.print("has 3v2 tag"); // the mp3 files I have use ID3v2 Tags
            }

            Tag tag = mp3File2.getTag();

            String s = tag.getFirst(FieldKey.ALBUM);

            System.out.println(s);
        }
        catch (Exception e){
            Log.error("" + e.getMessage());
        }

        Log.print("input");
        Log.print("This is mp3 file 2", "input");
        Log.print("mp3 file 3", "input");

        Log.error("didn't find tags", "input");
        Log.error("cant read mp3", "input");
    }
}