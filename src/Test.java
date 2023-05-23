// Purpose: Quick testing class

import org.jaudiotagger.tag.FieldKey;

public class Test {
    public static void main(String[] args) {

        // add \" for ffmpeg direct inputs and no \" when entering as path such as for FileHandler stuff

        String inputFlac = "F:\\Software\\Java Apps\\MP3Editor\\test-mp3-1\\20 - Misa no Tema A.flac";
        String inputMp3 = "F:\\Software\\Java Apps\\MP3Editor\\test-mp3-1\\20 - Misa no Theme A.mp3";

        String inputPath = "F:\\Software\\Java Apps\\MP3Editor\\test-mp3-1";

        String outPath = "F:\\Software\\Java Apps\\MP3Editor\\test-out";

        AudioEditor audioEditor = new AudioEditor(inputPath);

        //audioEditor.displayDataByLoudness();

        audioEditor.normalizeFiles(outPath);

        //audioEditor.displayDataByLoudness();

        //audioEditor.displayDataByAlbum();

    }
}