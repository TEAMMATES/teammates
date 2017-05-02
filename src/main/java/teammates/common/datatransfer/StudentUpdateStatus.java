package teammates.common.datatransfer;

/**
 * Represents a student's update status, typically after enrollment.
 */
public enum StudentUpdateStatus {

    ERROR(0),
    NEW(1),
    MODIFIED(2),
    UNMODIFIED(3),
    NOT_IN_ENROLL_LIST(4),
    UNKNOWN(5);

    public static final int STATUS_COUNT = 6;

    public final int numericRepresentation;

    StudentUpdateStatus(int numericRepresentation) {
        this.numericRepresentation = numericRepresentation;
    }

    public static StudentUpdateStatus enumRepresentation(int numericRepresentation) {
        switch (numericRepresentation) {
        case 0:
            return ERROR;
        case 1:
            return NEW;
        case 2:
            return MODIFIED;
        case 3:
            return UNMODIFIED;
        case 4:
            return NOT_IN_ENROLL_LIST;
        default:
            return UNKNOWN;
        }
    }

}
