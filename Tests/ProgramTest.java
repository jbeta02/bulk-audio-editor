import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

class ProgramTest {

    @Test
    @DisplayName("Single Run All Commands Without Output")
    void singleRunCommandsWithNoOutput() {
        String inputPath = "F:\\Software\\Java Apps\\MP3Editor\\test-mp3-1";
        String outputPath = "";


        Program.runProgram(inputPath, "ab", "new-", "");

        for (File file : FileHandler.getFolders(inputPath)) {
            System.out.println(file.getName().contains("new-"));

            Assertions.assertTrue(file.getName().contains("new-"));
        }

        for (File file : FileHandler.getFolders(inputPath)) {
            file.delete();
        }
    }

    @Test
    @DisplayName("Single Run All Commands With Output")
    void singleRunCommandsWithOutput() {

    }
}