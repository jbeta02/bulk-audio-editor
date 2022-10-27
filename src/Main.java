import com.mpatric.mp3agic.Mp3File;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        // mp3agic example
//        Mp3File mp3File1;
//
//        try {
//            mp3File1 = new Mp3File("test-mp3/She's Back.mp3");
//
//            if (mp3File1.hasId3v1Tag()){
//                System.out.println("has 3v1 tag");
//            }
//
//            if (mp3File1.hasId3v2Tag()){
//                System.out.println("has 3v2 tag"); // the mp3 files I have use ID3v2 Tags
//            }
//        }
//        catch (Exception e){
//            System.out.println("Error: " + e.getMessage());
//        }

        /////////////////////////////////////////////////////////////////////

        // jaudiotagger example

        //TODO I like jaudiotagger better since it has more features and supports more formats
        // continue the project using this library instead of the other one

        MP3File mp3File2;

        File file = new File("test-mp3/DOA.mp3");

        try {
            mp3File2 = (MP3File) AudioFileIO.read(file);

            if (mp3File2.hasID3v1Tag()){
                System.out.println("has 3v1 tag");
            }

            if (mp3File2.hasID3v2Tag()){
                System.out.println("has 3v2 tag"); // the mp3 files I have use ID3v2 Tags
            }

            Tag tag = mp3File2.getTag();

            String s = tag.getFirst(FieldKey.ALBUM);

            System.out.println(s);
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}