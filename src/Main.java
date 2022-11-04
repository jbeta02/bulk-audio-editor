// Purpose: Provide interface for user to interact with program

public class Main {
    public static void main(String[] args) {

//        MP3Editor mp3Editor = new MP3Editor("test-mp3/2Volt.mp3");
//
//        mp3Editor.removeFromFileName("2");
        ProgramUI programUI = new ProgramUI();

        programUI.runCycle();

//        MP3Editor mp3Editor = new MP3Editor("test-mp3/");

    }
}