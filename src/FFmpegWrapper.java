import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// Purpose: Make FFmpeg easier to use for audio files and specifically for loudness normalization

public class FFmpegWrapper {

    public static final double INTEGRATED_LOUDNESS = -16; // in LUFS
    public static final double TRUE_PEAK = -2; // in LU
    public static final double LOUDNESS_RANGE = 7; // in dBTP

    private final String FFMPEG = "ffmpeg/bin/ffmpeg.exe";

    private double integratedLoudness; // (I) in LUFS
    private double truePeak; // (TP) in LU

    private double measuredI; // measured integrated loudness
    private double measuredTp; // measured true peak
    private double measuredLRA; // measured loudness range
    private double measuredThresh;
    private double offset;

    private final String[] SUPPORTED_CODECS = {
            "libmp3lame", // for mp3
            "flac",
    };

    private String mp3Bitrate = "320k";

    // references:
    // provides definitions to terms
    // https://en.wikipedia.org/wiki/Audio_normalization
    // https://producerhive.com/music-production-recording-tips/lufs-vs-dbfs-differences/#:~:text=LUFS%20is%20a%20measurement%20of,level%2C%20without%20human%20perceptual%20filters.

    // solution to post provides a lot of information on using ffmpeg, ffmpeg-normalize docs is also good resource
    // https://superuser.com/questions/323119/how-can-i-normalize-audio-using-ffmpeg

    // FFmpeg docs for loudness normalization
    // http://ffmpeg.org/ffmpeg-all.html#loudnorm

    // implementation of loudness normalization using ffmpeg
    public void normalizeLoudness(String inputFile, double integrateLoudness, double truePeak, String outputFile) {

        String ext = inputFile.substring(inputFile.lastIndexOf(".") + 1);

        String[] bitrate = {"", ""};

        String codec = "noCodecSet";

        // is mp3
        if (ext.equalsIgnoreCase("mp3")) {
            // set codec and desired bit rate for mp3
            codec = SUPPORTED_CODECS[0];
            bitrate[0] = "-b:a"; // used for bitrate
            bitrate[1] = mp3Bitrate; // in kbps
        }

        // is flac
        else if (ext.equalsIgnoreCase("flac")) {
            // set codec and use current file bitrate
            codec = SUPPORTED_CODECS[1];
            // don't set specific bitrate instead use default bitrate for codec
        }

        // check if values set successfully set
        if (setIntegratedLoudness(integrateLoudness) && setTruePeak(truePeak)) {
            try{
                // check that input integratedLoudness and truePeak are valid
                setIntegratedLoudness(integrateLoudness);
                setTruePeak(truePeak);

                // extract loudness stats for future use
                extractLoudnessStats(inputFile);

                // setup ffmpeg command
                String[] passTwo = {"-i", inputFile,
                        //"-nostdin", // disable console interaction while command is being run
                        "-y", // override output files without asking
                        "-filter_complex", // will use filter loudnorm in following line
                        "[0:0]loudnorm=" +
                                "I=" + integrateLoudness +
                                ":TP=" + truePeak +
                                ":LRA=" + measuredLRA + // use measured LRA as target to force linear normalization
                                ":offset=" + offset +
                                ":measured_I=" + measuredI +
                                ":measured_TP=" + measuredTp +
                                ":measured_LRA=" + measuredLRA +
                                ":measured_thresh=" + measuredThresh +
                                ":linear=true" +
                                ":print_format=json[norm0]",
                        "-map", "[norm0]",
                        "-c:a", // used for codec (audio copied as is)
                        codec, // set codec
                        bitrate[0], // used for bitrate
                        bitrate[1], //
                        "-c:s", "copy", // copy audio and video streams of input to output without re-encoding
                        outputFile, // output file
                        "-hide_banner", // hide ffmpeg banner in output
                };

                // run command and save output to print
                ArrayList<String> commandResult = runFfmpegCommand(passTwo);
//                Log.print("running ffmpeg command (normalize loudness)", FFMPEG + arrayToString(passTwo));

                // print output
//                for (String outputLine : commandResult) {
//                    Log.print("command output", outputLine);
//                }
            }
            catch (TimeoutException timeoutException) {
                Log.error("ffmpeg timed-out on normalization process for file: " + inputFile + ". Skipping process for file...");
            }
//            catch (Exception e) {
//                Log.errorE("error running ffmpeg", e);
//            }
        }
        // values not set successfully, tell user that command was skipped
        else {
            Log.error("Loudness values not set correctly, normalization was skipped. Run command again with new values");
        }
    }

