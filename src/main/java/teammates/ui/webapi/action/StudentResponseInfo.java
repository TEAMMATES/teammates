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
public class StudentResponseInfo {

    /**
     * The feedback session response status for a student.
     */
    public static class StudentResponseStatus extends ApiOutput {
        String email;
        String name;
        String sectionName;
        String teamName;
        boolean responseStatus;

        StudentResponseStatus(String email, String name, String sectionName, String teamName, boolean responseStatus) {
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
    public static class StudentsResponseStatus extends ApiOutput {

        private List<StudentResponseStatus> studentsResponseStatus;

        StudentsResponseStatus(FeedbackSessionResponseStatus responseStatus) {
            studentsResponseStatus = new ArrayList<>();

            Map<String, Boolean> allStudentsResponse = responseStatus.studentsWhoResponded.stream()
                    .collect(Collectors.toMap(studentEmail -> studentEmail, response -> true));
            allStudentsResponse.putAll(responseStatus.studentsWhoDidNotRespond.stream()
                    .collect(Collectors.toMap(studentEmail -> studentEmail, response -> false)));

            allStudentsResponse.forEach((studentEmail, response) -> {
                studentsResponseStatus.add(new StudentResponseStatus(
                        studentEmail, responseStatus.emailNameTable.get(studentEmail),
                        responseStatus.emailSectionTable.get(studentEmail),
                        responseStatus.emailTeamNameTable.get(studentEmail), response));
            });
        }

        public List<StudentResponseStatus> getStudentsResponseStatus() {
            return studentsResponseStatus;
        }
    }
}
