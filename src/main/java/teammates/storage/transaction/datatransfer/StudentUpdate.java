package teammates.storage.transaction.datatransfer;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * A data class to contain old and new {@link StudentAttributes} in an update.
 */
public class StudentUpdate {
    private StudentAttributes originalStudent;
    private StudentAttributes updatedStudent;

    public void setOriginalStudent(StudentAttributes originalStudent) {
        this.originalStudent = originalStudent;
    }

    public void setUpdatedStudent(StudentAttributes updatedStudent) {
        this.updatedStudent = updatedStudent;
    }

    public StudentAttributes getOriginalStudent() {
        return originalStudent;
    }

    public StudentAttributes getUpdatedStudent() {
        return updatedStudent;
    }
}