    private void parseStreams(String inputFile) {

        try {
            // setup ffmpeg command
            String[] prePass = {"-i", inputFile,
                    "-c", "copy", // copy audio and video streams of input to output without re-encoding
                    "-t", "0", // used to specify duration
                    "-map", "0", // select which streams to use from input to use in output (in this case use all streams)
                    "-f", // force file format
                    "null", "NUL", // no output
                    "-hide_banner"}; // hide ffmpeg banner in output

            // run command and save output to print
            ArrayList<String> commandResult = runFfmpegCommand(prePass);
//            Log.print("running ffmpeg command (parse streams)", FFMPEG + arrayToString(prePass));

            // print output
//            for (String outputLine : commandResult) {
//                Log.print("command output", outputLine);
//            }
        }
        catch (TimeoutException timeoutException) {
            Log.error("ffmpeg timed-out on stream parsing process for file: " + inputFile + ". Skipping process for file...");
        }
    }

    // get loudness stats of file, use getMeasured---() methods to get values after using this method
    public void extractLoudnessStats(String inputFile) {

        try {
            // parse streams
            parseStreams(inputFile);

            // setup ffmpeg command
            String[] passOne = {"-i", inputFile,
                    //"-nostdin", // disable console interaction while command is being run
                    //"-y", // override output files without asking
                    "-filter_complex", // will use filter loudnorm in following line
                    "[0:0]loudnorm=" +
                            "I=" + INTEGRATED_LOUDNESS +
                            ":TP=" + TRUE_PEAK +
                            ":LRA=" + LOUDNESS_RANGE +
                            ":offset=" + 0.0 +
                            ":print_format=json",
                    "-vn", // skip video
                    "-sn", // skip subtitles
                    "-f", // force file format
                    "null", "NUL",// no output
                    "-hide_banner"}; // hide ffmpeg banner in output

            // run command and save output to print
            ArrayList<String> commandResult = runFfmpegCommand(passOne);
//            Log.print("running ffmpeg command (get loudness data)", FFMPEG + arrayToString(passOne));

            // print output
//            for (String outputLine : commandResult) {
//                Log.print("command output", outputLine);
//            }

            // grab data pairs (key, value)
            ArrayList<Pair<String, Double>> loudnessData = extractDataPairs(commandResult);

            // save values from (key, value) so they can be accessed by outside processes
            measuredI = loudnessData.get(0).getValue();
            measuredTp = loudnessData.get(1).getValue();
            measuredLRA = loudnessData.get(2).getValue();
            measuredThresh = loudnessData.get(3).getValue();
            offset = loudnessData.get(4).getValue();
        }
        catch (TimeoutException timeoutException) {
            Log.error("ffmpeg timed-out on loudness values extraction process for file: " + inputFile + ". Skipping process for file...");
        }
    }

    public void convertToMP3(String inputFile, String outputFile) {
        try {
            // setup ffmpeg command
            String[] convert = {"-i", inputFile,
                    "-y", // override output files without asking
                    "-c:a", // used for codec (audio copied as is)
                    SUPPORTED_CODECS[0], // set codec
                    "-b:a", // used for bitrate
                    mp3Bitrate, // set bitrate value
                    outputFile, // output file
                    "-hide_banner", // hide ffmpeg banner in output
            };

            // run command and save output to print
            ArrayList<String> commandResult = runFfmpegCommand(convert);
//            Log.print("running ffmpeg command (parse streams)", FFMPEG + arrayToString(prePass));

            // print output
//            for (String outputLine : commandResult) {
//                Log.print("command output", outputLine);
//            }
        }
        catch (TimeoutException timeoutException) {
            Log.error("ffmpeg timed-out on file conversion process for file: " + inputFile + ". Skipping process for file...");
        }
    }

    // private utility methods for class ----------------------------------------------

