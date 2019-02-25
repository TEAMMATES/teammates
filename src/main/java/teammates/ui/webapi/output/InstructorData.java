package teammates.ui.webapi.output;

/**
 * The API output format of an Instructor.
 */
public class InstructorData extends ApiOutput {
    private final String name;

    public InstructorData(String instructorName) {
        this.name = instructorName;
    }

    public String getName() {
        return name;
    }
}
