// Purpose: Quick testing class

public class Test {
    public static void main(String[] args) {

        MP3Editor mp3Editor = new MP3Editor("test-mp3/"); // test-mp3/
        //MP3Editor mp3Editor = new MP3Editor("test-mp3/");

        //mp3Editor.removeFromFileName("2");

        //mp3Editor.displayData();

        String inputFile = "\"F:\\Software\\Java Apps\\MP3Editor\\test-mp3\\in-She's Back .mp3\""; //in-She's Back
        String outputFile = "\"F:\\Software\\Java Apps\\MP3Editor\\test-mp3\\out1-She's Back .mp3\"";

        //TODO test values with other test files
        mp3Editor.normalizeVolume(inputFile, outputFile, -14, -5, 11);

    }
}