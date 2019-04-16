package teammates.e2e.cases.lnp;

public class ReportAnalysis {

    private String transaction;
    private String throughput;
    private String pct3ResTime;
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
        return pct3ResTime;
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
}
