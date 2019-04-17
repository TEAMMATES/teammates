package teammates.e2e.cases.lnp;

public class ReportAnalysis {

    private String transaction;
    private String throughput;
    private String pct1ResTime; // 90th percentile
    private int sampleCount;
    private int errorCount;
    private double meanResTime;

    public String getTransaction() {
        return transaction;
    }

    public String getThroughput() {
        return throughput;
    }

    public String getPct3ResTime() {
        return pct1ResTime;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public double getMeanResTime() {
        return meanResTime;
    }

    public void showCompleteAnalysis(String testName) {
        System.out.print(formatAnalysis(testName));
    }

    private String formatAnalysis(String testName) {
        return testName + ": " + sampleCount + " samples, throughput: " + throughput + " mean res time: " + meanResTime
                + " 90th Percentile: " + pct1ResTime + " Err: " + errorCount + " (" + 1.0 * errorCount / sampleCount + "%) ";
    }
}
