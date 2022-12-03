import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FFmpegWrapper {

    public static final double INTEGRATED_LOUDNESS = -16;
    public static final double TRUE_PEAK = -2;
    public static final double LOUDNESS_RANGE = 7;

    private final String FFMPEG = "ffmpeg/bin/ffmpeg.exe";

    private double integratedLoudness;
    private double truePeak;

    private double measuredI;
    private double measuredTp;
    private double measuredLRA;
    private double measuredThresh;
    private double offset;

    // references:
    // provides definitions to terms
    // https://en.wikipedia.org/wiki/Audio_normalization
    // https://producerhive.com/music-production-recording-tips/lufs-vs-dbfs-differences/#:~:text=LUFS%20is%20a%20measurement%20of,level%2C%20without%20human%20perceptual%20filters.

    // solution to post provides a lot of information on using ffmpeg, ffmpeg-normalize docs is also good resource
    // https://superuser.com/questions/323119/how-can-i-normalize-audio-using-ffmpeg

    // FFmpeg docs for loudness normalization
    // http://ffmpeg.org/ffmpeg-all.html#loudnorm

    // implementation of loudness normalization using ffmpeg
    public void normalizeLoudness(String inputFile, double integrateLoudness, double truePeak, String outputFile){
        try{

            setIntegratedLoudness(integrateLoudness);
            setTruePeak(truePeak);

            extractLoudnessStats(inputFile);

            String[] passTwo = {"-i", inputFile, "-af",
                    "loudnorm=" +
                            "I=" + integrateLoudness +
                            ":TP=" + truePeak +
                            ":LRA=" + measuredLRA + // use measured LRA as target to force linear normalization
                            ":measured_I=" + measuredI +
                            ":measured_LRA=" + measuredLRA +
                            ":measured_TP=" + measuredTp +
                            ":measured_thresh=" + measuredThresh +
                            ":offset=" + offset +
                            ":linear=true" +
                            ":print_format=json",
                    "-hide_banner", "-ar", "48k", outputFile}; //TODO add -y modifier to override files if needed

            Log.print("running command (pass two)", FFMPEG + arrayToString(passTwo));
            ArrayList<String> commandResult = runFfmpegCommand(passTwo);

            for (String outputLine: commandResult){
                Log.print("command output", outputLine);
            }
        }
        catch (Exception e){
            Log.errorE("error running ffmpeg", e);
        }
    }

    // get loudness stats of file, use getMeasured---() methods to get values after using this method
    public void extractLoudnessStats(String inputFile){
        String[] passOne = {"-i", inputFile, "-af",
                "loudnorm=" +
                        "I=" + INTEGRATED_LOUDNESS +
                        ":TP=" + TRUE_PEAK +
                        ":LRA=" + LOUDNESS_RANGE +
                        ":print_format=json",
                "-hide_banner", "-f", "null", "-"};

        Log.print("running command (pass one)", FFMPEG + arrayToString(passOne));
        ArrayList<String> commandResult = runFfmpegCommand(passOne);

        for (String outputLine: commandResult){
            Log.print("command output", outputLine);
        }

        ArrayList<Pair<String, Double>> loudnessData = extractDataPairs(commandResult);

        measuredI = loudnessData.get(0).getValue();
        measuredTp = loudnessData.get(1).getValue();
        measuredLRA = loudnessData.get(2).getValue();
        measuredThresh = loudnessData.get(3).getValue();
        offset = loudnessData.get(4).getValue();
    }

    // private utility methods for class ----------------------------------------------

    private ArrayList<Pair<String, Double>> extractDataPairs(ArrayList<String> outputLines){
        ArrayList<Pair<String, Double>> targetData = new ArrayList<>();

        for (String line : outputLines){
            if (line.contains("input_i")){
                targetData.add(extractJsonKeyValue(line));
            }
            if (line.contains("input_tp")){
                targetData.add(extractJsonKeyValue(line));
            }
            if (line.contains("input_lra")){
                targetData.add(extractJsonKeyValue(line));
            }
            if (line.contains("input_thresh")){
                targetData.add(extractJsonKeyValue(line));
            }
            if (line.contains("target_offset")){
                targetData.add(extractJsonKeyValue(line));
                break;
            }
        }

        return targetData;
    }

    private Pair<String, Double> extractJsonKeyValue(String jsonLine){
        jsonLine = jsonLine.replace("\"", "").replace(",", "");
        String[] LineParts = jsonLine.split(":");

        String key = LineParts[0].trim();
        double value  = Double.parseDouble(LineParts[1].trim());

        //Log.print("extracted Data", key + ", " + value);

        return new Pair<>(key, value);
    }

    private String arrayToString(String[] stringArray){
        String string = "";
        for (String s : stringArray) {
            string += " " + s;
        }

        return string;
    }

    // run an FFmpeg command and collect its output
    private ArrayList<String> runFfmpegCommand(String[] args){
        // ArrayList will hold output lines
        ArrayList<String> output = new ArrayList<>();

        try{
            // ArrayList will hold arguments that make up command
            ArrayList<String> command = new ArrayList<>();

            // start with FFmpeg
            command.add(FFMPEG);

            // add the other args
            for (String arg: args){
                command.add(arg);
            }

            // run ffmpeg exe
            ProcessBuilder build = new ProcessBuilder(command);
            build.redirectErrorStream(true);
            Process process = build.start();

            // wait for completion of FFmpeg command
            process.waitFor();

            // read exe output stream
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // collect output
            for(Object line: reader.lines().toArray()){
                String stringLine = (String) line;
                output.add(stringLine);
            }

            // close stream since no longer needed
            inputStream.close();
        }
        catch (Exception e){
            Log.errorE("error running ffmpeg", e);
        }

        return output;
    }

    private boolean withinRange(double value, double min, double max){
        boolean inRange = false;
        if (value < min || value > max){
            Log.error("value entered " + value + ", must be between " + min  + " - " + max);
        }
        else {
            inRange = true;
        }
        return inRange;
    }

    // accessors and mutators ----------------------------------------------

    //TODO change how program reacts to bad data (curr: set default vals > new: ask to enter values again)

    public double getIntegratedLoudness() {
        return integratedLoudness;
    }

    public void setIntegratedLoudness(double targetInLUFS) {
        if (!withinRange(targetInLUFS, -70, -5)){
            Log.error("Will set to default integrated loudness to ", INTEGRATED_LOUDNESS + " LUFS");
        }
        else {
            integratedLoudness = targetInLUFS;
        }
    }

    // won't set a loudness range (LRA) since it would lead to dynamic normalization instead of linear
//    public double getLoudnessRange() {
//        return loudnessRange;
//    }
//
//    public void setLoudnessRange(double targetInLU) {
//        if (withinRange(targetInLU, 1, 50)){
//            Log.error("Will set default loudness range to ", LOUDNESS_RANGE + " LU");
//        }
//        else {
//            loudnessRange = targetInLU;
//        }
//    }

    public double getTruePeak() {
        return truePeak;
    }

    public void setTruePeak(double targetInDB) {
        if (!withinRange(targetInDB, -9, 0)){
            Log.error("Will set default true peak to ", TRUE_PEAK);
        }
        else {
            truePeak = targetInDB;
        }
    }

    // following instance vars are set by ffmpeg so wrapper class does not need to check through mutators
    public double getMeasuredI() {
        return measuredI;
    }

    public double getMeasuredTp() {
        return measuredTp;
    }

    public double getMeasuredLRA() {
        return measuredLRA;
    }

    public double getMeasuredThresh() {
        return measuredThresh;
    }

    public double getOffset() {
        return offset;
    }
}