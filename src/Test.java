// Purpose: Quick testing class

import java.io.IOException;

public class Test {
    public static void main(String[] args) {

        MP3Editor mp3Editor = new MP3Editor("test-mp3/out23-This Fffire.mp3"); // test-mp3/
        //MP3Editor mp3Editor = new MP3Editor("test-mp3/");

        //mp3Editor.removeFromFileName("2");

        //mp3Editor.displayData();

//        String inputFile = "\"F:\\Software\\Java Apps\\MP3Editor\\test-mp3\\in-The Reason.mp3\"";
//        String outputFile = "\"F:\\Software\\Java Apps\\MP3Editor\\test-mp3\\out23-The Reason.mp3\"";

//        String inputFile = "\"F:\\Software\\Java Apps\\MP3Editor\\test-mp3\\in-She's Back.mp3\"";
//        String outputFile = "\"F:\\Software\\Java Apps\\MP3Editor\\test-mp3\\out23-She's Back.mp3\"";
//
        // add \" for ffmpgeg direct inputs and no \" when entering as path such as for FileHandler stuff
        String inputFile = "F:\\Software\\Java Apps\\MP3Editor\\test-mp3\\"; // F:\Software\Java Apps\MP3Editor\test-mp3\out23-This Fffire.mp3
        String outputFile = "F:\\Software\\Java Apps\\MP3Editor\\test-mp3\\out23-This Fffire.mp3";
        String outputPath = "F:\\Software\\Java Apps\\MP3Editor\\test-mp3-2\\"; // F:\Software\Java Apps\MP3Editor\test-mp3-2

        //mp3Editor.normalizeLoudness(inputFile, -23, -2, outputFile); // default: -23, -2, 7 or preferred?: -16, -2, 7

        try {
            Double.parseDouble("abc");
        }
        catch (Exception e) {
            Log.errorE("not double ", e);
        }
    }
}