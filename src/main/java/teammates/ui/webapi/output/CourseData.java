package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.CourseAttributes;

/**
 * The API output format of {@link CourseAttributes}.
 */
public class CourseData extends ApiOutput {
    private String id;
    private String name;
    private String timeZone;

    public CourseData(CourseAttributes courseAttributes) {
        this.id = courseAttributes.getId();
        this.name = courseAttributes.getName();
        this.timeZone = courseAttributes.getTimeZone().getId();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTimeZone() {
        return timeZone;
    }
}
