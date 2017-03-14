package teammates.client.scripts.helper;

/**
 * Utility class to be used with loops to print messages regularly.
 */
public class LoopHelper {

    public final int printCycle;
    public final String message;
    private int count;

    public LoopHelper(int printCycle, String message) {
        this.printCycle = printCycle;
        this.message = message;
        count = 0;
    }

    /**
     * Increments count and prints the count and message on system output when count is a multiple of printCycle.
     */
    public void recordLoop() {
        count++;
        if (count % printCycle == 0) {
            System.out.printf("[%d] %s\n", count, message);
        }
    }

    public int getCount() {
        return count;
    }

}
