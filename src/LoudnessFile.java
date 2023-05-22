import org.jaudiotagger.audio.AudioFile;

// Purpose: Class to bundle up an audioFile with loudness data
public class LoudnessFile {

    private AudioFile audioFile;
    private double measuredI; // measured integrated loudness
    private double measuredTp; // measured true peak
    private double measuredLRA; // measured loudness range

    public LoudnessFile(AudioFile audioFile) {
        FFmpegWrapper fFmpegWrapper = new FFmpegWrapper();

        // run ffmpeg command to get loudness stats of file
        fFmpegWrapper.extractLoudnessStats(audioFile.getFile().getPath());

        // save loudness data
        measuredI = fFmpegWrapper.getMeasuredI();
        measuredTp = fFmpegWrapper.getMeasuredTp();
        measuredLRA = fFmpegWrapper.getMeasuredLRA();

        this.audioFile = audioFile;
    }

    public AudioFile getAudioFile() {
        return audioFile;
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
