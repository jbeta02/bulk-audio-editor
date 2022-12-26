import org.jaudiotagger.audio.mp3.MP3File;

// Purpose: Class to bundle up an mp3File with loudness data
public class LoudnessFile {

    private MP3File mp3File;
    private double measuredI; // measured integrated loudness
    private double measuredTp; // measured true peak
    private double measuredLRA; // measured loudness range

    public LoudnessFile(MP3File mp3File) {
        FFmpegWrapper fFmpegWrapper = new FFmpegWrapper();

        // run ffmpeg command to get loudness stats of file
        fFmpegWrapper.extractLoudnessStats(mp3File.getFile().getPath());

        // save loudness data
        measuredI = fFmpegWrapper.getMeasuredI();
        measuredTp = fFmpegWrapper.getMeasuredTp();
        measuredLRA = fFmpegWrapper.getMeasuredLRA();

        this.mp3File = mp3File;
    }

    public MP3File getMp3File() {
        return mp3File;
    }

    public double getMeasuredI() {
        return measuredI;
    }

    public double getMeasuredTp() {
        return measuredTp;
    }

    public double getMeasuredLRA() {
        return measuredLRA;
    }

}
