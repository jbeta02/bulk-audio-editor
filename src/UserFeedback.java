public class UserFeedback {

    public static void progressBar(String label, int current, int end) {

        // check if there is still progress to bar left
        if (current <= end) {
            double currentPercent = (double) current / end * 100;

            // format percentage
            String percent = String.format("%.0f", currentPercent);

            // print progress bar
            System.out.printf("%s %s %-8s %s", label, buildProgressBar(current, end), percent + " %", current + "/" + end + " \r");
        }
    }

    // build a progress bar
    private static String buildProgressBar(int current, int end) {

        // calc current position of progressBar assuming bar is 20 chars wide
        int barWidth = 20;
        int currPos = (int) (current / (double) end * barWidth);

        // build progress bar based on current work on job
        String progress = "";
        for (int i = 0; i < currPos; i++) {

            // add tail
            progress += "=";

            // only add head to end
            if ((i + 1) == currPos) {
                progress += ">";
            }
        }
        // add start and end to progress bar
        String progressBar = String.format("%-22s%s", "[" + progress, "]");

        return progressBar;
    }

    public static void print(String statement) {
        System.out.println(statement);
    }

    public static void printIndent(String label, Object value) {
        String format = "%-30s %s\n";
        System.out.printf(format, label + ":", value);
    }
}
