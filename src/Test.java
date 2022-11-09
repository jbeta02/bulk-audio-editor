// Purpose: Quick testing class

public class Test {
    public static void main(String[] args) {

        MP3Editor mp3Editor = new MP3Editor("test-mp3/2Volt.mp3");
        //MP3Editor mp3Editor = new MP3Editor("test-mp3/");

        //mp3Editor.removeFromFileName("2");

        mp3Editor.modifyAlbum("new2 alb");

    }
}