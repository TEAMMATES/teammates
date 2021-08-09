package teammates.e2e.util;

/**
 * Represents a task that can be retried.
 */
public abstract class Retryable {

    private String name;

    public Retryable(String name) {
        this.name = name;
    }

    /**
     * Executes a method that runs the task once.
     */
    protected abstract void run();

    /**
     * Returns the name of the task.
     */
    public String getName() {
        return name;
    }
}
