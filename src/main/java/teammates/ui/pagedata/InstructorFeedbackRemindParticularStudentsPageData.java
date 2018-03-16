package teammates.ui.pagedata;

import java.util.Comparator;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.datatransfer.attributes.AccountAttributes;

public class InstructorFeedbackRemindParticularStudentsPageData extends PageData {
    private FeedbackSessionResponseStatus responseStatus;
    private String courseId;
    private String fsName;

    public InstructorFeedbackRemindParticularStudentsPageData(
                AccountAttributes account, String sessionToken, FeedbackSessionResponseStatus responseStatus,
                String courseId, String fsName) {
        super(account, sessionToken);
        this.responseStatus = responseStatus;
        this.courseId = courseId;
        this.fsName = fsName;
        this.sortStudentNamesListsInResponseStatus();
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFsName() {
        return fsName;
    }

    public FeedbackSessionResponseStatus getResponseStatus() {
        return responseStatus;
    }

    /**
     * sorts the students in the responseStatus (both who responded and with no response).
     * The sort is by Team then by student name.
     */
    private void sortStudentNamesListsInResponseStatus() {
        Map<String, String> teams = this.responseStatus.getEmailTeamNameTable();
        Comparator<String> studentsComparator = (student1, student2) -> {
            String student1Team = teams.getOrDefault(student1, "");
            String student2Team = teams.getOrDefault(student2, "");
            if (student1Team.equals(student2Team)) {
                return student1.compareTo(student2);
            } else {
                return student1Team.compareTo(student2Team);
            }
        };

        responseStatus.noResponse.sort(studentsComparator);
        responseStatus.studentsWhoResponded.sort(studentsComparator);
    }
}
