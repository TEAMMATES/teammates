package teammates.client.scripts.util;

/**
 * Helper class to help manage loops.
 */
public class LoopHelper {

    /**
     * The number of iterations between every printing.
     */
    public final int printCycle;

    /**
     * The message which will be printed regularly.
     */
    public final String message;

    private int count;

    /**
     * Constructs a {@link LoopHelper} object which prints {@code message} for every {@code printCycle} iterations.
     */
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