    // extract json data pairs from output
    private ArrayList<Pair<String, Double>> extractDataPairs(ArrayList<String> outputLines) {
        ArrayList<Pair<String, Double>> targetData = new ArrayList<>();

        // search output lines for desired json values then extract data
        for (String line : outputLines) {
            if (line.contains("input_i")) {
                targetData.add(extractJsonKeyValue(line));
            }
            if (line.contains("input_tp")) {
                targetData.add(extractJsonKeyValue(line));
            }
            if (line.contains("input_lra")) {
                targetData.add(extractJsonKeyValue(line));
            }
            if (line.contains("input_thresh")) {
                targetData.add(extractJsonKeyValue(line));
            }
            if (line.contains("target_offset")) {
                targetData.add(extractJsonKeyValue(line));
                break;
            }
        }

        return targetData;
    }

    // grab json key value pair from a json line
    private Pair<String, Double> extractJsonKeyValue(String jsonLine) {
        jsonLine = jsonLine.replace("\"", "").replace(",", "");
        String[] LineParts = jsonLine.split(":");

        String key = LineParts[0].trim();
        double value  = Double.parseDouble(LineParts[1].trim());

        //Log.print("extracted Data", key + ", " + value);

        return new Pair<>(key, value);
    }

    // convert an array to a string with values separated by a space
    private String arrayToString(String[] stringArray) {
        String string = "";
        // separate array values with space
        for (String s : stringArray) {
            string += " " + s;
        }

        return string;
    }

    // run an FFmpeg command and collect its output
    public ArrayList<String> runFfmpegCommand(String[] args) throws TimeoutException{
        // ArrayList will hold output lines
        ArrayList<String> output = new ArrayList<>();

        try{
            // ArrayList will hold arguments that make up command
            ArrayList<String> command = new ArrayList<>();

            // start with FFmpeg
            command.add(FFMPEG);

            // add the other args
            for (String arg : args) {
                command.add(arg);
            }

            // run ffmpeg exe
            ProcessBuilder build = new ProcessBuilder(command);
            build.redirectErrorStream(true);
            Process process = build.start();

            // wait for completion of FFmpeg command
            boolean isCompleted = process.waitFor(90, TimeUnit.SECONDS); // timeout after 1.5 min

            if (!isCompleted) {
                throw new TimeoutException();
            }

            // read exe output stream
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // collect output
            for(Object line : reader.lines().toArray()) {
                String stringLine = (String) line;
                output.add(stringLine);
            }

            // close stream since no longer needed
            inputStream.close();
        }
        catch (IOException | InterruptedException e) {
            Log.errorE("error running ffmpeg", e);
        }

        return output;
    }

    // check if value is within a range
    private boolean withinRange(double value, double min, double max) {
        boolean inRange = false;
        if (value < min || value > max) {
            Log.error("Value entered " + value + ", must be between " + min  + " and " + max);
        }
        else {
            inRange = true;
        }
        return inRange;
    }

    // accessors and mutators ----------------------------------------------

    public double getIntegratedLoudness() {
        return integratedLoudness;
    }

    // set a valid integrated loudness
    public boolean setIntegratedLoudness(double targetInLUFS) {
        boolean success = false;

        if (withinRange(targetInLUFS, -70, -5)) {
            integratedLoudness = targetInLUFS;
            success = true;
        }

        return success;
    }

    //---------------

    // won't set a loudness range (LRA) since it would lead to dynamic normalization instead of linear
//    public double getLoudnessRange() {
//        return loudnessRange;
//    }
//
//    public void setLoudnessRange(double targetInLU) {
//        if (withinRange(targetInLU, 1, 50)) {
//            Log.error("Will set default loudness range to ", LOUDNESS_RANGE + " LU");
//        }
//        else {
//            loudnessRange = targetInLU;
//        }
//    }

    //---------------

    public double getTruePeak() {
        return truePeak;
    }

    public boolean setTruePeak(double targetInDB) {
        boolean success = false;

        if (withinRange(targetInDB, -9, 0)) {
            truePeak = targetInDB;
            success = true;
        }

        return success;
    }

    //---------------

    // following instance vars are set by ffmpeg so this wrapper class does not need to use mutators
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