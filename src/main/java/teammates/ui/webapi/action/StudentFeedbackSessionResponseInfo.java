package teammates.ui.webapi.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Data transfer objects for {@link FeedbackSessionResponseStatus} between controller and HTTP.
 */
public class StudentFeedbackSessionResponseInfo {

    /**
     * The feedback session response status for a student.
     */
    public static class StudentFeedbackSessionResponseStatus extends ApiOutput {
        String email;
        String name;
        String sectionName;
        String teamName;
        boolean responseStatus;

        StudentFeedbackSessionResponseStatus(String email, String name, String sectionName, String teamName,
                                             boolean responseStatus) {
            this.email = email;
            this.name = name;
            this.sectionName = sectionName;
            this.teamName = teamName;
            this.responseStatus = responseStatus;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getSectionName() {
            return sectionName;
        }

        public String getTeamName() {
            return teamName;
        }

        public boolean getResponseStatus() {
            return responseStatus;
        }

    }

    /**
     * The feedback session response status for all students.
     */
    public static class StudentsFeedbackSessionResponseStatus extends ApiOutput {

        private List<StudentFeedbackSessionResponseStatus> studentsFeedbackSessionResponseStatus;

        StudentsFeedbackSessionResponseStatus(FeedbackSessionResponseStatus responseStatus) {
            studentsFeedbackSessionResponseStatus = new ArrayList<>();

            Map<String, Boolean> allStudentsResponse = responseStatus.studentsWhoResponded.stream()
                    .collect(Collectors.toMap(studentEmail -> studentEmail, response -> true));
            allStudentsResponse.putAll(responseStatus.studentsWhoDidNotRespond.stream()
                    .collect(Collectors.toMap(studentEmail -> studentEmail, response -> false)));

            allStudentsResponse.forEach((studentEmail, response) -> {
                studentsFeedbackSessionResponseStatus.add(new StudentFeedbackSessionResponseStatus(
                        studentEmail, responseStatus.emailNameTable.get(studentEmail),
                        responseStatus.emailSectionTable.get(studentEmail),
                        responseStatus.emailTeamNameTable.get(studentEmail), response));
            });
        }

        public List<StudentFeedbackSessionResponseStatus> getStudentsFeedbackSessionResponseStatus() {
            return studentsFeedbackSessionResponseStatus;
        }
    }
}
