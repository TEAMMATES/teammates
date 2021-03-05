package teammates.storage.transaction.datatransfer;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * A data class to contain old and new {@link StudentAttributes} in an update.
 */
public class StudentUpdate {
    private StudentAttributes before;
    private StudentAttributes after;

    public void setBefore(StudentAttributes before) {
        this.before = before;
    }

    public void setAfter(StudentAttributes after) {
        this.after = after;
    }

    public StudentAttributes getBefore() {
        return before;
    }

    public StudentAttributes getAfter() {
        return after;
    }
}

