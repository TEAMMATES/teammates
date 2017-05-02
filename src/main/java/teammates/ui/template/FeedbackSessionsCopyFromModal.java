package teammates.ui.template;

import java.util.List;

/**
 * Data model for the modal that can be found on the feedbacks page,
 * which allows the user to copy a feedback session from an existing session.
 * Contains the options for course to copy to,
 * the name of feedback session to create,
 * and the list of feedback sessions that can be copied from.
 *
 */
public class FeedbackSessionsCopyFromModal {

    private List<FeedbackSessionsTableRow> existingFeedbackSessions;
    private String fsName;
    private List<ElementTag> coursesSelectField;

    public FeedbackSessionsCopyFromModal(List<FeedbackSessionsTableRow> existingFeedbackSessions,
                                         String fsName,
                                         List<ElementTag> coursesSelectField) {
        this.existingFeedbackSessions = existingFeedbackSessions;
        this.fsName = fsName;
        this.coursesSelectField = coursesSelectField;
    }

    public List<FeedbackSessionsTableRow> getExistingFeedbackSessions() {
        return existingFeedbackSessions;
    }

    public String getFsName() {
        return fsName;
    }

    public List<ElementTag> getCoursesSelectField() {
        return coursesSelectField;
    }
}
